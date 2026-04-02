# Findr — Architecture Overview

## Project Structure

Findr is a **Kotlin Multiplatform (KMP)** project. All shared UI and business logic lives in `composeApp/src/commonMain`. Platform-only code is in `androidMain` / `iosMain`.

```
Findr/
├── composeApp/
│   └── src/
│       ├── commonMain/          ← Shared Compose UI + all business logic
│       │   └── kotlin/com/exa/android/reflekt/
│       │       ├── App.kt                    ← App entry point / root composable
│       │       ├── feature/                  ← Feature modules (one folder per feature)
│       │       └── ui/                       ← Design system (components + theme)
│       ├── androidMain/         ← Android platform specifics
│       └── iosMain/             ← iOS platform specifics
├── iosApp/                      ← iOS project wrapper
└── shared/                      ← Optional KMP shared module
```

---

## Clean Architecture

Each feature follows a strict 3-layer separation:

```
┌─────────────────────────────────────────────────────────────┐
│  Presentation Layer                                          │
│  Screen.kt · ViewModel.kt · UiState.kt · Event.kt          │
├─────────────────────────────────────────────────────────────┤
│  Domain Layer                                               │
│  UseCase.kt · Repository interface · Domain models          │
├─────────────────────────────────────────────────────────────┤
│  Data Layer                                                 │
│  RepositoryImpl.kt · DataSource interface + impl · DTOs     │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Rule

> Outer layers depend on inner layers. Inner layers never import from outer layers.

```
Presentation → Domain ← Data
                 ↑
           (interfaces only)
```

---

## Data Flow

```
UI Event
   │
   ▼
ViewModel.onEvent(event)
   │
   ▼
UseCase.invoke(params)
   │
   ▼
Repository.someFlow(): Flow<UiState>
   │
   ▼  (DataSource wraps Firebase/API call)
DataSource.fetch(): NetworkResponse<T>
   │
   ▼  (mapped through RepositoryResponseHandler)
Flow<UiState<T>>
   │
   ▼
ViewModel._uiState.update
   │
   ▼
Screen collects uiState
   │
   ▼
Composable renders based on UiState.ContentState
```

---

## Layer Responsibilities

### Presentation
| File | Responsibility |
|------|---------------|
| `*Screen.kt` | Render UI, collect state, forward events to ViewModel |
| `*ViewModel.kt` | Hold `_uiState`, process events, call use cases |
| `*UiState.kt` | Immutable data class for the complete screen state |
| `*Event.kt` | `sealed class` for all user interactions |

### Domain
| File | Responsibility |
|------|---------------|
| `*UseCase.kt` | Single business operation, calls repository, validates inputs |
| `*Repository.kt` | Interface — declares data operations (returns `Flow<UiState<T>>`) |
| `domain/model/` | Pure Kotlin data classes (no Android/Firebase imports) |

### Data
| File | Responsibility |
|------|---------------|
| `*RepositoryImpl.kt` | Implements domain interface, delegates to datasource |
| `*DataSource.kt` | Interface with one method per remote/local call |
| `*DataSourceImpl.kt` | Calls Firebase/REST, returns `NetworkResponse<T>` |
| `dto/` | Network DTOs (annotated for serialization) |
| `mapper/` | Extension functions mapping DTO → domain model |

---

## KMP Strategy

| Layer | Where code lives | Notes |
|-------|-----------------|-------|
| Domain models | `commonMain` | Pure Kotlin, no platform deps |
| Repository interfaces | `commonMain` | No platform deps |
| Use cases | `commonMain` | No platform deps |
| ViewModel | `commonMain` | Uses `androidx.lifecycle:lifecycle-viewmodel` (KMP-compatible) |
| Compose UI | `commonMain` | Compose Multiplatform |
| DataSource interfaces | `commonMain` | Abstract away Firebase |
| Firebase DataSourceImpl | `androidMain` | Android-specific Firebase SDK |
| iOS DataSourceImpl | `iosMain` | iOS Firebase SDK via `expect/actual` |

### `expect/actual` Pattern for Platform Code

```kotlin
// commonMain
expect class FirebaseAuthProvider {
    suspend fun signInWithEmail(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(): AuthResult
}

// androidMain
actual class FirebaseAuthProvider {
    actual suspend fun signInWithEmail(...) = ... // Android Firebase SDK
}

// iosMain
actual class FirebaseAuthProvider {
    actual suspend fun signInWithEmail(...) = ... // iOS Firebase SDK
}
```

---

## Feature Directory Structure (per feature)

```
feature/
└── auth/
    ├── data/
    │   ├── datasource/
    │   │   ├── AuthDataSource.kt           ← interface
    │   │   └── FirebaseAuthDataSourceImpl.kt  ← expect/actual impl
    │   ├── dto/
    │   │   └── AuthResponseDto.kt
    │   ├── mapper/
    │   │   └── AuthMapper.kt
    │   └── repository/
    │       └── AuthRepositoryImpl.kt
    ├── domain/
    │   ├── model/
    │   │   ├── AuthResult.kt
    │   │   └── User.kt
    │   ├── repository/
    │   │   └── AuthRepository.kt           ← interface
    │   └── usecase/
    │       ├── SignInWithEmailUseCase.kt
    │       └── SignInWithGoogleUseCase.kt
    └── presentation/
        ├── login/
        │   ├── LoginScreen.kt
        │   ├── LoginViewModel.kt
        │   ├── LoginUiState.kt
        │   └── LoginEvent.kt
        ├── registration/
        │   └── ...
        └── components/                    ← Feature-private sub-components
            ├── AuthCard.kt
            ├── GoogleSignInButton.kt
            └── OrDivider.kt
```

---

## Navigation Architecture (Atomic Subgraphs)

Findr uses Jetpack Compose Navigation (`org.jetbrains.androidx.navigation:navigation-compose`) configured with type-safe routing via `kotlinx.serialization` and an **Atomic Subgraph Strategy**.

### The Subgraph Strategy
Instead of defining all routes in a single mammoth `NavHost`, we decouple routing by defining feature-specific subgraphs.

```kotlin
// In commonMain/navigation/AppRoute.kt
@Serializable data object AuthGraph
@Serializable data object HomeGraph
@Serializable data object OnboardingGraph
```

Each module exposes an extension function `NavGraphBuilder.<feature>Graph(navController)` that encapsulates its children nodes. The root `App.kt` simply installs these subgraphs into a primary `NavHost`.

### Bottom Navigation
The bottom navigation components reside inside their specific subgraph (e.g. `HomeGraph`) and update by observing `navController.currentBackStackEntryAsState()`.

> **See `04_NAVIGATION.md`** for full implementation details.

---

## Dependency Injection (DI)

Currently using manual DI in `App.kt`. Target: **Koin** (KMP compatible).

```kotlin
// Module definition (commonMain)
val authModule = module {
    single<AuthDataSource>    { FirebaseAuthDataSourceImpl() }
    single<AuthRepository>    { AuthRepositoryImpl(get()) }
    factory { SignInWithEmailUseCase(get()) }
    factory { SignInWithGoogleUseCase(get()) }
    viewModel { LoginViewModel(get(), get()) }
}
```

> **See `06_DEPENDENCY_INJECTION.md`** for full Koin setup.
