# Android Clean Architecture Implementation Guide

A complete reference for implementing Clean Architecture in Android using Kotlin, Jetpack Compose, Hilt, Coroutines/Flow, Retrofit, and Type-Safe Navigation.

---

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Network Layer](#network-layer)
   - [API Client Configuration](#api-client-configuration)
   - [Request Header Interceptor](#request-header-interceptor)
   - [Token Refresh & Authenticator](#token-refresh--authenticator)
   - [HTTP Exception Mapping](#http-exception-mapping)
   - [Sealed Response Classes](#sealed-response-classes)
   - [NetworkResponseHandler](#networkresponsehandler)
3. [Data Layer](#data-layer)
   - [Data Source Interface](#data-source-interface)
   - [Data Source Implementation](#data-source-implementation)
   - [Repository Interface & Implementation](#repository-interface--implementation)
   - [RepositoryResponseHandler](#repositoryresponsehandler)
4. [Domain Layer](#domain-layer)
   - [Type Hierarchy Interfaces](#type-hierarchy-interfaces)
   - [AppUseCase Base Classes](#appusecase-base-classes)
   - [Use Case Implementation](#use-case-implementation)
5. [Dependency Injection (Hilt)](#dependency-injection-hilt)
   - [Dispatcher Annotations](#dispatcher-annotations)
   - [Network Module](#network-module)
   - [Repository Module (Binds)](#repository-module-binds)
   - [DataSource Module](#datasource-module)
6. [ViewModel Layer](#viewmodel-layer)
   - [BaseViewModel](#baseviewmodel)
   - [Feature ViewModel](#feature-viewmodel)
   - [ValueWithError (Form State)](#valuewitherror-form-state)
   - [Form State Class Pattern](#form-state-class-pattern)
7. [Navigation](#navigation)
   - [Route Definitions](#route-definitions)
   - [Navigation Composable Utilities](#navigation-composable-utilities)
   - [AppNavHost](#appnavhost)
   - [Modular NavGraph Extensions](#modular-navgraph-extensions)
   - [Nested Navigation Graphs](#nested-navigation-graphs)
8. [Main Activity Setup](#main-activity-setup)
9. [Compose Theme System](#compose-theme-system)
   - [Theme Object & CompositionLocals](#theme-object--compositionlocals)
   - [Color System](#color-system)
   - [Typography](#typography)
   - [Dimensions](#dimensions)
   - [ThemeSideEffect](#themesideeffect)
10. [Compose Utility Primitives](#compose-utility-primitives)
    - [StringWrapper](#stringwrapper)
    - [ThrottleClick Modifier](#throttleclick-modifier)
    - [Shimmer Modifier & Brush](#shimmer-modifier--brush)
    - [ModifierExtensions (dashedBorder)](#modifierextensions-dashedborder)
    - [Type Aliases](#type-aliases)
11. [Core UI Elements](#core-ui-elements)
    - [AppIcon](#appicon)
    - [LoadingIndicator (3 Dots)](#loadingindicator-3-dots)
    - [Spacers](#spacers)
    - [PowerplayChip](#powerplaychip)
12. [Composite Components](#composite-components)
    - [TopBar](#topbar)
    - [Button](#button)
    - [TextField](#textfield)
    - [NetworkErrorView](#networkerrorview)
    - [CommonDialogContent](#commondialogcontent)
    - [BottomButtons Footer](#bottombuttons-footer)
    - [ChipGroup](#chipgroup)
    - [OptionCard](#optioncard)
13. [Screen Scaffold Pattern](#screen-scaffold-pattern)
14. [Paging (Pagination)](#paging-pagination)
15. [Singleton Builder Utility](#singleton-builder-utility)

---

## Architecture Overview

```
app
 └── workflow/          ← Navigation wiring, MainActivity, feature Screens + ViewModels
 └── ui-compose/        ← Design system: theme, reusable Compose components
 └── ui/                ← Legacy XML UI base classes (Fragment, BottomSheet, Activity)
 └── core/              ← Network, DI, base classes, data sources, repositories
```

**Data flow:**

```
UI (Composable) → ViewModel → UseCase → Repository → DataSource → Retrofit/Apollo
                                                                        ↓
UI ← ViewModel ← UIResponse ← Flow<UIResponse> ← NetworkResponse ← Response<T>
```

---

## Network Layer

### API Client Configuration

Environment-aware base URL. Release builds use BuildConfig constants; debug/staging reads from SharedPreferences (for runtime switching).

```kotlin
object ApiClient {
    val BASE_URL: String by lazy {
        if (BuildConfig.BUILD_TYPE != "release")
            SharedPreferences.baseUrl
        else BuildConfig.BACKEND_URL
    }

    val BASE_GRAPHQL_URL: String by lazy {
        if (BuildConfig.BUILD_TYPE != "release")
            SharedPreferences.graphQLUrl
        else BuildConfig.GRAPHQL_BACKEND_URL
    }
}
```

### Request Header Interceptor

Injects auth token, platform, locale, org/project IDs, and app version into every request. Uses custom annotation-based routing to inject query parameters.

```kotlin
@Singleton
class RequestHeaderInterceptor @Inject constructor(
    private val projectUserCacheRepository: ProjectAndUserCacheRepository,
    @Annotations.IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = runBlocking(ioDispatcher) { fetchProjectAndUserPreferences() }

        val userToken = prefs?.userSession?.userToken.toNullIfEmpty
        val projectId = prefs?.projectSession?.projectId.toNullIfEmpty
        val orgId = prefs?.orgId.toNullIfEmpty

        val requestBuilder = chain.request().newBuilder().apply {
            addHeader("Accept-Encoding", "identity")
            addHeader("x-platform", "and")
            addHeader("language", LocaleHelper.systemLocale.language)
            addHeader("app-version", "${BuildConfig.VERSION_CODE}")
            addHeader("accept-language", LocaleHelper.systemLocale.language)
            addHeader("country", "IN")
            addHeader("User-Agent", "AppName/${BuildConfig.VERSION_CODE} (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})")
            userToken?.let { addHeader("authorization", "Bearer $it") }
            orgId?.let { addHeader("x-apm-org_id", it) }
            projectId?.let { addHeader("x-apm-project_id", it) }
        }

        // Annotation-driven query param injection
        computeAnnotationDrivenParams(requestBuilder, chain.request(), projectId, orgId)
        return chain.proceed(requestBuilder.build())
    }

    private fun computeAnnotationDrivenParams(
        requestBuilder: Request.Builder,
        request: Request,
        projectId: String?,
        orgId: String?
    ): Request.Builder {
        val method = request.tag(Invocation::class.java)?.method()
        method?.let {
            val newUrl = request.url.newBuilder()
            it.annotations.forEach { annotation ->
                when (annotation) {
                    is Annotations.RequiresProjectIdQuery ->
                        projectId?.let { id -> newUrl.addQueryParameter(annotation.queryNameKey, id) }
                    is Annotations.RequiresOrgIdQuery ->
                        orgId?.let { id -> newUrl.addQueryParameter(annotation.queryNameKey, id) }
                }
            }
            requestBuilder.url(newUrl.build())
        }
        return requestBuilder
    }

    private suspend fun fetchProjectAndUserPreferences(): ProjectAndUserPreferences? =
        suspendCancellableCoroutine { cont ->
            CoroutineScope(ioDispatcher).launch {
                try {
                    cont.resume(projectUserCacheRepository.getProjectUserPreferenceAsync())
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
                cont.invokeOnCancellation { cancel() }
            }
        }
}
```

**Custom annotation for Retrofit method-level query injection:**

```kotlin
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequiresProjectIdQuery(val queryNameKey: String = "project_id")

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RequiresOrgIdQuery(val queryNameKey: String = "org_id")

// Usage on Retrofit interface:
@RequiresProjectIdQuery
@GET("v1/some-endpoint")
suspend fun getSomething(): Response<SomeResponse>
```

---

### Token Refresh & Authenticator

OkHttp `Authenticator` handles 401 responses. On success, retries the original request with the new token. On failure (expired refresh token), clears session and restarts the app.

```kotlin
class AppAuthenticator @Inject constructor(
    private val refreshTokenUseCaseProvider: Provider<RefreshAuthTokenUseCase>,
    private var signOutUseCase: SignOutWorkUseCase,
    private var cacheRepository: ProjectAndUserCacheRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            if (response.request.url.toString().contains(REFRESH_TOKEN_ENDPOINT)
                && response.code == 401 || refreshToken.isEmpty()
            ) {
                onAuthFailure(isRefreshTokenExpired = true)
                return@runBlocking null
            }

            var newRequest: Request? = null
            refreshTokenUseCaseProvider.get()(RefreshAuthTokenRequest(refreshToken)).collect { result ->
                when (result) {
                    is UIResponse.Success -> {
                        cacheRepository.setUserTokenAsync(result.data.authToken)
                        refreshToken = result.data.refreshToken
                        newRequest = response.request.newBuilder()
                            .header("authorization", "Bearer ${result.data.authToken}")
                            .build()
                    }
                    is UIResponse.Error -> {
                        onAuthFailure(false)
                        newRequest = null
                    }
                    is UIResponse.Loading -> Unit
                }
            }
            newRequest
        }
    }

    private suspend fun onAuthFailure(isRefreshTokenExpired: Boolean) {
        cacheRepository.clearUserTokenInBackground()
        signOutUseCase.process()
        // Restart the app to the login screen
        val ctx = App.applicationContext
        val intent = ctx.packageManager.getLaunchIntentForPackage(ctx.packageName)
        ctx.startActivity(Intent.makeRestartActivityTask(intent?.component))
        exitProcess(0)
    }

    companion object {
        const val REFRESH_TOKEN_ENDPOINT = "v1/auth/refresh-token"
    }
}
```

> **Key point:** Use `Provider<UseCase>` injection instead of direct injection to avoid circular dependencies in Dagger/Hilt.

---

### HTTP Exception Mapping

Enum mapping HTTP codes to UI presentation strategy.

```kotlin
@Keep
enum class HttpException(
    val code: Int,
    var message: String = "Something went wrong",
    var errorList: List<ErrorItem>? = null,
    var metaData: HashMap<String, Any>? = null
) {
    OK(200),
    NOT_FOUND(404, "No content found"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    NO_INTERNET_CONNECTION(600, "No internet connection"),
    SOCKET_TIMEOUT(623, "Request timed out"),
    GENERAL_ERROR(626, "Something went wrong");

    fun getErrorType(): NetworkErrorType = when (code) {
        NOT_FOUND.code        -> NetworkErrorType.NO_CONTENT_FOUND
        in 400..499           -> NetworkErrorType.CLIENT_ERROR
        in 500..599           -> NetworkErrorType.SERVER_ERROR
        NO_INTERNET_CONNECTION.code -> NetworkErrorType.NO_INTERNET
        SOCKET_TIMEOUT.code   -> NetworkErrorType.TIME_OUT
        else                  -> NetworkErrorType.GENERAL_ERROR
    }

    enum class NetworkErrorType(val viewType: NetworkErrorViewType, val preDefinedMsg: String) {
        NO_CONTENT_FOUND(NetworkErrorViewType.FULL_SCREEN, NOT_FOUND.message),
        CLIENT_ERROR(NetworkErrorViewType.TOAST, GENERAL_ERROR.message),
        SERVER_ERROR(NetworkErrorViewType.TOAST, INTERNAL_SERVER_ERROR.message),
        NO_INTERNET(NetworkErrorViewType.TOAST, NO_INTERNET_CONNECTION.message),
        TIME_OUT(NetworkErrorViewType.TOAST, SOCKET_TIMEOUT.message),
        GENERAL_ERROR(NetworkErrorViewType.TOAST, GENERAL_ERROR.message),
        NONE(NetworkErrorViewType.NONE, "")
    }

    enum class NetworkErrorViewType {
        FULL_SCREEN, TOAST, SNACK_BAR, NONE
    }

    data class NetworkErrorUI(
        val errorType: NetworkErrorType,
        val errorInfo: ErrorCodeMsg
    ) {
        companion object {
            fun getSuccess() = NetworkErrorUI(NetworkErrorType.NONE, ErrorCodeMsg(OK.code, ""))
            fun getGeneralError() = NetworkErrorUI(NetworkErrorType.NONE, ErrorCodeMsg(GENERAL_ERROR.code, ""))
            fun getNoNetworkError() = NetworkErrorUI(
                NetworkErrorType.NO_INTERNET,
                ErrorCodeMsg(NO_INTERNET_CONNECTION.code, NO_INTERNET_CONNECTION.message)
            )
        }
    }

    data class ErrorCodeMsg(
        val code: Int,
        val msg: String,
        val metaData: HashMap<String, Any>? = null
    )
}
```

---

### Sealed Response Classes

**NetworkResponse** — raw API result (repository/data-source layer):

```kotlin
sealed class NetworkResponse<out O> {
    data class Success<out O>(val data: O) : NetworkResponse<O>()
    data class ApiError<out O>(
        val code: Int,
        val message: String?,
        val errorList: ArrayList<ErrorItem>? = null,
        val metaData: HashMap<String, Any>? = null
    ) : NetworkResponse<O>()
    data class NetworkError<out O>(val error: IOException) : NetworkResponse<O>()
    data class UnknownError<out O>(val throwable: Throwable) : NetworkResponse<O>()
    data class SocketTimeout<out O>(
        val type: TimeoutErrorType? = null,
        val error: SocketTimeoutException
    ) : NetworkResponse<O>()
}
```

**UIResponse** — UI-ready state (ViewModel/UI layer):

```kotlin
@Keep
sealed class UIResponse<out O> {
    data class Success<out O>(val data: O) : UIResponse<O>()
    data class Error<out O>(val error: HttpException.NetworkErrorUI) : UIResponse<O>()
    data class Loading<out O>(
        val data: O? = null,                          // optional stale data during refresh
        val loaderType: LoadingState = LoadingState.NONE
    ) : UIResponse<O>()

    val getOrNull: O? get() = (this as? Success)?.data
    val getError: HttpException.NetworkErrorUI
        get() = (this as? Error)?.error ?: HttpException.NetworkErrorUI.getGeneralError()
    val isLoading: Boolean get() = this is Loading
}

// Conversion extension
fun <T> NetworkResponse<T>.getUIResponse(): UIResponse<T> = when (this) {
    is NetworkResponse.Success -> UIResponse.Success(data)
    else -> UIResponse.Error(buildNetworkErrorUI())   // buildNetworkErrorUI() maps error codes
}
```

---

### NetworkResponseHandler

Central handler for Retrofit and Apollo API calls. Executes on the provided dispatcher and catches all error types.

```kotlin
class NetworkResponseHandler(private val dispatcher: CoroutineDispatcher) {

    /** REST (Retrofit) */
    suspend fun <T : Any> handleApi(
        timeoutErrorType: TimeoutErrorType = TimeoutErrorType.TOAST,
        execute: suspend () -> Response<T>
    ): NetworkResponse<T> = withContext(dispatcher) {
        try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                NetworkResponse.Success(body)
            } else {
                val error = response.errorBody()?.let {
                    Gson().fromJson(it.charStream(), ErrorResponse::class.java)
                }
                NetworkResponse.ApiError(
                    code = response.code(),
                    message = error?.errorDetail?.firstOrNull() ?: error?.message,
                    errorList = error?.errorList,
                    metaData = error?.metaData
                )
            }
        } catch (e: Throwable) {
            e.toNetworkResponse(timeoutErrorType)  // extension maps IOException, SocketTimeout, etc.
        }
    }

    /** GraphQL (Apollo) */
    suspend fun <T : Operation.Data, M : AppData> handleGraphQLAPI(
        timeoutErrorType: TimeoutErrorType = TimeoutErrorType.TOAST,
        castedClassName: Class<M>,
        execute: suspend () -> ApolloResponse<T>
    ): NetworkResponse<M> = withContext(dispatcher) {
        try {
            val response = execute()
            val body = response.data
            if (!response.hasErrors() && body != null) {
                NetworkResponse.Success(body.mapModelAToModelB(castedClassName))
            } else {
                NetworkResponse.ApiError(
                    code = HttpException.INTERNAL_SERVER_ERROR.code,
                    message = response.errors?.firstOrNull()?.message,
                    errorList = null
                )
            }
        } catch (e: Throwable) {
            when (e) {
                is SocketTimeoutException -> NetworkResponse.SocketTimeout(timeoutErrorType, e)
                is IOException            -> NetworkResponse.NetworkError(e)
                is ApolloHttpException    -> NetworkResponse.ApiError(e.statusCode, e.message, null)
                else                      -> NetworkResponse.UnknownError(e)
            }
        }
    }
}
```

---

## Data Layer

### Data Source Interface

```kotlin
// All methods are suspend, return NetworkResponse<T>
internal interface UserDataSource {
    suspend fun getUser(): NetworkResponse<UserData>
    suspend fun getUserProfile(): NetworkResponse<UserData>
    suspend fun requestOtp(request: SendOtpRequest): NetworkResponse<SendOtpResponse>
    suspend fun verifyOtp(request: VerifyOtpRequest): NetworkResponse<VerifyOtpResponse>
}
```

### Data Source Implementation

```kotlin
@Singleton
internal class UserDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
    private val networkResponseHandler: NetworkResponseHandler
) : UserDataSource {

    override suspend fun getUser(): NetworkResponse<UserData> =
        networkResponseHandler.handleApi { userApi.getUser() }

    override suspend fun requestOtp(request: SendOtpRequest): NetworkResponse<SendOtpResponse> =
        networkResponseHandler.handleApi { userApi.requestOtp(request) }
}
```

**Retrofit API interface:**

```kotlin
interface UserApi {
    @GET("v1/user/me")
    suspend fun getUser(): Response<UserData>

    @POST("v1/auth/otp/request")
    suspend fun requestOtp(@Body request: SendOtpRequest): Response<SendOtpResponse>
}
```

---

### Repository Interface & Implementation

```kotlin
// Interface — in domain layer
interface UserRepository {
    fun getUser(): Flow<UIResponse<User>>
    fun requestOtp(request: SendOtpRequest): Flow<UIResponse<SendOtpResponse>>
    fun verifyOtp(request: VerifyOtpRequest): Flow<UIResponse<VerifyOtpResponse>>
}

// Implementation — in data layer
internal class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val repositoryResponseHandler: RepositoryResponseHandler
) : UserRepository {

    override fun getUser(): Flow<UIResponse<User>> =
        repositoryResponseHandler.getUIStateAsFlow {
            userDataSource.getUser().map { it.toUIData() as User }
        }

    override fun requestOtp(request: SendOtpRequest): Flow<UIResponse<SendOtpResponse>> =
        repositoryResponseHandler.getUIStateAsFlow {
            userDataSource.requestOtp(request)
        }
}
```

> **`.map { it.toUIData() }`** — extension function on `NetworkResponse<NetworkData>` that maps the DTO to a domain entity.

---

### RepositoryResponseHandler

Wraps data-source calls into `Flow<UIResponse<T>>`, emitting `Loading` first then `Success` or `Error`.

```kotlin
class RepositoryResponseHandler @Inject constructor() {

    /** Single result */
    fun <O> getUIStateAsFlow(
        initial: (suspend () -> O?)? = null,       // optional cached data for optimistic UI
        execute: suspend () -> NetworkResponse<O>
    ): Flow<UIResponse<O>> = flow {
        emit(UIResponse.Loading(data = initial?.invoke()))
        emit(execute().getUIResponse())
    }

    /** Paging — custom response shape */
    fun <I : AppData, O : AppData> getPagerAsFlow(
        startingPageIndex: Int = 1,
        transformOnSuccess: suspend (I) -> List<O>,
        source: suspend (Int) -> NetworkResponse<I>,
    ): Flow<PagingData<O>> = PagingUtils<Int>().flow {
        WrappedPagingSource(startingPageIndex, transformOnSuccess, source)
    }

    /** Paging — list response */
    fun <I : AppData> getPagerAsFlow(
        startingPageIndex: Int = 1,
        source: suspend (Int) -> NetworkResponse<List<I>>,
    ): Flow<PagingData<I>> = PagingUtils<Int>().flow {
        ListPagingSource(startingPageIndex, source)
    }
}
```

---

## Domain Layer

### Type Hierarchy Interfaces

Marker interfaces enforce compile-time contracts between layers.

```kotlin
sealed interface BaseData

interface ResponseData : BaseData    // raw network/local data
interface NetworkData : ResponseData
interface LocalData : ResponseData

interface AppData : BaseData         // domain/UI data
interface UIData : AppData           // data safe for direct display
interface UINetworkData : AppData {  // network data that can produce UIData
    fun getUiData(): UIData = AnyUIData()
}

interface PagingAppData : AppData {
    val paginationAPIResult: MutableLiveData<HttpException>
}
```

**Rule:** DTOs implement `NetworkData`. Domain entities implement `UIData`. Repository maps `NetworkData → UIData`.

---

### AppUseCase Base Classes

Sealed hierarchy provides typed operator-fun `invoke` for consistent use-case contracts.

```kotlin
sealed class AppUseCase {

    // Single object in, single object out
    abstract class WithInput<I : AppData, O : UINetworkData> : AppUseCase() {
        abstract operator fun invoke(input: I): Flow<UIResponse<O>>
    }

    // No input, single object out
    abstract class WithNoInput<O : UIData> : AppUseCase() {
        abstract operator fun invoke(): Flow<UIResponse<O>>
    }

    // String input shorthand
    abstract class WithStringInput<O : UINetworkData> : AppUseCase() {
        abstract operator fun invoke(input: String): Flow<UIResponse<O>>
    }

    // Numeric input shorthand
    abstract class WithNumberInput<O : UINetworkData> : AppUseCase() {
        abstract operator fun invoke(input: Number): Flow<UIResponse<O>>
    }

    // List variants
    sealed class ListData {
        abstract class WithInput<I : AppData, O : UINetworkData> : AppUseCase() {
            abstract operator fun invoke(input: I): Flow<UIResponse<List<O>>>
        }
        abstract class WithNoInput<O : UINetworkData> : AppUseCase() {
            abstract operator fun invoke(): Flow<UIResponse<List<O>>>
        }
    }

    // Paging variants
    sealed class Paging {
        abstract class WithInput<I : AppData, O : UINetworkData> : AppUseCase() {
            abstract operator fun invoke(input: I): Flow<PagingData<O>>
        }
        abstract class WithNoInput<O : UINetworkData> : AppUseCase() {
            abstract operator fun invoke(): Flow<PagingData<O>>
        }
    }
}
```

### Use Case Implementation

```kotlin
internal class RequestOtpUseCase @Inject constructor(
    private val userRepository: UserRepository
) : AppUseCase.WithInput<SendOtpRequest, SendOtpResponse>() {

    override fun invoke(input: SendOtpRequest): Flow<UIResponse<SendOtpResponse>> =
        userRepository.requestOtp(input)
}
```

---

## Dependency Injection (Hilt)

### Dispatcher Annotations

```kotlin
object Annotations {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class IoDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class MainDispatcher

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class DefaultDispatcher
}
```

### Network Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Annotations.IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideNetworkResponseHandler(
        @Annotations.IoDispatcher dispatcher: CoroutineDispatcher
    ): NetworkResponseHandler = NetworkResponseHandler(dispatcher)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        requestHeaderInterceptor: RequestHeaderInterceptor,
        authenticator: AppAuthenticator
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(requestHeaderInterceptor)
        .authenticator(authenticator)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiClient.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)
}
```

### Repository Module (Binds)

```kotlin
// Use @Binds abstract module to map interface → implementation
@Module
@InstallIn(ViewModelComponent::class)
internal abstract class UserRepositoryModule {

    @Binds
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```

### DataSource Module

```kotlin
@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserDataSourceModule {

    @Binds
    @Singleton
    abstract fun bindUserDataSource(
        impl: UserDataSourceImpl
    ): UserDataSource
}
```

---

## ViewModel Layer

### BaseViewModel

Provides master error handling, loading state, and extension functions for API result processing.

```kotlin
abstract class BaseViewModel : ViewModel() {

    // Global error broadcast (observed by Activity/Screen to show toasts/snackbars)
    private val _masterNetworkError = MutableLiveData(HttpException.NetworkErrorUI.getSuccess())
    val masterNetworkErrorLiveData: LiveData<HttpException.NetworkErrorUI> = _masterNetworkError
    val masterNetworkErrorFlow: Flow<HttpException.NetworkErrorUI> = _masterNetworkError.asFlow()

    // Loading state (LiveData for XML, Compose State for Compose)
    val loadingState = MutableLiveData(LoadingState.NONE)
    val loadingStateCompose = mutableStateOf(LoadingState.NONE)

    // Button is disabled while loading
    val isButtonEnabled: LiveData<Boolean> = loadingState.switchMap { state ->
        liveData { emit(state == LoadingState.NONE) }
    }

    // One-shot guard: ensures initial API calls are only made once per ViewModel instance
    private var isInitialCallMade = false
    fun initialApiCalls(block: (() -> Unit)?) {
        if (isInitialCallMade || block == null) return
        block()
        isInitialCallMade = true
    }

    // Convenience wrapper: runs a UseCase with loading state management
    protected inline fun <O : AppData> fetchData(
        useCase: BaseUseCase<O>,
        loaderType: LoadingState? = LoadingState.LOADING,
        crossinline response: (res: O) -> Unit
    ) {
        loadingState.postValue(loaderType)
        viewModelScope.launch {
            val result = useCase.process()
            response(result)
            loadingState.postValue(LoadingState.NONE)
        }
    }

    // Extension on NetworkResponse: routes success/error to callbacks
    fun <T> NetworkResponse<T>.callV2(
        onSuccess: (T) -> Unit,
        onCustomError: ((HttpException.NetworkErrorUI) -> Unit)? = null,
        onFailureWithMasterError: ((HttpException.NetworkErrorUI) -> Unit)? = null,
    ) {
        when (val call = getUIResponse()) {
            is UIResponse.Success -> {
                onSuccess(call.data)
                _masterNetworkError.postValue(HttpException.NetworkErrorUI.getSuccess())
            }
            is UIResponse.Error -> when {
                onCustomError != null -> onCustomError(call.error)
                onFailureWithMasterError != null -> {
                    onFailureWithMasterError(call.error)
                    _masterNetworkError.postValue(call.error)
                }
                else -> _masterNetworkError.postValue(call.error)
            }
            is UIResponse.Loading -> Unit
        }
    }
}
```

### Feature ViewModel

```kotlin
@HiltViewModel
internal class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val requestOtpUseCase: RequestOtpUseCase,
) : ViewModel() {

    // Extract typed route arguments from SavedStateHandle
    val args = savedStateHandle.toRoute<OrgRoutes.UserProfile>()

    // Manual refresh trigger
    private val _refresh = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_LATEST)

    fun refresh() { _refresh.tryEmit(Unit) }

    // State derived from refresh + usecase
    val state = _refresh
        .onStart { emit(Unit) }              // trigger initial load
        .flatMapLatest {
            getUserProfileUseCase().map { response ->
                when (response) {
                    is UIResponse.Success -> UIResponse.Success(mapToProfileState(response.data))
                    is UIResponse.Loading -> UIResponse.Loading()
                    is UIResponse.Error   -> UIResponse.Error(response.error)
                }
            }
        }
        // No need to stateIn if only collected from Compose with collectAsStateWithLifecycle

    // OTP countdown timer (restartable via SharedFlow trigger)
    private val _restartTimer = MutableSharedFlow<Unit>()
    private val startTime = 60

    val timerFlow = _restartTimer.flatMapLatest {
        flow {
            for (i in startTime downTo 0) {
                emit(i)
                delay(1.seconds)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, startTime)

    val isResendEnabled = timerFlow.map { it == 0 }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // Boolean loading states for granular UI control
    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting = _isSubmitting.asStateFlow()

    fun submitOtp(otp: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            // ...call use case...
            _isSubmitting.value = false
        }
    }
}
```

---

### ValueWithError (Form State)

Reactive form field combining `mutableStateOf` value + error with optional validator.

```kotlin
class ValueWithError<VALUE, ERROR>(
    initial: VALUE,
    private val initialError: ERROR,        // used as sentinel for "no error"
) {
    private val _value = mutableStateOf(initial)
    private val _error = mutableStateOf(initialError)

    val value get() = _value.value
    var error
        get() = _error.value
        set(value) { _error.value = value }

    val isValid get() = _error.value == initialError

    private var onValueChange: ((VALUE) -> Unit)? = null
    fun setOnValueChange(fn: (VALUE) -> Unit) { onValueChange = fn }

    private var validate: ((VALUE) -> ERROR)? = null
    fun setValidator(validator: (VALUE) -> ERROR) { validate = validator }

    /** Explicit validation pass (e.g. on form submit) */
    fun validate(allowEmpty: Boolean = false): Boolean {
        if (allowEmpty && (value == null || value.toString().isBlank())) {
            _error.value = initialError
            return true
        }
        _error.value = validate?.invoke(_value.value) ?: initialError
        return isValid
    }

    /** Sets value and immediately validates */
    fun setValue(newValue: VALUE) {
        _value.value = newValue
        onValueChange?.invoke(newValue)
        _error.value = validate?.invoke(_value.value) ?: initialError
    }
}
```

### Form State Class Pattern

```kotlin
internal data class EditProfileState(
    val initialName: String,
    val initialEmail: String?,
    val initialPhone: String,
) {
    // Each editable field is a ValueWithError initialized in the constructor body
    val name = ValueWithError<String, String?>(initialName, null).apply {
        setValidator { value -> "Cannot be empty".takeIf { value.isBlank() } }
    }

    val email = ValueWithError<String?, String?>(initialEmail, null).apply {
        setValidator { value ->
            "Invalid email".takeUnless { value.isNullOrBlank() || value.matches(EMAIL_REGEX) }
        }
    }

    val phone = ValueWithError<String?, String?>(initialPhone, null).apply {
        setValidator { value ->
            "Invalid phone".takeUnless { value?.matches(PHONE_REGEX) == true }
        }
    }

    /** Validate all fields at once — returns true if all pass */
    fun validateAll(): Boolean = listOf(
        name.validate(),
        email.validate(allowEmpty = true),
        phone.validate()
    ).all { it }

    /** Dirty check */
    fun hasChanges(): Boolean = initialName != name.value || initialEmail != email.value
}
```

**Usage in Composable:**

```kotlin
@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is UIResponse.Loading -> LoadingView()
        is UIResponse.Error   -> ErrorView(state.error) { viewModel.refresh() }
        is UIResponse.Success -> {
            val form = state.data
            AppTextField(
                value = form.name.value,
                error = form.name.error,
                onValueChanged = { form.name.setValue(it) }
            )
        }
    }
}
```

---

## Navigation

### Route Definitions

Use `@Serializable` sealed interfaces for type-safe routes. Arguments are data class fields.

```kotlin
// Root sealed interface — all routes implement AppRoute
sealed interface AppRoute

// Group by feature in sealed interfaces
sealed interface CommonRoutes : AppRoute {

    @Serializable
    @Keep
    data class Authentication(
        val deepLink: String? = null
    ) : AppRoute

    @Serializable
    @Keep
    data class Onboarding(
        val deepLink: String? = null
    ) : AppRoute

    // Deep link route with factory method
    @Serializable
    @Keep
    data class DeepLink(val deepLink: String) : AppRoute {
        companion object {
            fun generate(vararg paths: String, params: Map<String, String> = emptyMap()): DeepLink {
                val path = paths.joinToString("/")
                val query = params.entries.joinToString("&") { (k, v) -> "$k=$v" }
                return DeepLink("https://app.example.com/$path?$query")
            }
        }
    }

    // In-app deep link (custom scheme)
    @Serializable
    @Keep
    data class InAppDeepLink(val deepLink: String) : AppRoute {
        companion object {
            fun generate(vararg paths: String, params: Map<String, String> = emptyMap()): InAppDeepLink {
                val path = paths.joinToString("/")
                val query = params.entries.joinToString("&") { (k, v) -> "$k=$v" }
                return InAppDeepLink("myapp://$path?$query")
            }
        }
    }
}

// Feature routes — one file per feature group
sealed interface OrgRoutes : AppRoute {
    @Serializable @Keep data class OrgHomeTopRoute(val org: PartialOrg) : AppRoute
    @Serializable @Keep data class ProjectList(val org: PartialOrg) : AppRoute
    @Serializable @Keep data class UserProfile(val userId: String) : AppRoute
    @Serializable @Keep data class EditUserProfile : AppRoute
}
```

**Custom Parcelable/NavType for complex objects:**

```kotlin
// For passing non-primitive types as nav args, create a custom NavType
val PartialOrgNavType = object : NavType<PartialOrg>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String) =
        bundle.getString(key)?.let { Json.decodeFromString<PartialOrg>(it) }
    override fun parseValue(value: String) = Json.decodeFromString<PartialOrg>(value)
    override fun serializeAsValue(value: PartialOrg) = Json.encodeToString(value)
    override fun put(bundle: Bundle, key: String, value: PartialOrg) =
        bundle.putString(key, Json.encodeToString(value))
}

// typeMap companion on data class for convenience:
data class PartialOrg(/* ... */) {
    companion object {
        val typeMap = mapOf(typeOf<PartialOrg>() to PartialOrgNavType)
    }
}
```

---

### Navigation Composable Utilities

Custom `powerplayComposable` extension applies consistent fade transitions (50ms) across all screens and extracts typed route args automatically.

```kotlin
const val NAV_TRANSITION_DURATION = 50

val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(animationSpec = tween(NAV_TRANSITION_DURATION, easing = LinearEasing))
}

val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(NAV_TRANSITION_DURATION, easing = LinearEasing))
}

/** Typed route — auto-extracts args via toRoute<T>() */
inline fun <reified T : Any> NavGraphBuilder.appComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry, T) -> Unit
) {
    composable<T>(
        typeMap = typeMap,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = enterTransition,
        popExitTransition = exitTransition,
    ) {
        val args = it.toRoute<T>()
        content(it, args)
    }
}

/** Typed route — no args extraction needed */
inline fun <reified T : Any> NavGraphBuilder.appComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable<T>(
        typeMap = typeMap,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = enterTransition,
        popExitTransition = exitTransition,
        content = content
    )
}

/**
 * Navigate without growing back stack in circular patterns (A→B→A→B...).
 * Use for tab-style navigation or linked screens.
 */
fun NavController.navigateLinked(
    route: Any,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route) {
        popUpTo(route) { inclusive = false }
        launchSingleTop = true
        builder()
    }
}
```

---

### AppNavHost

Central NavHost with modular graph extensions and deep-link dialog registration.

```kotlin
@Composable
internal fun AppNavHost(startDestination: AppRoute) {
    val navController = LocalNavController.current

    LaunchedEffect(Unit) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("NavHost", "route => ${destination.route}")
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // In-app deep link dialog (non-dismissible)
        dialog<CommonRoutes.InAppDeepLink>(
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) { entry ->
            InAppDeepLinkDialog(viewModel = hiltViewModel(entry))
        }

        // Web deep link screen
        appComposable<CommonRoutes.DeepLink> { entry ->
            DeepLinkScreen(viewModel = hiltViewModel(entry))
        }

        // Feature nav groups (extension functions on NavGraphBuilder)
        navigationAuthentication()
        navigationOnboarding()
        navigationCommon()
        navigationOrgHome()
        navigationProject()
    }
}
```

---

### Modular NavGraph Extensions

Each feature module registers its own routes as extension functions on `NavGraphBuilder`.

```kotlin
// In auth module:
fun NavGraphBuilder.navigationAuthentication() {
    appComposable<CommonRoutes.Authentication> { _, args ->
        LoginScreen(deepLink = args.deepLink)
    }
    appComposable<AuthRoutes.ForgotPassword> { entry ->
        ForgotPasswordScreen(viewModel = hiltViewModel(entry))
    }
}

// In org module:
fun NavGraphBuilder.navigationOrgHome(modifier: Modifier = Modifier) {
    // Nested nav graph with startDestination
    navigation<OrgRoutes.OrgHomeTopRoute>(
        startDestination = OrgRoutes.ProjectList::class,
        typeMap = PartialOrg.typeMap
    ) {
        navigationProjectList(modifier)
    }

    navigationOrgDashboard(modifier)
    navigationSettings(modifier)

    // Bridge to Activity-based screen
    activity<OrgRoutes.ChatList>(typeMap = PartialOrg.typeMap) {
        activityClass = ChatListActivity::class
    }
}
```

### Nested Navigation Graphs

```kotlin
// Declare nested graph with typed top-level route and start destination
navigation<OrgRoutes.OrgHomeTopRoute>(
    startDestination = OrgRoutes.ProjectList::class,
    typeMap = PartialOrg.typeMap
) {
    appComposable<OrgRoutes.ProjectList>(typeMap = PartialOrg.typeMap) { entry, args ->
        ProjectListScreen(org = args.org, navController = LocalNavController.current)
    }
    // ... more destinations inside this graph
}
```

---

## Main Activity Setup

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var dateUtils: DateUtils
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        // Keep splash screen until start destination is determined
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.startDestination.value == null
        }

        // Sanitize deep-link intents (HTTPS only, known host)
        handleIntent(intent)

        setContent {
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
            startDestination?.let { destination ->
                navController = rememberNavController()
                AppTheme {
                    CompositionLocalProvider(
                        LocalActivity provides this@MainActivity,
                        LocalNavController provides navController,
                        LocalFragmentManager provides supportFragmentManager,
                        LocalDateUtils provides dateUtils,
                    ) {
                        AppNavHost(startDestination = destination)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Sanitize and restart with new intent for deep links
        val sanitized = IntentSanitizer.Builder()
            .allowData { it.scheme == "https" && it.host == getString(R.string.known_host) }
            .allowAction(Intent.ACTION_VIEW)
            .build()
            .sanitize(intent) { Log.w("Intent", "Sanitization failed: ${intent.dataString}") }
        finish()
        startActivity(sanitized)
    }
}
```

**LocalNavController CompositionLocal:**

```kotlin
val LocalNavController = compositionLocalOf<NavHostController> {
    error("No NavController provided")
}

val LocalActivity = compositionLocalOf<ComponentActivity> {
    error("No Activity provided")
}
```

---

## Compose Theme System

### Theme Object & CompositionLocals

```kotlin
object AppTheme {
    val typography: AppTypography
        @Composable @ReadOnlyComposable get() = LocalAppTypography.current

    val colorScheme: AppColorScheme
        @Composable @ReadOnlyComposable get() = LocalAppColorScheme.current

    val dimens: AppDimens
        @Composable @ReadOnlyComposable get() = LocalAppDimens.current

    val shapes: AppShapes
        @Composable @ReadOnlyComposable get() = LocalAppShapes.current
}

// CompositionLocal definitions
val LocalAppTypography = staticCompositionLocalOf { AppTypography }
val LocalAppColorScheme = staticCompositionLocalOf { AppLightColorScheme }
val LocalAppDimens = staticCompositionLocalOf { AppMobileDimens }
val LocalAppShapes = staticCompositionLocalOf { AppShapes }

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAppTypography provides AppTypography,
        LocalAppColorScheme provides AppLightColorScheme,
        LocalAppDimens provides AppMobileDimens,
        LocalAppShapes provides AppShapes,
        LocalTonalElevationEnabled provides false,
        LocalRippleConfiguration provides null       // disable Material ripples globally
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                background = AppTheme.colorScheme.surface.default,
                surface = AppTheme.colorScheme.surface.default,
                primary = AppTheme.colorScheme.brand,
            ),
            content = content
        )
    }
}
```

---

### Color System

Semantic color tokens organized into nested objects.

```kotlin
data class ContentColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val brand: Color,
    val critical: Color,
    val warning: Color,
    val success: Color,
)

data class SurfaceColors(
    val sunken: Color,
    val default: Color,
    val raised: Color,
    val overlay: Color,
    val brand: Color,
    val critical: Color,
    val warning: Color,
    val success: Color,
)

data class StrokeColors(
    val neutral: Color,
    val brand: Color,
    val critical: Color,
    val warning: Color,
    val success: Color,
)

data class AppColorScheme(
    val content: ContentColors,
    val surface: SurfaceColors,
    val stroke: StrokeColors,
    val brand: Color,
    val critical: Color,
    val warning: Color,
    val success: Color,
    val white: Color,
)

// Usage: AppTheme.colorScheme.content.primary
//        AppTheme.colorScheme.surface.sunken
//        AppTheme.colorScheme.stroke.neutral
```

---

### Typography

```kotlin
// Map Material3 TextStyle slots to custom design tokens
val AppTypography = Typography(
    displayLarge   = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Bold,   fontSize = 30.sp),
    headlineMedium = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    headlineSmall  = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
    titleLarge     = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    labelLarge     = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    labelMedium    = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium,   fontSize = 14.sp),
    labelSmall     = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Medium,   fontSize = 12.sp),
    bodyLarge      = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    bodyMedium     = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal,   fontSize = 14.sp),
    bodySmall      = TextStyle(fontFamily = AppFont, fontWeight = FontWeight.Normal,   fontSize = 12.sp),
)

// Usage: AppTheme.typography.labelLarge
```

---

### Dimensions

```kotlin
data class MarginTokens(
    val extraSmall: Dp,  // 4.dp
    val small: Dp,       // 8.dp
    val secondary: Dp,   // 10.dp
    val medium: Dp,      // 12.dp
    val tertiary: Dp,    // 14.dp
    val large: Dp,       // 16.dp
    val extraLarge: Dp,  // 24.dp
)

data class AppDimens(
    val margin: MarginTokens,
    val icon: IconTokens,
    val image: ImageTokens,
    val stroke: StrokeTokens,
    val elevation: ElevationTokens,
)

// Usage: AppTheme.dimens.margin.medium
//        AppTheme.dimens.icon.large
```

---

### ThemeSideEffect

Call at the top of any screen to configure status/navigation bar colors. Restores defaults on dispose.

```kotlin
@Composable
fun ThemeSideEffect(
    statusBarColor: Color = AppTheme.colorScheme.surface.default,
    navigationBarColor: Color = AppTheme.colorScheme.surface.default,
    isAppearanceLightStatusBars: Boolean = true,
    softInputMode: Int = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED
) {
    val context = LocalContext.current
    val view = LocalView.current
    if (!view.isInEditMode && context is Activity) {
        val defaultColor = AppTheme.colorScheme.surface.default
        LifecycleStartEffect(Unit) {
            val window = context.window
            window.statusBarColor = statusBarColor.toArgb()
            window.setSoftInputMode(softInputMode)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                isAppearanceLightStatusBars
            onStopOrDispose {
                window.statusBarColor = defaultColor.toArgb()
                window.navigationBarColor = defaultColor.toArgb()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
            }
        }
    }
}

// Usage: Call at the root of every Screen composable
@Composable
fun MyScreen() {
    ThemeSideEffect()
    // ... screen content
}
```

---

## Compose Utility Primitives

### StringWrapper

Abstracts over hardcoded strings, string resources, and annotated strings for use in composables.

```kotlin
sealed interface StringWrapper {
    data class Text(val value: String) : StringWrapper
    data class AnnotatedText(val value: AnnotatedString) : StringWrapper
    class StringResource(@StringRes val resId: Int, vararg val formatArgs: Any) : StringWrapper

    companion object {
        fun from(value: String) = Text(value)
        fun from(@StringRes resId: Int, vararg formatArgs: Any) = StringResource(resId, *formatArgs)
        fun from(value: AnnotatedString) = AnnotatedText(value)
    }
}

@Composable
fun StringWrapper.asString(): String = when (this) {
    is StringWrapper.Text           -> value
    is StringWrapper.AnnotatedText  -> value.text
    is StringWrapper.StringResource -> stringResource(resId, *formatArgs)
}

// Usage in composables:
Text(text = title.asString())
```

---

### ThrottleClick Modifier

Prevents double-taps. Applies alpha or scale visual feedback on press.

```kotlin
enum class RippleType { ALPHA, SCALE }

fun Modifier.throttleClick(
    enabled: Boolean = true,
    throttleTimeInMs: Long = 300L,
    rippleType: RippleType = RippleType.ALPHA,
    onClick: () -> Unit,
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    var enableAgain by remember { mutableStateOf(enabled) }
    val scope = rememberCoroutineScope()

    val isPressed by interactionSource.collectIsPressedAsState()
    val alpha by animateFloatAsState(
        targetValue = if (rippleType == RippleType.ALPHA && isPressed) 0.5f else 1f, label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (rippleType == RippleType.SCALE && isPressed) 0.98f else 1f, label = ""
    )

    this
        .clickable(enabled = enableAgain, indication = null, interactionSource = interactionSource) {
            enableAgain = false
            onClick()
            scope.launch {
                delay(throttleTimeInMs)
                enableAgain = true
            }
        }
        .alpha(alpha)
        .scale(scale)
}
```

---

### Shimmer Modifier & Brush

```kotlin
// Brush — composable function returning an animated shimmer gradient
@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    durationMillis: Int = 1500,
    widthOfShadowBrush: Int = 1000,
): Brush {
    if (!showShimmer) return Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 1.0f),
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.3f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer translate"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation - widthOfShadowBrush, 0f),
        end = Offset(translateAnimation, 180f),
    )
}

// Modifier shorthand
@Composable
fun Modifier.shimmer(showShimmer: Boolean = true): Modifier =
    background(shimmerBrush(showShimmer))

// Usage — skeleton loading placeholder:
Box(modifier = Modifier.fillMaxWidth().height(16.dp).shimmer(isLoading))
```

---

### ModifierExtensions (dashedBorder)

```kotlin
fun Modifier.dashedBorder(
    shape: Shape,
    color: Color,
    strokeWidth: Dp = 1.dp,
    dashLength: Dp = 6.dp,
    gapLength: Dp = 6.dp,
    cap: StrokeCap = StrokeCap.Round
) = drawWithContent {
    val outline = shape.createOutline(size, layoutDirection, this)
    val dashedStroke = Stroke(
        width = strokeWidth.toPx(),
        cap = cap,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )
    drawContent()
    drawOutline(outline = outline, style = dashedStroke, brush = SolidColor(color))
}
```

---

### Type Aliases

```kotlin
// Button label + click handler pair
typealias LabelWithListener = Pair<String, () -> Unit>

// Usage: Pair("Cancel") { dismiss() }
```

---

## Core UI Elements

### AppIcon

Supports loading skeleton, error state, and badge count.

```kotlin
data class AppIconParams(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val badgeCount: Int? = null,
    val contentDescription: String? = null
)

@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    icon: IconWrapper,
    iconTint: Color = LocalContentColor.current,
    iconSize: Dp = AppTheme.dimens.icon.large,
    params: AppIconParams = AppIconParams()
) {
    when {
        params.isLoading -> Box(
            modifier = Modifier.clip(AppTheme.shapes.chip.medium).size(AppTheme.dimens.image.small).shimmer(true)
        )
        params.isError -> Icon(
            modifier = Modifier.size(iconSize),
            painter = Icons.CircleAlert.icon,
            contentDescription = params.contentDescription,
            tint = AppTheme.colorScheme.critical
        )
        else -> Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Icon(
                modifier = Modifier.size(iconSize),
                painter = icon.icon,
                contentDescription = params.contentDescription ?: "icon",
                tint = iconTint
            )
            if ((params.badgeCount ?: 0) > 0) {
                Badge(
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-2).dp, y = 2.dp),
                    containerColor = AppTheme.colorScheme.critical
                ) {
                    Text(
                        text = if ((params.badgeCount ?: 0) < 100) "${params.badgeCount}" else "99+",
                        color = AppTheme.colorScheme.white,
                        style = AppTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
```

---

### LoadingIndicator (3 Dots)

Animated bouncing dots indicator, used inside buttons or standalone.

```kotlin
private const val NUM_DOTS = 3
private const val DOT_ANIM_DURATION = 300
private const val DOT_ANIM_DELAY = DOT_ANIM_DURATION / NUM_DOTS

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colorScheme.brand,
    dotSize: Dp = AppTheme.dimens.margin.small,
    dotSpacing: Dp = AppTheme.dimens.margin.extraSmall,
) {
    val dotSizePx = with(LocalDensity.current) { dotSize.toPx() }
    val offsets = List(NUM_DOTS) { index ->
        var offset by remember { mutableFloatStateOf(0f) }
        LaunchedEffect(Unit) {
            animate(
                initialValue = dotSizePx / 5f,
                targetValue = -dotSizePx / 5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(DOT_ANIM_DURATION),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(DOT_ANIM_DELAY * index)
                )
            ) { value, _ -> offset = value }
        }
        offset
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        offsets.forEach { yOffset ->
            Box(
                modifier = Modifier
                    .padding(horizontal = dotSpacing)
                    .size(dotSize)
                    .offset(y = yOffset.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
```

---

### Spacers

```kotlin
@Composable
fun VerticalSpacer(height: Dp) = Spacer(modifier = Modifier.height(height))

@Composable
fun HorizontalSpacer(width: Dp) = Spacer(modifier = Modifier.width(width))

// Usage:
VerticalSpacer(AppTheme.dimens.margin.medium)
```

---

### PowerplayChip

```kotlin
enum class AppChipSize { Small, Medium, Large }

sealed class AppChipColorScheme {
    object Brand   : AppChipColorScheme()
    object Neutral : AppChipColorScheme()
    object Critical: AppChipColorScheme()
}

@Composable
fun AppChip(
    text: String,
    color: AppChipColorScheme = AppChipColorScheme.Neutral,
    size: AppChipSize = AppChipSize.Medium,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    // Renders a selectable chip with themed colors
}
```

---

## Composite Components

### TopBar

```kotlin
@Immutable
data class TopBarColors(
    val containerColor: Color,
    val iconTint: Color,
    val titleColor: Color,
    val subtitleColor: Color,
    val dividerColor: Color
)

object TopBarDefaults {
    @Composable
    fun colors(
        containerColor: Color = AppTheme.colorScheme.surface.default,
        iconTint: Color = AppTheme.colorScheme.content.primary,
        titleColor: Color = AppTheme.colorScheme.content.primary,
        subtitleColor: Color = AppTheme.colorScheme.content.secondary,
        dividerColor: Color = AppTheme.colorScheme.stroke.neutral,
    ) = TopBarColors(containerColor, iconTint, titleColor, subtitleColor, dividerColor)
}

@Composable
fun AppTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: IconWrapper = Icons.ArrowLeft,
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    colors: TopBarColors = TopBarDefaults.colors(),
    showDivider: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .background(colors.containerColor)
                .padding(AppTheme.dimens.margin.small)
                .then(modifier),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.small),
        ) {
            AppIcon(
                modifier = Modifier
                    .throttleClick(onClick = onNavigationIconClick)
                    .padding(AppTheme.dimens.margin.small),
                icon = navigationIcon,
                iconTint = colors.iconTint
            )
            Column(modifier = Modifier.weight(1f)) {
                subtitle?.let {
                    Text(
                        text = it,
                        style = AppTheme.typography.labelSmall,
                        color = colors.subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = textAlign
                    )
                }
                Text(
                    text = title,
                    style = AppTheme.typography.labelLarge,
                    color = colors.titleColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = textAlign
                )
            }
            CompositionLocalProvider(LocalContentColor provides colors.iconTint) {
                actions()
            }
        }
        if (showDivider) HorizontalDivider(color = colors.dividerColor)
    }
}
```

---

### Button

**ButtonType** and **ButtonColor** determine the visual variant:

```kotlin
// Color variants (sealed class, @Composable property for theme access)
sealed class AppButtonColor {
    abstract val primaryColor: Color   @Composable @ReadOnlyComposable get
    abstract val secondaryColor: Color @Composable @ReadOnlyComposable get

    data object Brand   : AppButtonColor() { /* brand color from theme */ }
    data object Critical: AppButtonColor() { /* critical color from theme */ }
    data object Success : AppButtonColor() { /* success color from theme */ }
    data object Neutral : AppButtonColor() { /* content.primary from theme */ }
}

// Type variants
enum class ButtonType {
    Primary,    // filled container
    Secondary,  // outlined
    Tertiary,   // ghost/text with subtle border
    Link,       // text only
    Neutral;

    @Composable @ReadOnlyComposable
    fun getContainerColor(color: AppButtonColor): Color = when (this) {
        Primary -> color.primaryColor
        else    -> Color.White
    }

    @Composable @ReadOnlyComposable
    fun getContentColor(color: AppButtonColor): Color = when (this) {
        Primary -> color.secondaryColor
        else    -> color.primaryColor
    }

    @Composable @ReadOnlyComposable
    fun getBorderStroke(color: AppButtonColor, isEnabled: Boolean = true): BorderStroke? = when (this) {
        Secondary -> BorderStroke(1.dp, if (isEnabled) color.primaryColor else color.primaryColor.copy(alpha = 0.4f))
        Neutral   -> BorderStroke(1.dp, AppTheme.colorScheme.stroke.neutral)
        else      -> null
    }
}
```

**Button composable with throttle and loading state:**

```kotlin
data class AppButtonParams(
    val label: StringWrapper,
    val icon: IconWrapper? = null,
    @Position.HorizontalPosition val iconPosition: Int = Position.START,
    val buttonType: ButtonType = ButtonType.Primary,
    val color: AppButtonColor = AppButtonColor.Brand,
    val size: ButtonSize = ButtonSize.Large,
    val enabled: Boolean = true,
    val loading: Boolean = false,
) {
    companion object {
        fun default(label: StringWrapper) = AppButtonParams(label = label, buttonType = ButtonType.Primary)
    }
}

@Composable
fun AppButton(
    label: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    buttonType: ButtonType = ButtonType.Primary,
    color: AppButtonColor = AppButtonColor.Brand,
    size: ButtonSize = ButtonSize.Medium,
    icon: IconWrapper? = null,
    @Position.HorizontalPosition iconPosition: Int = Position.START,
    enabled: Boolean = true,
    throttleTimeInMs: Long = 300L,
    onClick: () -> Unit
) {
    require(label.isNotBlank()) { "Use AppIconButton for icon-only buttons" }

    var enableAgain by remember { mutableStateOf(true) }
    LaunchedEffect(enableAgain) {
        if (enableAgain) return@LaunchedEffect
        delay(throttleTimeInMs)
        enableAgain = true
    }

    val isEnabled by remember(enabled, isLoading) { derivedStateOf { enabled && !isLoading } }
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(if (isEnabled && pressed) 0.98f else 1f, label = "scale")
    val containerColor by animateColorAsState(
        targetValue = when {
            pressed    -> buttonType.getContainerColor(color).copy(alpha = 0.85f)
            !isEnabled -> buttonType.getContainerColor(color).copy(alpha = 0.4f)
            else       -> buttonType.getContainerColor(color)
        },
        label = "containerColor"
    )

    val border = buttonType.getBorderStroke(color, isEnabled)
    val contentColor = buttonType.getContentColor(color).let {
        if (!isEnabled) it.copy(alpha = 0.4f) else it
    }

    Button(
        modifier = modifier.scale(scale),
        onClick = {
            if (!enableAgain) return@Button
            enableAgain = false
            onClick()
        },
        enabled = isEnabled,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        border = border,
        shape = AppTheme.shapes.button,
        contentPadding = size.contentPadding,
    ) {
        if (isLoading) {
            LoadingIndicator(color = contentColor)
        } else {
            // icon + label layout based on iconPosition
            if (icon != null && iconPosition == Position.START) {
                AppIcon(icon = icon, iconTint = contentColor)
                HorizontalSpacer(AppTheme.dimens.margin.small)
            }
            Text(text = label, style = size.textStyle, color = contentColor)
            if (icon != null && iconPosition == Position.END) {
                HorizontalSpacer(AppTheme.dimens.margin.small)
                AppIcon(icon = icon, iconTint = contentColor)
            }
        }
    }
}
```

---

### TextField

```kotlin
data class AppTextFieldParams(
    val leadingIcon: IconWithListener? = null,
    val trailingIcon: IconWithListener? = null,
    val isLoading: Boolean = false,
    val prefix: String? = null,
    val suffix: String? = null,
    val helperText: String? = null,
    val readOnly: Boolean = false,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val keyboardActions: KeyboardActions = KeyboardActions.Default,
    val minLines: Int = 1,
    val maxLines: Int = 1,
    val mandatory: Boolean = false,
    val count: Int? = null,          // max character count; shows counter if set
    val textAlign: TextAlign = TextAlign.Start,
)

// Editable variant
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
    params: AppTextFieldParams = AppTextFieldParams(),
    error: String? = null,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    showError: Boolean = !error.isNullOrEmpty(),
    onValueChanged: (String) -> Unit
) {
    // OutlinedTextField with:
    // - label/hint, leading/trailing icons
    // - error border and message when showError
    // - character count display
    // - mandatory asterisk in label
    // - loading shimmer on trailing slot
}

// Read-only clickable variant (shows field but opens picker on click)
@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    label: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
    params: AppTextFieldParams = AppTextFieldParams(),
    error: String? = null,
    showError: Boolean = !error.isNullOrEmpty(),
    onClickListener: () -> Unit,
) {
    // Box overlaying a disabled OutlinedTextField with a clickable interceptor
}
```

---

### NetworkErrorView

Full-screen or inline error/empty state with retry button.

```kotlin
@Composable
fun NetworkErrorView(
    modifier: Modifier = Modifier,
    icon: IconWrapper = Icons.WifiOff,
    title: StringWrapper = StringWrapper.from(R.string.no_internet_connection),
    message: StringWrapper? = null,
    buttonText: StringWrapper = StringWrapper.from(R.string.tap_to_retry),
    onRetry: () -> Unit
) {
    ThemeSideEffect()
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = AppTheme.dimens.margin.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppIcon(
            modifier = Modifier
                .clip(CircleShape)
                .background(AppTheme.colorScheme.surface.sunken)
                .padding(AppTheme.dimens.margin.secondary),
            icon = icon,
            iconTint = AppTheme.colorScheme.content.tertiary
        )
        VerticalSpacer(AppTheme.dimens.margin.secondary)
        Text(
            text = title.asString(),
            style = AppTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
        message?.let {
            VerticalSpacer(AppTheme.dimens.margin.small)
            Text(text = it.asString(), style = AppTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        }
        VerticalSpacer(AppTheme.dimens.margin.tertiary)
        AppButton(label = buttonText.asString(), buttonType = ButtonType.Tertiary, onClick = onRetry)
    }
}
```

---

### CommonDialogContent

Reusable dialog body with 1–3 buttons in horizontal or vertical layout.

```kotlin
@Composable
fun CommonDialogContent(
    modifier: Modifier = Modifier,
    title: StringWrapper,
    message: StringWrapper? = null,
    primaryButton: LabelWithListener,
    secondaryButton: LabelWithListener? = null,
    tertiaryButton: LabelWithListener? = null,
    @Orientation orientation: Int = Orientation.HORIZONTAL,
    color: AppButtonColor = AppButtonColor.Brand,
) {
    Column(
        modifier = Modifier.padding(AppTheme.dimens.margin.medium).then(modifier),
        horizontalAlignment = if (orientation == Orientation.VERTICAL) Alignment.CenterHorizontally else Alignment.Start
    ) {
        Text(text = title.asString(), style = AppTheme.typography.headlineSmall)
        VerticalSpacer(AppTheme.dimens.margin.small)
        message?.let {
            Text(text = it.asString(), color = AppTheme.colorScheme.content.secondary, style = AppTheme.typography.bodyMedium)
        }
        VerticalSpacer(AppTheme.dimens.margin.large)

        if (orientation == Orientation.HORIZONTAL) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Min).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.small, Alignment.End)
            ) {
                tertiaryButton?.let {
                    AppButton(modifier = Modifier.fillMaxHeight(), label = it.first,
                        buttonType = ButtonType.Tertiary, color = AppButtonColor.Neutral, onClick = it.second)
                }
                secondaryButton?.let {
                    AppButton(modifier = Modifier.fillMaxHeight(), label = it.first,
                        buttonType = ButtonType.Secondary, color = color, onClick = it.second)
                }
                AppButton(modifier = Modifier.fillMaxHeight(), label = primaryButton.first,
                    buttonType = ButtonType.Primary, color = color, onClick = primaryButton.second)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.small)
            ) {
                AppButton(modifier = Modifier.fillMaxWidth(), label = primaryButton.first,
                    buttonType = ButtonType.Primary, color = color, onClick = primaryButton.second)
                secondaryButton?.let {
                    AppButton(modifier = Modifier.fillMaxWidth(), label = it.first,
                        buttonType = ButtonType.Secondary, color = color, onClick = it.second)
                }
                tertiaryButton?.let {
                    AppButton(modifier = Modifier.fillMaxWidth(), label = it.first,
                        buttonType = ButtonType.Tertiary, color = AppButtonColor.Neutral, onClick = it.second)
                }
            }
        }
    }
}

// Typical usage inside AlertDialog:
AlertDialog(onDismissRequest = onDismiss) {
    Surface(shape = AppTheme.shapes.card.medium) {
        CommonDialogContent(
            title = StringWrapper.from("Delete item?"),
            message = StringWrapper.from("This action cannot be undone."),
            primaryButton = "Delete" to { onConfirm() },
            secondaryButton = "Cancel" to { onDismiss() },
            color = AppButtonColor.Critical
        )
    }
}
```

---

### BottomButtons Footer

Dual-button sticky footer for form screens.

```kotlin
@Composable
fun BottomButtons(
    modifier: Modifier = Modifier,
    primaryButtonParams: AppButtonParams,
    secondaryButtonParams: AppButtonParams,
    onPrimaryButtonClick: () -> Unit,
    onSecondaryButtonClick: () -> Unit,
    showDivider: Boolean = true,
) {
    if (showDivider) HorizontalDivider(color = AppTheme.colorScheme.stroke.neutral)
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        AppButton(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            params = secondaryButtonParams.copy(buttonType = ButtonType.Secondary),
            onClick = onSecondaryButtonClick
        )
        AppButton(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            params = primaryButtonParams.copy(buttonType = ButtonType.Primary),
            onClick = onPrimaryButtonClick,
        )
    }
}

// Usage at the bottom of a Scaffold:
Scaffold(
    bottomBar = {
        BottomButtons(
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimens.margin.medium),
            primaryButtonParams = AppButtonParams.default(StringWrapper.from("Save")),
            secondaryButtonParams = AppButtonParams.default(StringWrapper.from("Cancel")),
            onPrimaryButtonClick = { viewModel.save() },
            onSecondaryButtonClick = { navController.popBackStack() },
        )
    }
) { padding ->
    // ... form content
}
```

---

### ChipGroup

Multi/single selection chip group with forced-selection support.

```kotlin
@OptIn(ExperimentalLayoutApi::class)
@Composable
inline fun <T> AppChipGroup(
    modifier: Modifier = Modifier,
    items: List<Pair<T, String>>,       // id to display label
    selectedItemIds: List<T> = emptyList(),
    forceSelectedIds: List<T> = emptyList(),  // always selected, non-toggleable
    selection: Selection = Selection.MULTIPLE,
    chipSize: AppChipSize = AppChipSize.Medium,
    crossinline onItemUpdate: (List<T>) -> Unit
) {
    val checked = remember { mutableStateListOf<T>().also { it.addAll(selectedItemIds) } }
    val list = remember { mutableStateListOf<Pair<T, String>>().also { it.addAll(items) } }

    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.secondary),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.secondary),
    ) {
        list.forEach { (id, label) ->
            val chipColor = if (id in forceSelectedIds || id in checked)
                AppChipColorScheme.Brand else AppChipColorScheme.Neutral
            AppChip(
                text = label,
                color = chipColor,
                size = chipSize,
                enabled = id !in forceSelectedIds,
                onClick = {
                    if (id in forceSelectedIds) return@AppChip
                    if (checked.contains(id)) checked.remove(id)
                    else {
                        if (selection == Selection.SINGLE) checked.clear()
                        checked.add(id)
                    }
                    onItemUpdate(checked + forceSelectedIds)
                }
            )
        }
    }
}
```

---

### OptionCard

Simple list item row with optional start/end icons.

```kotlin
@Composable
fun AppBasicOptionCard(
    modifier: Modifier = Modifier,
    label: String,
    key: String? = null,           // if null, label is used as click key
    startIcon: IconWrapper? = null,
    endIcon: IconWrapper? = null,
    contentColor: Color = AppTheme.colorScheme.content.primary,
    onClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .throttleClick { onClick(key ?: label) }
            .fillMaxWidth()
            .background(AppTheme.colorScheme.surface.default)
            .padding(AppTheme.dimens.margin.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.medium)
    ) {
        startIcon?.let { AppIcon(icon = it, iconTint = contentColor) }
        Text(modifier = Modifier.weight(1f), text = label,
            style = AppTheme.typography.bodyLarge, color = contentColor)
        endIcon?.let { AppIcon(icon = it, iconTint = contentColor) }
    }
}
```

---

## Screen Scaffold Pattern

Standard screen template combining all patterns.

```kotlin
@Composable
internal fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel()
) {
    // 1. Configure status bar
    ThemeSideEffect()

    val navController = LocalNavController.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Screen Title",
                onNavigationIconClick = { navController.popBackStack() },
                actions = {
                    AppIcon(
                        modifier = Modifier.throttleClick { /* action */ },
                        icon = Icons.Filter
                    )
                }
            )
        },
        bottomBar = {
            // Optional footer
            BottomButtons(
                modifier = Modifier.fillMaxWidth().padding(AppTheme.dimens.margin.medium),
                primaryButtonParams = AppButtonParams.default(StringWrapper.from(R.string.save)),
                secondaryButtonParams = AppButtonParams.default(StringWrapper.from(R.string.cancel)),
                onPrimaryButtonClick = { viewModel.submit() },
                onSecondaryButtonClick = { navController.popBackStack() },
            )
        }
    ) { padding ->
        // 2. Handle UIResponse states
        when (val s = state) {
            is UIResponse.Loading -> {
                // Shimmer skeleton or progress indicator
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingIndicator()
                }
            }
            is UIResponse.Error -> {
                NetworkErrorView(
                    modifier = Modifier.fillMaxSize(),
                    title = StringWrapper.from(s.error.errorInfo.msg.ifEmpty { "Error loading data" }),
                    onRetry = { viewModel.refresh() }
                )
            }
            is UIResponse.Success -> {
                val data = s.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(AppTheme.dimens.margin.medium),
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.margin.medium),
                ) {
                    items(data.items) { item ->
                        AppBasicOptionCard(
                            label = item.name,
                            endIcon = Icons.ChevronRight,
                            onClick = { navController.navigate(OrgRoutes.Detail(item.id)) }
                        )
                    }
                }
            }
        }
    }
}
```

---

## Paging (Pagination)

**PagingSource implementation:**

```kotlin
class ListPagingSource<I : AppData>(
    private val startingPageIndex: Int,
    private val source: suspend (Int) -> NetworkResponse<List<I>>,
) : PagingSource<Int, I>() {

    override fun getRefreshKey(state: PagingState<Int, I>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, I> {
        val page = params.key ?: startingPageIndex
        return when (val response = source(page)) {
            is NetworkResponse.Success -> LoadResult.Page(
                data = response.data,
                prevKey = if (page == startingPageIndex) null else page - 1,
                nextKey = if (response.data.isEmpty()) null else page + 1
            )
            else -> LoadResult.Error(Exception("Network error"))
        }
    }
}
```

**Repository returns `Flow<PagingData<T>>`:**

```kotlin
override fun getPagedItems(): Flow<PagingData<Item>> =
    repositoryResponseHandler.getPagerAsFlow(
        startingPageIndex = 1,
        source = { page -> dataSource.getItems(page) }
    ).cachedIn(viewModelScope)  // cache in VM to survive recomposition
```

**In Compose:**

```kotlin
val pagedItems: LazyPagingItems<Item> = viewModel.pagedItems.collectAsLazyPagingItems()

LazyColumn {
    items(pagedItems) { item ->
        if (item != null) ItemRow(item)
    }
    item {
        when (pagedItems.loadState.append) {
            is LoadState.Loading -> LoadingIndicator()
            is LoadState.Error   -> RetryButton { pagedItems.retry() }
            else -> Unit
        }
    }
}
```

---

## Singleton Builder Utility

Thread-safe lazy singleton with deferred initialization.

```kotlin
abstract class SingletonBuilder<T> {
    @Volatile private var instance: T? = null

    @Synchronized
    fun getInstance(): T = instance ?: initialize().also { instance = it }

    protected abstract fun initialize(): T
}

// Usage:
class RetrofitBuilder : SingletonBuilder<Retrofit>() {
    override fun initialize(): Retrofit = Retrofit.Builder()
        .baseUrl(ApiClient.BASE_URL)
        .build()
}

// Lazy delegate alternative:
object ServiceLocator {
    val retrofit: Retrofit by lazy { /* build */ }
}
```

---

## Summary: Key Conventions

| Convention | Detail |
|---|---|
| Response type in repository | `Flow<UIResponse<T>>` |
| Response type in data source | `NetworkResponse<T>` |
| UseCase invocation | `operator fun invoke(...)` |
| ViewModel state | `StateFlow` / `mutableStateOf` |
| Loading before API | Emit `UIResponse.Loading` first |
| Error handling | Centralized in `BaseViewModel.callV2` or `getUIResponse()` |
| Navigation args | `@Serializable data class` routes |
| Screen entry point | `hiltViewModel()` inside composable |
| Status bar config | `ThemeSideEffect()` at screen root |
| Click debounce | `Modifier.throttleClick()` everywhere |
| Strings in compose | `StringWrapper` (never hardcode in components) |
| Loading skeleton | `Modifier.shimmer(isLoading)` on placeholder boxes |
| DI scoping | `@Singleton` for network/repo; `@HiltViewModel` for ViewModels |
| Paging | `RepositoryResponseHandler.getPagerAsFlow` + `.cachedIn(viewModelScope)` |
