# Findr — Data Flow & Response Handling

## Core Response Types

### `NetworkResponse<T>` — Raw API/Firebase Result
Lives in `core/network/`. Represents the raw outcome of a datasource call.

```kotlin
// core/network/response/NetworkResponse.kt
sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T) : NetworkResponse<T>()
    data class ApiError<out T>(
        val code: Int,
        val message: String?,
    ) : NetworkResponse<T>()
    data class NetworkError<out T>(val exception: Exception) : NetworkResponse<T>()
    data class UnknownError<out T>(val throwable: Throwable) : NetworkResponse<T>()
}
```

### `UiState<T>` — UI-Ready State
Lives in `core/ui/`. Wraps the complete screen rendering state.

```kotlin
// core/ui/state/UiState.kt
sealed class UiState<out T> {
    /** Initial / before first load */
    data object Idle : UiState<Nothing>()

    /** Loading — can carry stale data for skeleton/shimmer */
    data class Loading<out T>(val data: T? = null) : UiState<T>()

    /** Data available */
    data class Success<out T>(val data: T) : UiState<T>()

    /** Error — structured */
    data class Error(val error: AppError) : UiState<Nothing>()
}

// Helper extensions
val <T> UiState<T>.isLoading: Boolean get() = this is UiState.Loading
val <T> UiState<T>.isSuccess: Boolean get() = this is UiState.Success
val <T> UiState<T>.data: T? get() = (this as? UiState.Success)?.data
```

### `AppError` — Structured Error
```kotlin
// core/ui/state/AppError.kt
data class AppError(
    val type: ErrorType,
    val message: String?,
    val code: Int? = null,
) {
    companion object {
        fun noInternet() = AppError(ErrorType.NO_INTERNET, "No internet connection")
        fun server(message: String? = null) = AppError(ErrorType.SERVER, message)
        fun general(message: String? = null) = AppError(ErrorType.GENERAL, message)
        fun fromCode(code: Int, msg: String?) = AppError(
            type = when (code) {
                in 400..499 -> ErrorType.CLIENT
                in 500..599 -> ErrorType.SERVER
                else -> ErrorType.GENERAL
            },
            message = msg,
            code = code,
        )
    }
}

enum class ErrorType { NO_INTERNET, SERVER, CLIENT, GENERAL, NOT_FOUND }
```

---

## Repository Response Handler

Centralizes the `NetworkResponse → Flow<UiState>` conversion. Every repository uses this:

```kotlin
// core/data/RepositoryResponseHandler.kt
class RepositoryResponseHandler {
    fun <T> asUiStateFlow(
        execute: suspend () -> NetworkResponse<T>
    ): Flow<UiState<T>> = flow {
        emit(UiState.Loading())
        val result = execute()
        emit(result.toUiState())
    }
}

// Extension function
fun <T> NetworkResponse<T>.toUiState(): UiState<T> = when (this) {
    is NetworkResponse.Success   -> UiState.Success(data)
    is NetworkResponse.ApiError  -> UiState.Error(AppError.fromCode(code, message))
    is NetworkResponse.NetworkError -> UiState.Error(AppError.noInternet())
    is NetworkResponse.UnknownError -> UiState.Error(AppError.general(throwable.message))
}
```

---

## Screen UiState Pattern

Each screen has its own `UiState` that uses `UiState<T>` for async data and individual fields for form state:

```kotlin
// Standard screen state
data class ProjectListUiState(
    val projects: UiState<List<Project>> = UiState.Idle,
    val searchQuery: String = "",
    val activeFilter: ProjectFilter = ProjectFilter.ALL,
    val errorMessage: String? = null,   // one-shot messages (snackbar)
)

// For form screens
data class CreateProjectUiState(
    val title: String = "",
    val description: String = "",
    val skills: Set<String> = emptySet(),
    val currentSkillInput: String = "",
    val openings: Int = 1,
    val submitState: UiState<Unit> = UiState.Idle,  // only tracks submit
    val fieldErrors: Map<String, String> = emptyMap(),  // field-level validation
    val errorMessage: String? = null,
)
```

---

## ViewModel Pattern

```kotlin
class ProjectListViewModel(
    private val getProjectsUseCase: GetProjectsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()

    init { loadProjects() }

    fun onEvent(event: ProjectListEvent) {
        when (event) {
            is ProjectListEvent.SearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                // debounce + search
            }
            is ProjectListEvent.FilterSelected -> {
                _uiState.update { it.copy(activeFilter = event.filter) }
                loadProjects()
            }
            is ProjectListEvent.Retry -> loadProjects()
            is ProjectListEvent.DismissError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun loadProjects() {
        viewModelScope.launch {
            getProjectsUseCase().collect { state ->
                _uiState.update { it.copy(projects = state) }
            }
        }
    }
}
```

---

## Screen Rendering Pattern

```kotlin
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onProjectClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    FindrScaffold(title = "Projects") { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState.projects) {
                is UiState.Idle    -> Unit
                is UiState.Loading -> ProjectListSkeleton()  // shimmer
                is UiState.Success -> ProjectList(
                    items = state.data,
                    onItemClick = onProjectClick,
                )
                is UiState.Error   -> NetworkErrorView(
                    errorType = state.error.type.toErrorType(),
                    message = state.error.message,
                    onRetry = { viewModel.onEvent(ProjectListEvent.Retry) },
                )
            }
        }
    }

    // One-shot error snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            // show snackbar
        }
    }
}
```

---

## Firebase Data Source Pattern

```kotlin
// In androidMain (or using expect/actual)
class FirebaseAuthDataSourceImpl : AuthDataSource {

    private val auth = Firebase.auth

    override suspend fun signInWithEmail(email: String, password: String): NetworkResponse<AuthUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return NetworkResponse.ApiError(401, "No user returned")
            NetworkResponse.Success(user.toDomain())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            NetworkResponse.ApiError(401, "Invalid credentials")
        } catch (e: Exception) {
            NetworkResponse.UnknownError(e)
        }
    }

    override suspend fun signInWithGoogle(): NetworkResponse<AuthUser> {
        // Platform-specific Google Sign-In flow
        return try {
            // ... Google Sign-In credential acquisition
            NetworkResponse.Success(user.toDomain())
        } catch (e: Exception) {
            NetworkResponse.UnknownError(e)
        }
    }
}
```

---

## Shimmer / Loading State

When `UiState.Loading` is received with no stale data, show skeleton placeholders:

```kotlin
// Skeleton for a feed card
@Composable
fun FeedCardSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ShimmerCircle(size = 40.dp)
            Spacer(Modifier.width(12.dp))
            Column {
                ShimmerBox(height = 14.dp, cornerRadius = 4.dp)
                Spacer(Modifier.height(6.dp))
                ShimmerBox(height = 10.dp, width = 120.dp, cornerRadius = 4.dp)
            }
        }
        Spacer(Modifier.height(12.dp))
        ShimmerBox(height = 12.dp, cornerRadius = 4.dp)
        Spacer(Modifier.height(6.dp))
        ShimmerBox(height = 12.dp, cornerRadius = 4.dp)
    }
}

// Usage in LazyColumn
LazyColumn {
    if (uiState.projects is UiState.Loading) {
        items(5) { FeedCardSkeleton() }
    }
}
```

---

## One-Shot Navigation Events

For events that trigger navigation (not just state), use a separate `SharedFlow`:

```kotlin
class LoginViewModel(...) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Navigation / one-shot events  
    private val _navigationEvent = MutableSharedFlow<LoginNavEvent>()
    val navigationEvent: SharedFlow<LoginNavEvent> = _navigationEvent.asSharedFlow()

    private fun onLoginSuccess() {
        viewModelScope.launch {
            _navigationEvent.emit(LoginNavEvent.NavigateToHome)
        }
    }
}

sealed class LoginNavEvent {
    data object NavigateToHome : LoginNavEvent()
    data object NavigateToRegistration : LoginNavEvent()
    data class ShowError(val message: String) : LoginNavEvent()
}

// In Screen:
LaunchedEffect(Unit) {
    viewModel.navigationEvent.collect { event ->
        when (event) {
            LoginNavEvent.NavigateToHome -> onLoginSuccess()
            LoginNavEvent.NavigateToRegistration -> onNavigateToSignUp()
            is LoginNavEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
        }
    }
}
```
