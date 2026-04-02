package com.exa.android.reflekt.core.network.response

/**
 * Raw result from a DataSource (Firebase / REST API call).
 * Never used directly in UI — always converted to [UiState] via [RepositoryResponseHandler].
 */
sealed class NetworkResponse<out T> {

    /** API call succeeded with a response body. */
    data class Success<out T>(val data: T) : NetworkResponse<T>()

    /** API call returned an error HTTP status (4xx / 5xx). */
    data class ApiError<out T>(
        val code: Int,
        val message: String?,
    ) : NetworkResponse<T>()

    /** No network connectivity or I/O exception. */
    data class NetworkError<out T>(val exception: Exception) : NetworkResponse<T>()

    /** Any other unhandled exception. */
    data class UnknownError<out T>(val throwable: Throwable) : NetworkResponse<T>()
}

/** Convenience: maps to [NetworkResponse.ApiError] with the given code and message. */
fun <T> networkError(code: Int, message: String?): NetworkResponse<T> =
    NetworkResponse.ApiError(code, message)
