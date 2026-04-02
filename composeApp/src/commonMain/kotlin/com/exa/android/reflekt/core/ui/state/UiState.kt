package com.exa.android.reflekt.core.ui.state

/**
 * UI-ready state wrapper. All async data in screen UiState data classes uses this type.
 *
 * Usage pattern in a screen UiState:
 * ```kotlin
 * data class ProjectListUiState(
 *     val projects: UiState<List<Project>> = UiState.Idle,
 * )
 * ```
 *
 * Usage pattern in a Composable:
 * ```kotlin
 * when (val state = uiState.projects) {
 *     UiState.Idle    -> Unit
 *     is UiState.Loading  -> ProjectListSkeleton()
 *     is UiState.Success  -> ProjectList(state.data)
 *     is UiState.Error    -> NetworkErrorView(message = state.error.message, onRetry = { ... })
 * }
 * ```
 */
sealed class UiState<out T> {

    /** Not yet started — before the first load is triggered. */
    data object Idle : UiState<Nothing>()

    /**
     * Loading in progress. Optionally carries [data] for optimistic/stale display
     * (e.g., show shimmer skeleton on top of a previously loaded list).
     */
    data class Loading<out T>(val data: T? = null) : UiState<T>()

    /** Data successfully loaded. */
    data class Success<out T>(val data: T) : UiState<T>()

    /** Load failed with a structured error. */
    data class Error(val error: AppError) : UiState<Nothing>()
}

// ─── Convenience extensions ───────────────────────────────────────────────────

val <T> UiState<T>.isLoading: Boolean get() = this is UiState.Loading
val <T> UiState<T>.isSuccess: Boolean get() = this is UiState.Success
val <T> UiState<T>.isError: Boolean   get() = this is UiState.Error

/** Returns the [Success] data, or null if not in success state. */
val <T> UiState<T>.dataOrNull: T? get() = (this as? UiState.Success)?.data
