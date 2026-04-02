# Findr — Refactoring Guide

This document outlines the **specific refactoring steps** needed to bring the codebase up to the new architecture standards.

---

## Priority 1: Extract Sub-Components

### LoginScreen.kt
Currently 523 lines in one file. Extract private composables:

| Current (private fun in LoginScreen.kt) | Target file |
|----------------------------------------|------------|
| `BackgroundDecorations()` | `components/BackgroundDecorations.kt` |
| `LogoSection()` | `components/LogoSection.kt` |
| `AuthCard()` | `components/AuthCard.kt` |
| `GoogleSignInButton()` | `components/GoogleSignInButton.kt` |
| `GoogleIcon()` | `components/GoogleSignInButton.kt` |
| `OrDivider()` | `components/OrDivider.kt` |
| `FooterSection()` | `components/FooterSection.kt` |

**AuthCard.kt** (the most important change — uses raw Compose → Findr components):
```kotlin
// BEFORE (using raw OutlinedTextField + Button)
OutlinedTextField(
    value = uiState.email,
    shape = RoundedCornerShape(16.dp),
    colors = OutlinedTextFieldDefaults.colors(...),
    ...
)

// AFTER (using FindrTextField)
FindrTextField(
    value = uiState.email,
    onValueChange = { onEvent(LoginEvent.EmailChanged(it)) },
    placeholder = "Student Email (.edu)",
    leadingIcon = Icons.Default.Email,
    errorMessage = uiState.emailError,
)
```

```kotlin
// BEFORE (raw Button)
Button(
    onClick = { onEvent(LoginEvent.SignInClicked) },
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Primary),
    ...
)

// AFTER (FindrPrimaryButton)
FindrPrimaryButton(
    text = "Sign In",
    onClick = { onEvent(LoginEvent.SignInClicked) },
    isLoading = uiState.isLoading,
    trailingIcon = {
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
    }
)
```

---

### HomeScreen.kt
Currently 1325 lines! Extract to `components/`:

| Extract | To file |
|---------|---------|
| `TopNavBar` | `components/TopNavBar.kt` |
| `BottomNavBar` | `components/BottomNavBar.kt` |
| `LiveNowSection` + `LiveEventCard` | `components/LiveNowSection.kt` |
| `TrendingNewsTicker` | `components/TrendingNewsTicker.kt` |
| `FilterChipsRow` | `components/FilterChipsRow.kt` |
| `FeedPostCard` | `components/FeedPostCard.kt` |
| `ProjectPostCard` | `components/ProjectPostCard.kt` |
| `BugPostCard` | `components/BugPostCard.kt` |
| `AnnouncementCard` | `components/AnnouncementCard.kt` |

**FilterChipsRow** — replace custom chip with `FindrFilterChip`:
```kotlin
// BEFORE (custom chip box)
Box(
    modifier = Modifier
        .clip(RoundedCornerShape(20.dp))
        .background(if (chip.isSelected) primary else surface)
        .clickable { onEvent(HomeEvent.FilterSelected(index)) }
        .padding(horizontal = 16.dp, vertical = 10.dp)
) {
    Text(chip.label, ...)
}

// AFTER (FindrFilterChip)
FindrFilterChip(
    label = chip.label,
    selected = chip.isSelected,
    onToggle = { onEvent(HomeEvent.FilterSelected(index)) },
)
```

---

### ProjectDetailScreen.kt
Currently 1147 lines. Extract to `components/`:

| Extract | To file |
|---------|---------|
| `HeroSection` + `HeroDecorations` + `GlassButton` | `components/HeroSection.kt` |
| `ProjectHeaderCard` | `components/ProjectHeaderCard.kt` |
| `TeamStatusSection` | `components/TeamStatusSection.kt` |
| `AboutSection` | `components/AboutSection.kt` |
| `OpenRolesSection` + `RoleCard` + `PulsingDot` + `SkillTag` | `components/OpenRolesSection.kt` |
| `CollaboratorsSection` + `CollaboratorAvatar` | `components/CollaboratorsSection.kt` |
| `DiscussionSection` + comment composables | `components/DiscussionSection.kt` |
| `EnrollButton` | `components/EnrollButton.kt` |

**SkillTag** → use `FindrSuggestionChip`:
```kotlin
// BEFORE (custom text with background)
Text(
    text = skill,
    modifier = Modifier
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colorScheme.background)
        .border(...)
        .padding(...)
)

// AFTER (FindrSuggestionChip — read-only, no action needed)
FindrSuggestionChip(label = skill, onClick = {})
```

---

## Priority 2: Add Missing Domain Layer

### ProjectDetailScreen — Extract Domain Models

Currently data models are in the presentation file (`ProjectDetailScreen.kt`). Move:

```kotlin
// MOVE to: feature/project/domain/model/Project.kt
data class Project(
    val id: String,
    val category: String,
    val title: String,
    val timeAgo: String,
    val authorId: String,
    val authorName: String,
    val authorSubtitle: String,
    val spotsTotal: Int,
    val spotsFilled: Int,
    val description: String,
    val roles: List<OpenRole>,
    val collaborators: List<Collaborator>,
    val status: ProjectStatus,
    val createdAt: Long,
)

// MOVE to: feature/project/domain/model/OpenRole.kt
data class OpenRole(
    val id: String,
    val title: String,
    val description: String,
    val skills: List<String>,
    val isHighlighted: Boolean = false,
)

// MOVE to: feature/project/domain/model/Collaborator.kt
data class Collaborator(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val role: String,
)
```

---

## Priority 3: Add ViewModel to ProjectDetailScreen

Currently `ProjectDetailScreen` uses `remember { sampleProject() }` — no ViewModel.

**Create:**
- `ProjectDetailViewModel.kt`
- `ProjectDetailUiState.kt`
- `ProjectDetailEvent.kt`

```kotlin
// ProjectDetailUiState.kt
data class ProjectDetailUiState(
    val project: UiState<Project> = UiState.Loading(),
    val isDescriptionExpanded: Boolean = false,
    val commentInput: String = "",
    val showEnrollDialog: Boolean = false,
    val enrollState: UiState<Unit> = UiState.Idle,
    val errorMessage: String? = null,
)

// ProjectDetailEvent.kt
sealed class ProjectDetailEvent {
    data object ToggleDescription : ProjectDetailEvent()
    data class CommentInputChanged(val text: String) : ProjectDetailEvent()
    data object SubmitComment : ProjectDetailEvent()
    data object EnrollClicked : ProjectDetailEvent()
    data object ConfirmEnroll : ProjectDetailEvent()
    data object DismissEnrollDialog : ProjectDetailEvent()
    data class RoleClicked(val roleId: String) : ProjectDetailEvent()
    data object BookmarkClicked : ProjectDetailEvent()
    data object ShareClicked : ProjectDetailEvent()
    data object DismissError : ProjectDetailEvent()
    data object Retry : ProjectDetailEvent()
}
```

---

## Priority 4: Add ViewModel to OnboardingScreen

Currently `OnboardingScreen` manages pager state locally. Add:

```kotlin
// OnboardingViewModel.kt
class OnboardingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _navEvent = MutableSharedFlow<OnboardingNavEvent>()
    val navEvent: SharedFlow<OnboardingNavEvent> = _navEvent.asSharedFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NextPage -> handleNext()
            OnboardingEvent.SkipClicked -> finish()
        }
    }

    private fun handleNext() {
        val state = _uiState.value
        if (state.currentPage < state.totalPages - 1) {
            _uiState.update { it.copy(currentPage = it.currentPage + 1) }
        } else {
            finish()
        }
    }

    private fun finish() {
        viewModelScope.launch {
            _navEvent.emit(OnboardingNavEvent.NavigateToLogin)
        }
    }
}
```

---

## Priority 5: Replace HomeUiState Compose Types

Currently `HomeUiState.kt` contains `ImageVector` and `Color` (Compose types) — these should not be in state:

```kotlin
// BEFORE (Compose types in state)
data class LiveEvent(
    val id: String,
    val title: String,
    val gradientColors: List<Color>,    // ❌ Compose
    val icon: ImageVector,              // ❌ Compose
)

// AFTER (enums + mapping in composable)
data class LiveEvent(
    val id: String,
    val title: String,
    val subtitle: String,
    val viewerCount: Int,
    val gradientType: LiveGradientType,  // ✅ enum
    val iconType: LiveEventIconType,     // ✅ enum
    val isPulsing: Boolean = false,
)

enum class LiveGradientType {
    BLUE_TECH, PURPLE_VOTE, GREEN_SPORTS;
    
    val colors: List<Color>
        @Composable get() = when (this) {
            BLUE_TECH    -> listOf(Color(0xFF1E3A5F), Color(0xFF0EA0D1))
            PURPLE_VOTE  -> listOf(Color(0xFF2D1B4E), Color(0xFF6366F1))
            GREEN_SPORTS -> listOf(Color(0xFF1B4332), Color(0xFF22C55E))
        }
}

enum class LiveEventIconType {
    LAPTOP, VOTE, SOCCER
}

// In LiveEventCard composable — map enum to actual values
val icon = when (event.iconType) {
    LiveEventIconType.LAPTOP -> Icons.Default.Laptop
    LiveEventIconType.VOTE   -> Icons.Default.HowToVote
    LiveEventIconType.SOCCER -> Icons.Default.SportsSoccer
}
val gradientColors = event.gradientType.colors
```

---

## Priority 6: AuthRepositoryImpl + Firebase

Add real Firebase implementation:

```kotlin
// BEFORE (stub)
class AuthRepositoryImpl : AuthRepository {
    override suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return AuthResult.Success(userId = "user_${email.hashCode()}")
    }
}

// AFTER (via DataSource abstraction)
class AuthRepositoryImpl(
    private val dataSource: AuthDataSource,
    private val handler: RepositoryResponseHandler,
) : AuthRepository {
    override fun signIn(email: String, password: String): Flow<UiState<AuthUser>> =
        handler.asUiStateFlow { dataSource.signInWithEmail(email, password) }

    override fun signInWithGoogle(): Flow<UiState<AuthUser>> =
        handler.asUiStateFlow { dataSource.signInWithGoogle() }
}
```

---

## Priority 7: Add Type-Safe Navigation

Replace string-based `currentScreen` navigation:

```kotlin
// BEFORE (string based)
var currentScreen by remember { mutableStateOf("login") }
when (currentScreen) {
    "login" -> LoginScreen(...)
    "home" -> HomeScreen(...)
}

// AFTER (type-safe NavHost with Atomic Subgraphs)
// navigation/AppRoute.kt
import kotlinx.serialization.Serializable

@Serializable data object OnboardingGraph
@Serializable data object AuthGraph
@Serializable data object HomeGraph
@Serializable data object PostGraph
@Serializable data object AppGraph // optionally wraps everything

@Serializable data object OnboardingRoute
@Serializable data object LoginRoute
@Serializable data object RegistrationRoute
@Serializable data object HomeRoute
@Serializable data object NotificationsRoute
@Serializable data object CreatePostRoute
@Serializable data object CreateProjectRoute
@Serializable data object CreateEventRoute
@Serializable data class ProjectDetailRoute(val projectId: String)

// feature/auth/presentation/navigation/AuthNavigation.kt
fun NavGraphBuilder.authGraph(navController: NavController) {
    navigation<AuthGraph>(startDestination = LoginRoute) {
        composable<LoginRoute> {
            val viewModel = koinViewModel<LoginViewModel>()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { navController.navigate(HomeGraph) {
                    popUpTo(AuthGraph) { inclusive = true }
                }},
                onNavigateToSignUp = { navController.navigate(RegistrationRoute) }
            )
        }
        composable<RegistrationRoute> { ... }
    }
}

// App.kt
@Composable
fun App() {
    FindrTheme(darkTheme = true) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = OnboardingGraph) {
            // Install subgraphs atomically
            onboardingGraph(navController)
            authGraph(navController)
            homeGraph(navController)
            postGraph(navController)
        }
    }
}
```

---

## Refactoring Checklist

### Auth Feature
- [ ] Extract LoginScreen sub-components to `components/`
- [ ] Replace raw Button/TextField with FindrPrimaryButton/FindrTextField
- [ ] Add `LoginNavEvent` SharedFlow for navigation
- [ ] Add Firebase AuthDataSource (expect/actual)
- [ ] Add AuthRepositoryImpl using DataSource
- [ ] Extract RegistrationScreen sub-components to `components/`
- [ ] Add field-level validation errors to RegistrationUiState

### Home Feature
- [ ] Extract all HomeScreen sub-components to `components/`
- [ ] Remove ImageVector/Color from HomeUiState — use enums
- [ ] Replace custom chip box with `FindrFilterChip`
- [ ] Add HomeNavEvent for one-shot navigation
- [ ] Load feed from repository (not hardcoded in ViewModel)

### Project Feature
- [ ] Move ProjectDetail data models to domain layer
- [ ] Add ProjectDetailViewModel + UiState + Event
- [ ] Extract ProjectDetailScreen sub-components to `components/`  
- [ ] Replace SkillTag with `FindrSuggestionChip`
- [ ] Add ProjectRepository + DataSource + UseCases

### Onboarding Feature
- [ ] Add OnboardingViewModel
- [ ] Add OnboardingNavEvent for navigation

### Post Feature
- [ ] Move PostCategory enum to domain layer
- [ ] Add data/domain layers for CreateProject and CreateEvent
- [ ] Extract CreateProjectScreen sub-components to `components/`
- [ ] Add real API/Firebase calls

### Core Infrastructure
- [ ] Add `core/network/response/NetworkResponse.kt`
- [ ] Add `core/ui/state/UiState.kt`
- [ ] Add `core/data/RepositoryResponseHandler.kt`
- [ ] Add Koin DI setup
- [ ] Add type-safe navigation (NavHost)
- [ ] Remove manual DI from App.kt
