# Findr — Coding Rules & Standards

## Golden Rules (Never Violate)

1. **No business logic in composables.** All logic lives in the ViewModel or use cases.
2. **No raw Compose primitives for buttons, text fields, chips.** Always use Findr-prefixed components.
3. **No hard-coded strings in UI.** Use resource strings or constants.
4. **No Firebase imports in domain layer.** Domain is 100% pure Kotlin.
5. **No `@Preview` in feature screens.** Previews go in separate `*Preview.kt` files.
6. **Events are sealed classes.** No Boolean/String parameters for navigation.
7. **One ViewModel per screen.** Sub-components receive state + event handler, never their own ViewModel.
8. **Platform code uses `expect/actual`.** No `if (isAndroid)` checks in `commonMain`.

9. **No Unnecessary Comments.** Write code that serves as its own documentation via clear naming, architecture, and types. Comments are restricted only to complex algorithmic explanations or describing public contract caveats. Avoid structural marker comments like `// --- Auth Section ---`.
10. **Atomic Subgraphs Only for Navigation.** Never place all app destinations in a single graph. Group logically related screens into modular subgraphs (e.g. `authGraph()`, `homeGraph()`).

---

## Event Pattern

Every user interaction is represented as a `sealed class` event. ViewModels expose a single `onEvent(event)` function.

```kotlin
// ✅ Correct — sealed class with typed data
sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object SignInClicked : LoginEvent()
    data object GoogleSignInClicked : LoginEvent()
    data object ForgotPasswordClicked : LoginEvent()
    data object CreateAccountClicked : LoginEvent()
    data object DismissError : LoginEvent()
}

// ❌ Wrong — string-based events
fun onAction(action: String, value: Any? = null)
```

### Sub-components receive `onEvent` handler
```kotlin
// ✅ Correct — pass lambda, let parent handle
@Composable
private fun AuthCard(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
) {
    FindrTextField(
        value = uiState.email,
        onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
        ...
    )
}

// ❌ Wrong — direct ViewModel reference in sub-component
@Composable
private fun AuthCard(viewModel: LoginViewModel)
```

---

## UiState Pattern

Each screen has one immutable data class. Use nested sealed classes for async content.

```kotlin
// ✅ Standard pattern
data class HomeUiState(
    // Async content
    val feedItems: UiState<List<FeedItem>> = UiState.Idle,
    val liveEvents: UiState<List<LiveEvent>> = UiState.Idle,
    // Synchronous UI state
    val selectedFilterIndex: Int = 0,
    val filterChips: List<FilterChipItem> = emptyList(),
    val searchQuery: String = "",
    val notificationCount: Int = 0,
    // One-shot messages (cleared after shown)
    val errorMessage: String? = null,
    val successMessage: String? = null,
)
```

### Do not put composable types in UiState
```kotlin
// ❌ Wrong — Compose types in domain state
data class HomeUiState(
    val icon: ImageVector,          // Compose type!
    val gradientColors: List<Color>, // Compose type!
)

// ✅ Correct — pure Kotlin in state, map to Compose in UI
data class LiveEvent(
    val id: String,
    val title: String,
    val iconKey: LiveEventIcon,     // enum
    val gradientType: GradientType, // enum
)

enum class LiveEventIcon { LAPTOP, SPORTS, VOTE, CAMPAIGN }
enum class GradientType { BLUE, PURPLE, GREEN }

// In composable — map enum → actual icon/color
val icon = when (event.iconKey) {
    LiveEventIcon.LAPTOP -> Icons.Default.Laptop
    LiveEventIcon.SPORTS -> Icons.Default.SportsSoccer
    ...
}
```

---

## Enum Usage

Use enums (not strings) for:
- Post categories
- Filter types  
- Error types
- Navigation destinations
- Feature flags

```kotlin
// Post categories
enum class PostCategory {
    PROJECT, EVENT, POST, BUG_REPORT
}

// Filter types
enum class FeedFilter {
    ALL, EVENTS, CLUBS, ACADEMIC, PARTIES;

    val label: String get() = when (this) {
        ALL -> "All Posts"
        EVENTS -> "Events"
        CLUBS -> "Clubs"
        ACADEMIC -> "Academic"
        PARTIES -> "Parties"
    }
}

// Project status  
enum class ProjectStatus {
    OPEN, CLOSED, COMPLETED;

    val isActive: Boolean get() = this == OPEN
}
```

---

## Sealed Class Usage

Use sealed classes for:
- Events (user interactions)
- Navigation events (one-shot)
- Domain results
- Feed item types

```kotlin
// Feed item types (with typed data, not Any)
sealed class FeedItem {
    abstract val id: String

    data class Post(val data: FeedPost) : FeedItem() {
        override val id = data.id
    }
    data class Project(val data: ProjectPost) : FeedItem() {
        override val id = data.id
    }
    data class Bug(val data: BugPost) : FeedItem() {
        override val id = data.id
    }
    data class Announcement(val data: AnnouncementPost) : FeedItem() {
        override val id = data.id
    }
}

// Navigation events (SharedFlow, one-shot)
sealed class LoginNavEvent {
    data object NavigateToHome : LoginNavEvent()
    data object NavigateToRegistration : LoginNavEvent()
    data class ShowError(val message: String) : LoginNavEvent()
}
```

---

## Click Handling Rules

| Scenario | Modifier to use |
|----------|----------------|
| Navigation triggers | `Modifier.throttleClick()` to prevent double-tap |
| Form submit buttons | `FindrPrimaryButton(onClick = ...)` — built-in debounce via `enabled` |
| List item clicks | `Modifier.clickable(indication = ripple())` |
| Icon-only clicks | `IconButton(onClick = ...)` |
| Text links ("Forgot password?") | `Modifier.throttleClick()` |

```kotlin
// ✅ Navigation trigger — throttled
Box(
    modifier = Modifier.throttleClick {
        viewModel.onEvent(HomeEvent.ProjectEnrollClicked(project.id))
    }
)

// ✅ List item with ripple
Row(
    modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
        ) { onEvent(HomeEvent.LiveEventClicked(event.id)) }
)

// ❌ Wrong — raw clickable without ripple for list items
Row(modifier = Modifier.clickable { ... })
```

---

## Loading State Rules

| Scenario | Component |
|----------|-----------|
| First page load | `ShimmerBox` / `ShimmerCircle` skeletons |
| Refresh (data already shown) | Subtle `LinearProgressIndicator` at top |
| Button action | `isLoading = true` on `FindrPrimaryButton` |
| Full-screen blocking | `FindrFullScreenLoader` overlay |
| Section reload | `ThreeDotLoadingIndicator` inline |

---

## Error Handling Rules

| Error level | Component |
|-------------|-----------|
| Full page fail | `NetworkErrorView(errorType, onRetry)` |
| Section fail | `InlineErrorBanner(message, onRetry)` |
| Form error | `errorMessage` on `FindrTextField` |
| Action fail (submit) | `FindrErrorSnackbar` at bottom |
| Transient info | `snackbarHostState.showSnackbar(msg)` via `FindrScaffold` |

---

## ViewModel Coroutine Rules

```kotlin
class MyViewModel(private val useCase: MyUseCase) : ViewModel() {

    // ✅ Launch in viewModelScope — auto-cancelled on ViewModel destruction
    private fun loadData() {
        viewModelScope.launch {
            useCase().collect { state ->
                _uiState.update { it.copy(content = state) }
            }
        }
    }

    // ✅ Multiple operations — use structured concurrency
    private fun loadAll() {
        viewModelScope.launch {
            supervisorScope {
                launch { loadProjects() }
                launch { loadLiveEvents() }
            }
        }
    }
}
```

---

## Composition Local Pattern

Never pass colors/typography raw into components. Use theme:

```kotlin
// ✅ Correct
val appColors = MaterialTheme.appColors
val primary = MaterialTheme.colorScheme.primary

// ❌ Wrong — hard-coded colors
val myBlue = Color(0xFF13B6EC)
```

---

## KMP Shared Code vs Platform Code

| What to share | Where |
|---------------|-------|
| Domain models, use cases, repository interfaces | `commonMain` |
| ViewModel logic | `commonMain` (lifecycle-viewmodel KMP) |
| All Compose UI | `commonMain` (Compose Multiplatform) |
| Firebase SDK calls | `androidMain` / `iosMain` via `expect/actual` |
| Platform permissions | `androidMain` / `iosMain` |
| File picker / camera | `androidMain` / `iosMain` |
| Deep links | `androidMain` / `iosMain` |

```kotlin
// commonMain — declare the contract
interface AuthDataSource {
    suspend fun signInWithEmail(email: String, password: String): NetworkResponse<AuthUser>
    suspend fun signInWithGoogle(): NetworkResponse<AuthUser>
    suspend fun signOut()
    fun getCurrentUser(): AuthUser?
}

// androidMain — Firebase implementation
class FirebaseAuthDataSourceImpl : AuthDataSource {
    private val auth = Firebase.auth
    override suspend fun signInWithEmail(...) = ...
}

// iosMain — Firebase implementation  
class FirebaseAuthDataSourceImpl : AuthDataSource {
    private val auth = FirebaseAuth.auth
    override suspend fun signInWithEmail(...) = ...
}
```

---

## Screen Sub-Component Rules

- Sub-components are **private** (`private fun`) in their file OR extracted to `components/` package
- Sub-components take **state data + event lambda**, not ViewModel
- Sub-components are named descriptively: `FeedPostCard`, `LiveEventCard`, `RoleCard`
- Sub-components > 50 lines should be in their own file in `components/`

```kotlin
// ✅ Sub-component in components/ directory
// file: feature/home/presentation/components/FeedPostCard.kt
package com.exa.android.reflekt.feature.home.presentation.components

@Composable
internal fun FeedPostCard(
    post: FeedPost,
    onEvent: (HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // ...
}
```

---

## Domain Model Rules

Domain models must be:
- Pure Kotlin (`data class`, no Android imports)
- Immutable (all `val`)
- Self-contained (no dependencies)

```kotlin
// ✅ Correct domain model
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val authorId: String,
    val authorName: String,
    val status: ProjectStatus,
    val roles: List<OpenRole>,
    val createdAt: Long,
)

// ❌ Wrong — Firebase type in domain
data class Project(
    val snapshot: DocumentSnapshot, // Firebase type!
)
```

---

## Repository Interface Rules

```kotlin
// ✅ Returns Flow<UiState<T>> — repository handles Loading/Success/Error
interface ProjectRepository {
    fun getProjectDetail(projectId: String): Flow<UiState<Project>>
    fun enrollInProject(projectId: String): Flow<UiState<Unit>>
    suspend fun createProject(project: NewProject): NetworkResponse<Project>
}

// ❌ Returns raw type — ViewModel has to handle errors manually
interface ProjectRepository {
    suspend fun getProjectDetail(projectId: String): Project?
}
```

---

## Use Case Rules

```kotlin
// ✅ One public method, validates inputs before delegating to repository
class SignInWithEmailUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): NetworkResponse<AuthUser> {
        if (email.isBlank()) return NetworkResponse.ApiError(400, "Email cannot be empty")
        if (!email.contains("@")) return NetworkResponse.ApiError(400, "Invalid email")
        if (password.length < 6) return NetworkResponse.ApiError(400, "Password too short")
        return repository.signInWithEmail(email, password)
    }
}

// Use case encapsulates ALL validation — ViewModel only calls it
class LoginViewModel(
    private val signInUseCase: SignInWithEmailUseCase,
) : ViewModel() {
    private fun signIn() {
        viewModelScope.launch {
            val result = signInUseCase(uiState.value.email, uiState.value.password)
            // handle result
        }
    }
}
```

---

## Anti-Patterns to Avoid

| Anti-Pattern | Fix |
|-------------|-----|
| `LaunchedEffect(true)` to load data | Load in `init {}` of ViewModel |
| `remember { mutableStateOf }` for business state | Move to ViewModel `_uiState` |
| Direct Firebase calls in ViewModel | Extract to DataSource + Repository |
| `if (platform == "android")` in commonMain | Use `expect/actual` |
| Passing ViewModel to sub-components | Pass `uiState` + `onEvent` lambda |
| Navigation in ViewModel via `navController` | Emit `NavEvent` through `SharedFlow` |
| Raw `Button` / `OutlinedTextField` | Use `FindrPrimaryButton` / `FindrTextField` |
| Composable type in data models | Use enums, later map to Compose type in UI |
| Business logic in composable functions | Move to ViewModel/UseCase |
| Hardcoded colors outside theme | Use `MaterialTheme.colorScheme` or `MaterialTheme.appColors` |
