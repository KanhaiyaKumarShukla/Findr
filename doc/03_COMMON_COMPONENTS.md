# Findr — Common UI Components

All shared components live in:
```
composeApp/src/commonMain/kotlin/com/exa/android/reflekt/ui/components/
```

> **RULE: Never write raw Compose primitives (Button, OutlinedTextField, etc.) inside feature screens if a Findr component exists. Always use the Findr-prefixed components.**

---

## Component Index

| Component | File | Purpose |
|-----------|------|---------|
| `FindrPrimaryButton` | `button/FindrButton.kt` | Primary CTA button with loading state |
| `FindrOutlinedButton` | `button/FindrButton.kt` | Secondary / outlined action button |
| `FindrTextButton` | `button/FindrButton.kt` | Ghost/text-only button |
| `FindrSocialButton` | `button/FindrButton.kt` | OAuth provider button (Google, Apple) |
| `FindrTextField` | `textfield/FindrTextField.kt` | Outlined text field with error + icons |
| `FindrFilterChip` | `chip/FindrChip.kt` | Toggleable filter chip |
| `FindrInputChip` | `chip/FindrChip.kt` | Dismissible tag chip |
| `FindrSuggestionChip` | `chip/FindrChip.kt` | Read-only suggestion chip |
| `FindrChipGroup` | `chip/FindrChip.kt` | Wrapping row of filter chips |
| `FindrTopBar` | `topbar/FindrTopBar.kt` | Left-aligned top bar |
| `FindrCenterTopBar` | `topbar/FindrTopBar.kt` | Center-aligned top bar |
| `FindrScaffold` | `scaffold/FindrScaffold.kt` | Full-screen scaffold + snackbar |
| `FindrErrorSnackbar` | `scaffold/FindrScaffold.kt` | Slide-up error snackbar |
| `FindrDialog` | `dialog/FindrDialog.kt` | Two-action confirmation dialog |
| `FindrInfoDialog` | `dialog/FindrDialog.kt` | Single-dismiss info dialog |
| `NetworkErrorView` | `error/NetworkErrorView.kt` | Full-screen error state |
| `InlineErrorBanner` | `error/NetworkErrorView.kt` | Inline error banner |
| `ShimmerBox` | `loading/ShimmerBox.kt` | Rectangular shimmer placeholder |
| `ShimmerCircle` | `loading/ShimmerBox.kt` | Circular shimmer placeholder |
| `UserCardSkeleton` | `loading/ShimmerBox.kt` | Pre-built profile card skeleton |
| `FindrCircularLoader` | `loading/FindrLoadingIndicator.kt` | Simple circular progress |
| `ThreeDotLoadingIndicator` | `loading/FindrLoadingIndicator.kt` | 3-dot pulse loader |
| `FindrFullScreenLoader` | `loading/FindrLoadingIndicator.kt` | Centered full-screen loader |
| `Modifier.shimmer()` | `modifier/ShimmerModifier.kt` | Shimmer sweep effect modifier |
| `Modifier.throttleClick()` | `modifier/ThrottleClickModifier.kt` | Debounced click modifier |
| `Modifier.dashedBorder()` | `modifier/DashedBorderModifier.kt` | Dashed border modifier |
| `SpacerXS/S/M/L/XL` | `foundation/Spacers.kt` | Named spacer shorthands |

---

## Buttons — `button/FindrButton.kt`

### `FindrPrimaryButton`
Full-width filled button with built-in loading spinner.

```kotlin
FindrPrimaryButton(
    text = "Sign In",
    onClick = { viewModel.onEvent(LoginEvent.SignInClicked) },
    isLoading = uiState.isLoading,
    enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank(),
    trailingIcon = {
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, Modifier.size(18.dp))
    }
)
```

### `FindrOutlinedButton`
Secondary action. Typical use: Cancel, Go Back, Skip.

```kotlin
FindrOutlinedButton(
    text = "Cancel",
    onClick = onCancel,
    leadingIcon = { Icon(Icons.Default.Close, contentDescription = null) }
)
```

### `FindrTextButton`
Ghost button. Typical use: "Forgot password?", "View all".

```kotlin
FindrTextButton(text = "Forgot password?", onClick = onForgotPassword)
```

### `FindrSocialButton`
White pill button for OAuth. Supply any icon composable.

```kotlin
FindrSocialButton(
    text = "Continue with Google",
    icon = { Text("G", fontWeight = FontWeight.Bold, color = GoogleBlue) },
    onClick = { viewModel.onEvent(LoginEvent.GoogleSignInClicked) },
    isLoading = uiState.isGoogleLoading,
)
```

---

## Text Field — `textfield/FindrTextField.kt`

Single component for all text inputs. Handles error state automatically.

```kotlin
FindrTextField(
    value = uiState.email,
    onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
    placeholder = "Email address",
    leadingIcon = Icons.Default.Email,
    errorMessage = uiState.emailError,  // null = no error shown
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next,
    ),
    keyboardActions = KeyboardActions(
        onNext = { focusManager.moveFocus(FocusDirection.Down) }
    ),
)

// Password field with trailing toggle
FindrTextField(
    value = uiState.password,
    onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
    placeholder = "Password",
    leadingIcon = Icons.Default.Lock,
    trailingIcon = {
        IconButton(onClick = { viewModel.onEvent(LoginEvent.TogglePasswordVisibility) }) {
            Icon(
                imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility 
                              else Icons.Default.VisibilityOff,
                contentDescription = null,
            )
        }
    },
    visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None 
                           else PasswordVisualTransformation(),
)
```

---

## Chips — `chip/FindrChip.kt`

### `FindrFilterChip` — toggleable selection
```kotlin
FindrFilterChip(
    label = "Projects",
    selected = uiState.activeFilter == Filter.PROJECTS,
    onToggle = { viewModel.onEvent(HomeEvent.FilterSelected(Filter.PROJECTS)) },
)
```

### `FindrChipGroup` — wrapping row from a list
```kotlin
FindrChipGroup(
    items = listOf("Kotlin", "Python", "React"),
    selectedItems = uiState.selectedSkills,
    onToggle = { skill -> viewModel.onEvent(CreateProjectEvent.ToggleSkill(skill)) },
)
```

### `FindrInputChip` — dismissible tag
```kotlin
uiState.selectedSkills.forEach { skill ->
    FindrInputChip(
        label = skill,
        onRemove = { viewModel.onEvent(CreateProjectEvent.RemoveSkill(skill)) },
    )
}
```

---

## Top Bars — `topbar/FindrTopBar.kt`

### `FindrTopBar` — standard left-aligned
```kotlin
FindrTopBar(
    title = "Create Project",
    onNavigateUp = { viewModel.onEvent(CreateProjectEvent.BackClicked) },
    actions = {
        IconButton(onClick = { viewModel.onEvent(CreateProjectEvent.PreviewClicked) }) {
            Icon(Icons.Default.Preview, contentDescription = "Preview")
        }
    }
)
```

### `FindrCenterTopBar` — modal / detail screens
```kotlin
FindrCenterTopBar(
    title = "Project Details",
    onNavigateUp = onBack,
)
```

---

## Scaffold — `scaffold/FindrScaffold.kt`

Standard screen wrapper. Use for all screens that need a top bar.

```kotlin
@Composable
fun CreateProjectScreen(viewModel: CreateProjectViewModel, onCancel: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar on error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onEvent(CreateProjectEvent.DismissError)
        }
    }

    FindrScaffold(
        title = "New Project",
        onNavigateUp = onCancel,
        snackbarHostState = snackbarHostState,
        bottomBar = {
            FindrPrimaryButton(
                text = "Post Project",
                onClick = { viewModel.onEvent(CreateProjectEvent.PostProject) },
                isLoading = uiState.isPosting,
                modifier = Modifier.padding(16.dp),
            )
        }
    ) { padding ->
        CreateProjectContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(padding),
        )
    }
}
```

### `FindrErrorSnackbar` — standalone error snackbar
```kotlin
Box(Modifier.fillMaxSize()) {
    // screen content
    FindrErrorSnackbar(
        message = uiState.errorMessage,
        onDismiss = { viewModel.onEvent(MyEvent.DismissError) },
        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
    )
}
```

---

## Dialogs — `dialog/FindrDialog.kt`

### `FindrDialog` — two-action confirmation
```kotlin
if (uiState.showDeleteDialog) {
    FindrDialog(
        title = "Delete Project",
        message = "This action cannot be undone.",
        confirmLabel = "Delete",
        dismissLabel = "Cancel",
        onConfirm = { viewModel.onEvent(ProjectDetailEvent.ConfirmDelete) },
        onDismiss = { viewModel.onEvent(ProjectDetailEvent.DismissDeleteDialog) },
        icon = Icons.Default.Delete,
        isDestructive = true,
    )
}
```

### `FindrInfoDialog` — single dismiss
```kotlin
FindrInfoDialog(
    title = "Application Sent!",
    message = "The project owner will review your application.",
    icon = Icons.Default.CheckCircle,
    onDismiss = { viewModel.onEvent(ProjectDetailEvent.DismissSuccessDialog) },
)
```

---

## Error Views — `error/NetworkErrorView.kt`

### `NetworkErrorView` — full-screen error
```kotlin
when (val state = uiState.projects) {
    is UiState.Error -> NetworkErrorView(
        errorType = state.error.type.toDisplayType(),
        message = state.error.message,
        onRetry = { viewModel.onEvent(ProjectListEvent.Retry) },
        onSecondaryAction = onBack,
        secondaryLabel = "Go Back",
    )
    // ...
}
```

### `InlineErrorBanner` — partial section error
```kotlin
if (uiState.commentsError != null) {
    InlineErrorBanner(
        message = uiState.commentsError,
        onRetry = { viewModel.onEvent(ProjectDetailEvent.RetryComments) },
    )
}
```

---

## Loading / Shimmer — `loading/`

### Individual shimmer primitives
```kotlin
// Rectangular placeholder
ShimmerBox(height = 14.dp, cornerRadius = 4.dp)
ShimmerBox(height = 12.dp, width = 120.dp, cornerRadius = 4.dp)

// Circular avatar placeholder
ShimmerCircle(size = 40.dp)

// Pre-built card skeleton
UserCardSkeleton()
```

### `Modifier.shimmer()` — apply shimmer to any composable
```kotlin
Box(
    Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(16.dp))
        .shimmer()
)
```

### Loaders
```kotlin
// Centered page-level loader
FindrFullScreenLoader(modifier = Modifier.fillMaxSize())

// Inline loader (e.g., inside a button)
FindrCircularLoader(size = 24.dp)

// Three-dot typing-style loader
ThreeDotLoadingIndicator()
```

---

## Modifiers — `modifier/`

### `Modifier.throttleClick()` — debounce rapid taps
```kotlin
// Use instead of raw .clickable{} for navigation/submit actions
Box(
    Modifier.throttleClick(throttleMs = 600L) {
        viewModel.onEvent(HomeEvent.ProjectEnrollClicked(project.id))
    }
)
```

### `Modifier.dashedBorder()` — dashed border
```kotlin
Box(
    Modifier
        .fillMaxWidth()
        .dashedBorder(
            strokeWidth = 1.dp,
            color = MaterialTheme.colorScheme.outline,
            cornerRadius = 12.dp,
        )
)
```

---

## Foundation — `foundation/Spacers.kt`

Named spacer shorthands for consistent spacing:
```kotlin
SpacerXS()  // 4.dp
SpacerS()   // 8.dp 
SpacerM()   // 16.dp
SpacerL()   // 24.dp
SpacerXL()  // 32.dp
```

---

## Theme Access

```kotlin
@Composable
fun MyComposable() {
    val colors = MaterialTheme.colorScheme      // standard M3 colors
    val appColors = MaterialTheme.appColors     // Findr custom colors
    val typography = MaterialTheme.typography   // standard M3 typography
}

// AppColorScheme tokens
appColors.accentBlue
appColors.accentGreen
appColors.accentPurple
appColors.accentYellow
appColors.accentOrange
appColors.cardBackground
appColors.cardBorder
appColors.formCardBackground
appColors.googleBlue
appColors.iconOnAccent
appColors.error
appColors.onError
```

---

## Component Checklist for PRs

Before submitting any PR, verify:

- [ ] All `Button` usages replaced with `FindrPrimaryButton` / `FindrOutlinedButton` / `FindrTextButton`
- [ ] All `OutlinedTextField` usages replaced with `FindrTextField`
- [ ] All `FilterChip` / `InputChip` usages replaced with `FindrFilterChip` / `FindrInputChip`
- [ ] All `TopAppBar` usages replaced with `FindrTopBar` / `FindrCenterTopBar`
- [ ] Loading states use `ShimmerBox` / `ShimmerCircle` or `FindrFullScreenLoader`
- [ ] Error states use `NetworkErrorView` or `InlineErrorBanner`
- [ ] Navigation triggers use `Modifier.throttleClick()` (not raw `.clickable{}`)
- [ ] Dialogs use `FindrDialog` / `FindrInfoDialog`
