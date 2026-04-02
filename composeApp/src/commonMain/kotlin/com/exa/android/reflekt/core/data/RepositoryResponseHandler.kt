package com.exa.android.reflekt.core.data

import com.exa.android.reflekt.core.network.response.NetworkResponse
import com.exa.android.reflekt.core.ui.state.AppError
import com.exa.android.reflekt.core.ui.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Centralises the conversion of [NetworkResponse] into [Flow<UiState<T>>].
 *
 * Every repository implementation delegates to this class:
 * ```kotlin
 * class ProjectRepositoryImpl(
 *     private val dataSource: ProjectDataSource,
 *     private val handler: RepositoryResponseHandler,
 * ) : ProjectRepository {
 *     override fun getProject(id: String): Flow<UiState<Project>> =
 *         handler.asUiStateFlow { dataSource.getProject(id).map { it.toDomain() } }
 * }
 * ```
 */
class RepositoryResponseHandler {

    /**
     * Wraps a single [NetworkResponse] call into a [Flow] that emits:
     * 1. [UiState.Loading] immediately
     * 2. [UiState.Success] or [UiState.Error] once [execute] completes
     */
    fun <T> asUiStateFlow(
        execute: suspend () -> NetworkResponse<T>,
    ): Flow<UiState<T>> = flow {
        emit(UiState.Loading())
        emit(execute().toUiState())
    }
}

// ─── Extension: NetworkResponse → UiState ────────────────────────────────────

fun <T> NetworkResponse<T>.toUiState(): UiState<T> = when (this) {
    is NetworkResponse.Success      -> UiState.Success(data)
    is NetworkResponse.ApiError     -> UiState.Error(AppError.fromCode(code, message))
    is NetworkResponse.NetworkError -> UiState.Error(AppError.noInternet())
    is NetworkResponse.UnknownError -> UiState.Error(AppError.general(throwable.message))
}

/** Maps one generic type to another while preserving the [NetworkResponse] wrapper. */
fun <A, B> NetworkResponse<A>.map(transform: (A) -> B): NetworkResponse<B> = when (this) {
    is NetworkResponse.Success      -> NetworkResponse.Success(transform(data))
    is NetworkResponse.ApiError     -> NetworkResponse.ApiError(code, message)
    is NetworkResponse.NetworkError -> NetworkResponse.NetworkError(exception)
    is NetworkResponse.UnknownError -> NetworkResponse.UnknownError(throwable)
}
