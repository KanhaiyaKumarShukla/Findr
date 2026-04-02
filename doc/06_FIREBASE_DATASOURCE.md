# Findr — Firebase Data Source Pattern

## Strategy: Abstract Firebase Behind Interfaces

Firebase is **never imported in `commonMain`**. All Firebase SDK calls happen in platform-specific `DataSourceImpl` classes. The domain layer relies on abstract interfaces, making future data source swaps (e.g., REST API, GraphQL) require only a new `DataSourceImpl`.

```
commonMain              androidMain / iosMain
────────────            ─────────────────────
AuthDataSource  ←─────  FirebaseAuthDataSourceImpl
ProjectDataSource ←───  FirebaseProjectDataSourceImpl
UserDataSource ←──────  FirebaseUserDataSourceImpl
```

---

## Interface Template

```kotlin
// commonMain
// feature/auth/data/datasource/AuthDataSource.kt
interface AuthDataSource {
    suspend fun signInWithEmail(email: String, password: String): NetworkResponse<AuthUser>
    suspend fun signInWithGoogle(): NetworkResponse<AuthUser>
    suspend fun signUp(email: String, password: String, displayName: String): NetworkResponse<AuthUser>
    suspend fun signOut()
    fun getCurrentUser(): AuthUser?
    fun observeAuthState(): Flow<AuthUser?>
}
```

---

## Android Firebase Implementation

```kotlin
// androidMain
// feature/auth/data/datasource/FirebaseAuthDataSourceImpl.kt
class FirebaseAuthDataSourceImpl : AuthDataSource {

    private val auth = Firebase.auth

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): NetworkResponse<AuthUser> = safeCall {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        result.user?.toDomain() ?: throw IllegalStateException("No user returned")
    }

    override suspend fun signInWithGoogle(): NetworkResponse<AuthUser> {
        // Google Sign-In is fully platform-specific — implemented via GoogleSignIn SDK
        // Credentials are obtained by the platform layer before calling this
        return safeCall {
            // Credential passed from platform entry point
            val credential = GoogleAuthProvider.getCredential(idToken = /* from platform */)
            val result = auth.signInWithCredential(credential).await()
            result.user?.toDomain() ?: throw IllegalStateException("No user returned")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): AuthUser? = auth.currentUser?.toDomain()

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}

// Safe call wrapper — maps exceptions to NetworkResponse
private suspend fun <T> safeCall(block: suspend () -> T): NetworkResponse<T> {
    return try {
        NetworkResponse.Success(block())
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        NetworkResponse.ApiError(401, "Invalid email or password")
    } catch (e: FirebaseAuthInvalidUserException) {
        NetworkResponse.ApiError(404, "No account found with this email")
    } catch (e: FirebaseNetworkException) {
        NetworkResponse.NetworkError(e)
    } catch (e: Exception) {
        NetworkResponse.UnknownError(e)
    }
}
```

---

## Firestore (Document DB) Pattern

```kotlin
// Interface — commonMain
interface ProjectDataSource {
    suspend fun getProject(projectId: String): NetworkResponse<ProjectDto>
    suspend fun getProjects(filter: ProjectFilter): NetworkResponse<List<ProjectDto>>
    suspend fun createProject(dto: CreateProjectDto): NetworkResponse<ProjectDto>
    fun observeProject(projectId: String): Flow<NetworkResponse<ProjectDto>>
}

// Implementation — androidMain
class FirebaseProjectDataSourceImpl : ProjectDataSource {

    private val db = Firebase.firestore

    override suspend fun getProject(projectId: String): NetworkResponse<ProjectDto> = safeFirestoreCall {
        val doc = db.collection("projects").document(projectId).get().await()
        doc.toObject<ProjectDto>() ?: throw NoSuchElementException("Project not found")
    }

    override suspend fun getProjects(filter: ProjectFilter): NetworkResponse<List<ProjectDto>> = safeFirestoreCall {
        val query = db.collection("projects")
            .whereNotEqualTo("status", "closed")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
        val snapshot = query.get().await()
        snapshot.toObjects<ProjectDto>()
    }

    override suspend fun createProject(dto: CreateProjectDto): NetworkResponse<ProjectDto> = safeFirestoreCall {
        val ref = db.collection("projects").document()
        val withId = dto.copy(id = ref.id)
        ref.set(withId).await()
        withId.toProjectDto()
    }

    override fun observeProject(projectId: String): Flow<NetworkResponse<ProjectDto>> = callbackFlow {
        val listener = db.collection("projects")
            .document(projectId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(NetworkResponse.UnknownError(error))
                    return@addSnapshotListener
                }
                val dto = snapshot?.toObject<ProjectDto>()
                if (dto != null) {
                    trySend(NetworkResponse.Success(dto))
                } else {
                    trySend(NetworkResponse.ApiError(404, "Project not found"))
                }
            }
        awaitClose { listener.remove() }
    }
}

private suspend fun <T> safeFirestoreCall(block: suspend () -> T): NetworkResponse<T> {
    return try {
        NetworkResponse.Success(block())
    } catch (e: FirebaseFirestoreException) {
        when (e.code) {
            FirebaseFirestoreException.Code.NOT_FOUND -> NetworkResponse.ApiError(404, "Not found")
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> NetworkResponse.ApiError(403, "Permission denied")
            else -> NetworkResponse.ApiError(500, e.message)
        }
    } catch (e: Exception) {
        NetworkResponse.UnknownError(e)
    }
}
```

---

## Mapper Pattern

```kotlin
// feature/auth/data/mapper/AuthMapper.kt

// Firebase User → Domain AuthUser
fun FirebaseUser.toDomain(): AuthUser = AuthUser(
    id = uid,
    email = email ?: "",
    displayName = displayName ?: "",
    photoUrl = photoUrl?.toString(),
    isEmailVerified = isEmailVerified,
)

// feature/project/data/mapper/ProjectMapper.kt

// DTO → Domain
fun ProjectDto.toDomain(): Project = Project(
    id = id,
    title = title,
    description = description,
    authorId = authorId,
    authorName = authorName,
    status = ProjectStatus.valueOf(status.uppercase()),
    roles = roles.map { it.toDomain() },
    createdAt = createdAt,
)

// Domain → DTO (for create/update)
fun NewProject.toDto(authorId: String): CreateProjectDto = CreateProjectDto(
    title = title,
    description = description,
    authorId = authorId,
    status = "open",
    roles = roles.map { it.toDto() },
    createdAt = System.currentTimeMillis(),
)
```

---

## Repository Implementation

```kotlin
// feature/project/data/repository/ProjectRepositoryImpl.kt
class ProjectRepositoryImpl(
    private val dataSource: ProjectDataSource,
    private val handler: RepositoryResponseHandler,
) : ProjectRepository {

    override fun getProjectDetail(projectId: String): Flow<UiState<Project>> =
        handler.asUiStateFlow {
            dataSource.getProject(projectId)
                .map { dto -> dto.toDomain() }  // extension on NetworkResponse
        }

    override fun observeProject(projectId: String): Flow<UiState<Project>> =
        dataSource.observeProject(projectId)
            .map { response ->
                when (response) {
                    is NetworkResponse.Success -> UiState.Success(response.data.toDomain())
                    is NetworkResponse.ApiError -> UiState.Error(AppError.fromCode(response.code, response.message))
                    else -> UiState.Error(AppError.general())
                }
            }
            .onStart { emit(UiState.Loading()) }
}
```

---

## Switching Data Sources in Future

If you ever want to switch from Firebase to a REST API:

1. Create a new `RestProjectDataSourceImpl` in `androidMain`
2. Register it in the DI module instead of `FirebaseProjectDataSourceImpl`
3. Zero changes in `domain/` or `presentation/`

```kotlin
// DI module toggle (Koin example)
val projectModule = module {
    // switch here:
    single<ProjectDataSource> { FirebaseProjectDataSourceImpl() }
    // future: single<ProjectDataSource> { RestProjectDataSourceImpl(get()) }

    single<ProjectRepository> { ProjectRepositoryImpl(get(), get()) }
}
```

---

## Real-time Updates Pattern

For features that need live data (e.g., project collaborator count), use Firestore `callbackFlow`:

```kotlin
// In ViewModel — observe live data
private fun observeProject() {
    viewModelScope.launch {
        projectRepository.observeProject(projectId)
            .collect { state ->
                _uiState.update { it.copy(project = state) }
            }
    }
}
```

The UI will re-render automatically when Firestore document updates.

---

## Error Codes Reference

| Firebase error | Code | User message |
|---------------|------|-------------|
| `INVALID_CREDENTIALS` | 401 | "Invalid email or password" |
| `USER_NOT_FOUND` | 404 | "No account found with this email" |
| `EMAIL_ALREADY_EXISTS` | 409 | "This email is already registered" |
| `WEAK_PASSWORD` | 400 | "Password must be at least 6 characters" |
| `NETWORK_ERROR` | — | "No internet connection" |
| `PERMISSION_DENIED` | 403 | "You don't have permission" |
| `NOT_FOUND` | 404 | "Content not found" |
| Generic exception | 500 | "Something went wrong. Please try again" |
