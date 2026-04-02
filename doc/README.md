# Findr Documentation

## Index

| File | What it covers |
|------|---------------|
| [01_ARCHITECTURE_OVERVIEW.md](./01_ARCHITECTURE_OVERVIEW.md) | Clean Architecture layers, KMP strategy, data flow diagram, feature directory layout, navigation plan |
| [02_DATA_FLOW.md](./02_DATA_FLOW.md) | `NetworkResponse`, `UiState`, `AppError`, `RepositoryResponseHandler`, screen rendering patterns, one-shot navigation events |
| [03_COMMON_COMPONENTS.md](./03_COMMON_COMPONENTS.md) | All Findr-prefixed components: usage, props, code examples, component checklist |
| [04_FILE_STRUCTURE.md](./04_FILE_STRUCTURE.md) | Full directory tree, naming conventions, file size limits, import rules |
| [05_CODING_RULES.md](./05_CODING_RULES.md) | Golden rules, event pattern, UiState pattern, enum usage, click handling, loading/error standards, anti-patterns |
| [06_FIREBASE_DATASOURCE.md](./06_FIREBASE_DATASOURCE.md) | Data source abstraction, Firebase implementation templates, mapper pattern, real-time updates, error code mapping |
| [07_REFACTORING_GUIDE.md](./07_REFACTORING_GUIDE.md) | Concrete refactoring checklist per feature, before/after code comparisons |

---

## Quick Reference

### Three-Layer Flow
```
Screen → ViewModel → UseCase → Repository → DataSource
   ↑                                              ↓
UiState ← StateFlow ← Flow<UiState<T>> ← NetworkResponse<T>
```

### Key Types
| Type | Location | Role |
|------|----------|------|
| `NetworkResponse<T>` | `core/network/response/` | Raw datasource result |
| `UiState<T>` | `core/ui/state/` | UI-ready result |
| `AppError` | `core/ui/state/` | Structured error |
| `RepositoryResponseHandler` | `core/data/` | Converter |

### Findr Component Quick Picks
| Scenario | Component |
|----------|-----------|
| Primary action button | `FindrPrimaryButton` |
| Text input | `FindrTextField` |
| Select filter | `FindrFilterChip` |
| Dismiss tag | `FindrInputChip` |
| Top bar | `FindrTopBar` / `FindrCenterTopBar` |
| Screen wrapper | `FindrScaffold` |
| Full-screen error | `NetworkErrorView` |
| Full-screen loading | `FindrFullScreenLoader` |
| Skeleton placeholder | `ShimmerBox` / `ShimmerCircle` |
| Confirmation dialog | `FindrDialog` |
| Debounce clicks | `Modifier.throttleClick()` |

### Golden Rules (Summary)
1. No business logic in composables
2. Always use Findr-prefixed components
3. Domain layer = pure Kotlin (zero Android/Firebase imports)
4. Firebase only in `androidMain` / `iosMain`
5. Events are sealed classes
6. Sub-components receive `state + onEvent(lambda)`, never ViewModel
7. Loading → `ShimmerBox` skeleton; Error → `NetworkErrorView`

---

> **Original architecture reference:** [ARCHITECTURE_GUIDE.md](./ARCHITECTURE_GUIDE.md) — contains the generic Clean Architecture implementation used as the baseline for this app.
