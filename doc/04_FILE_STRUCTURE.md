# Findr — File & Directory Structure

## Root Module Layout

```
composeApp/src/commonMain/kotlin/com/exa/android/reflekt/
├── App.kt                                  ← Root composable, NavHost (target)
├── core/                                   ← Cross-cutting concerns
│   ├── network/
│   │   ├── response/
│   │   │   ├── NetworkResponse.kt          ← Sealed raw API result
│   │   │   └── AppError.kt                 ← Structured error type
│   │   └── handler/
│   │       └── RepositoryResponseHandler.kt ← NetworkResponse → UiState converter
│   ├── ui/
│   │   └── state/
│   │       └── UiState.kt                  ← Sealed UI state wrapper
│   └── util/
│       ├── StringWrapper.kt                ← i18n / res-string wrapper
│       └── DateTimeUtils.kt                ← Shared date formatting
├── feature/
│   ├── auth/
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   ├── AuthDataSource.kt            ← interface
│   │   │   │   └── FirebaseAuthDataSource.kt    ← expect (platform impl)
│   │   │   ├── dto/
│   │   │   │   └── AuthResponseDto.kt
│   │   │   ├── mapper/
│   │   │   │   └── AuthMapper.kt
│   │   │   └── repository/
│   │   │       └── AuthRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── AuthResult.kt
│   │   │   │   └── User.kt
│   │   │   ├── repository/
│   │   │   │   └── AuthRepository.kt            ← interface
│   │   │   └── usecase/
│   │   │       ├── SignInWithEmailUseCase.kt
│   │   │       ├── SignInWithGoogleUseCase.kt
│   │   │       ├── SignUpUseCase.kt
│   │   │       └── SignOutUseCase.kt
│   │   └── presentation/
│   │       ├── login/
│   │       │   ├── LoginScreen.kt               ← root composable only
│   │       │   ├── LoginViewModel.kt
│   │       │   ├── LoginUiState.kt
│   │       │   ├── LoginEvent.kt
│   │       │   ├── LoginNavEvent.kt             ← one-shot navigation events
│   │       │   └── components/                  ← private sub-components
│   │       │       ├── AuthCard.kt
│   │       │       ├── GoogleSignInButton.kt
│   │       │       ├── LogoSection.kt
│   │       │       ├── OrDivider.kt
│   │       │       ├── BackgroundDecorations.kt
│   │       │       └── FooterSection.kt
│   │       └── registration/
│   │           ├── RegistrationScreen.kt
│   │           ├── RegistrationViewModel.kt
│   │           ├── RegistrationUiState.kt
│   │           ├── RegistrationEvent.kt
│   │           ├── RegistrationNavEvent.kt
│   │           └── components/
│   │               ├── StepIndicator.kt
│   │               ├── StepOne.kt              ← credentials
│   │               ├── StepTwo.kt              ← academic info
│   │               └── StepThree.kt            ← interests
│   ├── home/
│   │   └── presentation/
│   │       ├── HomeScreen.kt
│   │       ├── HomeViewModel.kt
│   │       ├── HomeUiState.kt
│   │       ├── HomeEvent.kt
│   │       ├── HomeNavEvent.kt
│   │       └── components/
│   │           ├── TopNavBar.kt
│   │           ├── BottomNavBar.kt
│   │           ├── LiveNowSection.kt
│   │           ├── LiveEventCard.kt
│   │           ├── TrendingNewsTicker.kt
│   │           ├── FilterChipsRow.kt
│   │           ├── FeedPostCard.kt
│   │           ├── ProjectPostCard.kt
│   │           ├── BugPostCard.kt
│   │           └── AnnouncementCard.kt
│   ├── onboarding/
│   │   └── presentation/
│   │       ├── OnboardingScreen.kt
│   │       ├── OnboardingViewModel.kt            ← new: manage state properly
│   │       ├── OnboardingUiState.kt
│   │       ├── OnboardingEvent.kt
│   │       └── components/
│   │           ├── OnboardingPageContent.kt
│   │           ├── CollaborationIllustration.kt
│   │           ├── CareerIllustration.kt
│   │           └── CommunityIllustration.kt
│   ├── post/
│   │   └── presentation/
│   │       ├── selection/
│   │       │   ├── CreatePostSelectionScreen.kt
│   │       │   ├── CreatePostSelectionViewModel.kt
│   │       │   ├── CreatePostSelectionUiState.kt
│   │       │   └── CreatePostSelectionEvent.kt
│   │       ├── create_project/
│   │       │   ├── CreateProjectScreen.kt
│   │       │   ├── CreateProjectViewModel.kt
│   │       │   ├── CreateProjectUiState.kt
│   │       │   ├── CreateProjectEvent.kt
│   │       │   └── components/
│   │       │       ├── ProjectTitleSection.kt
│   │       │       ├── SkillTagsSection.kt
│   │       │       ├── OpeningsCounter.kt
│   │       │       └── AttachmentSection.kt
│   │       └── create_event/
│   │           ├── CreateEventScreen.kt
│   │           ├── CreateEventViewModel.kt
│   │           ├── CreateEventUiState.kt
│   │           ├── CreateEventEvent.kt
│   │           └── components/
│   │               ├── EventDatePicker.kt
│   │               └── EventLocationSection.kt
│   ├── project/
│   │   ├── data/
│   │   │   ├── datasource/
│   │   │   │   ├── ProjectDataSource.kt
│   │   │   │   └── FirebaseProjectDataSource.kt
│   │   │   ├── dto/
│   │   │   │   └── ProjectDto.kt
│   │   │   ├── mapper/
│   │   │   │   └── ProjectMapper.kt
│   │   │   └── repository/
│   │   │       └── ProjectRepositoryImpl.kt
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   │   ├── Project.kt
│   │   │   │   ├── OpenRole.kt
│   │   │   │   └── Collaborator.kt
│   │   │   ├── repository/
│   │   │   │   └── ProjectRepository.kt
│   │   │   └── usecase/
│   │   │       ├── GetProjectDetailUseCase.kt
│   │   │       └── EnrollInProjectUseCase.kt
│   │   └── presentation/
│   │       ├── detail/
│   │       │   ├── ProjectDetailScreen.kt
│   │       │   ├── ProjectDetailViewModel.kt
│   │       │   ├── ProjectDetailUiState.kt
│   │       │   ├── ProjectDetailEvent.kt
│   │       │   └── components/
│   │       │       ├── HeroSection.kt
│   │       │       ├── ProjectHeaderCard.kt
│   │       │       ├── TeamStatusSection.kt
│   │       │       ├── AboutSection.kt
│   │       │       ├── OpenRolesSection.kt
│   │       │       ├── RoleCard.kt
│   │       │       ├── CollaboratorsSection.kt
│   │       │       ├── DiscussionSection.kt
│   │       │       └── EnrollButton.kt
│   │       └── list/
│   │           ├── ProjectListScreen.kt
│   │           ├── ProjectListViewModel.kt
│   │           ├── ProjectListUiState.kt
│   │           └── ProjectListEvent.kt
│   └── notification/
│       └── presentation/
│           ├── NotificationScreen.kt
│           ├── NotificationViewModel.kt
│           ├── NotificationUiState.kt
│           ├── NotificationEvent.kt
│           └── components/
│               └── NotificationItem.kt
└── ui/
    ├── components/
    │   ├── button/
    │   │   └── FindrButton.kt
    │   ├── chip/
    │   │   └── FindrChip.kt
    │   ├── dialog/
    │   │   └── FindrDialog.kt
    │   ├── error/
    │   │   └── NetworkErrorView.kt
    │   ├── foundation/
    │   │   └── Spacers.kt
    │   ├── loading/
    │   │   ├── FindrLoadingIndicator.kt
    │   │   └── ShimmerBox.kt
    │   ├── modifier/
    │   │   ├── DashedBorderModifier.kt
    │   │   ├── ShimmerModifier.kt
    │   │   └── ThrottleClickModifier.kt
    │   ├── scaffold/
    │   │   └── FindrScaffold.kt
    │   ├── textfield/
    │   │   └── FindrTextField.kt
    │   └── topbar/
    │       └── FindrTopBar.kt
    └── theme/
        ├── AppColorScheme.kt               ← custom color tokens
        ├── Color.kt                        ← raw color values
        ├── Theme.kt                        ← MaterialTheme wrapper
        └── Type.kt                        ← typography scale
```

---

## Platform-Specific Directories

```
composeApp/src/androidMain/kotlin/com/exa/android/reflekt/
├── feature/
│   └── auth/
│       └── data/
│           └── datasource/
│               └── FirebaseAuthDataSourceImpl.kt  ← Android Firebase
└── MainActivity.kt

composeApp/src/iosMain/kotlin/com/exa/android/reflekt/
└── feature/
    └── auth/
        └── data/
            └── datasource/
                └── FirebaseAuthDataSourceImpl.kt  ← iOS Firebase
```

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| Feature screens | `<Feature>Screen.kt` | `LoginScreen.kt` |
| ViewModels | `<Feature>ViewModel.kt` | `LoginViewModel.kt` |
| UI State | `<Feature>UiState.kt` | `LoginUiState.kt` |
| Events | `<Feature>Event.kt` | `LoginEvent.kt` |
| Nav events | `<Feature>NavEvent.kt` | `LoginNavEvent.kt` |
| Repository interfaces | `<Feature>Repository.kt` | `AuthRepository.kt` |
| Repository impls | `<Feature>RepositoryImpl.kt` | `AuthRepositoryImpl.kt` |
| DataSource interfaces | `<Feature>DataSource.kt` | `AuthDataSource.kt` |
| DataSource impls | `Firebase<Feature>DataSourceImpl.kt` | `FirebaseAuthDataSourceImpl.kt` |
| Use cases | `<Verb><Noun>UseCase.kt` | `SignInWithEmailUseCase.kt` |
| Domain models | `<Entity>.kt` | `Project.kt`, `User.kt` |
| DTOs | `<Entity>Dto.kt` | `ProjectDto.kt` |
| Mappers | `<Entity>Mapper.kt` | `ProjectMapper.kt` |
| Sub-components | `<Descriptive name>.kt` | `AuthCard.kt`, `RoleCard.kt` |
| Shared components | `Findr<Name>.kt` | `FindrButton.kt`, `FindrTextField.kt` |

---

## File Size Limits

| File type | Max lines | Action if exceeded |
|-----------|-----------|--------------------|
| Screen composable | 200 | Extract sub-components to `components/` |
| ViewModel | 250 | Extract domain logic to use cases |
| UiState | 50 | Split into sub-states if > 10 fields |
| Event sealed class | 30 | Group related events with sub-sealed-class |
| Component file | 150 | Split into multiple files |

---

## Import Rules

### Always import from `ui/components/` not raw Compose
```kotlin
// ✅ Correct
import com.exa.android.reflekt.ui.components.button.FindrPrimaryButton
import com.exa.android.reflekt.ui.components.textfield.FindrTextField

// ❌ Never in feature screens
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
```

### Domain layer: no Android/Compose imports
```kotlin
// ✅ Correct — pure Kotlin domain model
data class Project(
    val id: String,
    val title: String,
    val description: String,
    val createdAt: Long,
)

// ❌ Never in domain/model/
import androidx.compose.ui.graphics.Color
import com.google.firebase.firestore.DocumentSnapshot
```

### Data layer: no Compose UI imports
```kotlin
// ✅ Correct — DTO with serialization only
@Serializable
data class ProjectDto(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
)
```
