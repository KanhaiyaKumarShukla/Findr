package com.exa.android.reflekt.core.ui.state

/**
 * Structured error model for UI-layer error states.
 * Produced by [RepositoryResponseHandler] and carried inside [UiState.Error].
 */
data class AppError(
    val type: AppErrorType,
    val message: String?,
    val code: Int? = null,
) {
    companion object {
        fun noInternet(message: String? = "No internet connection") =
            AppError(AppErrorType.NO_INTERNET, message)

        fun server(message: String? = null) =
            AppError(AppErrorType.SERVER, message)

        fun general(message: String? = "Something went wrong. Please try again.") =
            AppError(AppErrorType.GENERAL, message)

        fun notFound(message: String? = "Content not found") =
            AppError(AppErrorType.NOT_FOUND, message, 404)

        fun fromCode(code: Int, msg: String?): AppError = AppError(
            type = when (code) {
                404          -> AppErrorType.NOT_FOUND
                in 400..499  -> AppErrorType.CLIENT
                in 500..599  -> AppErrorType.SERVER
                else         -> AppErrorType.GENERAL
            },
            message = msg,
            code = code,
        )
    }
}

enum class AppErrorType {
    NO_INTERNET,
    SERVER,
    CLIENT,
    GENERAL,
    NOT_FOUND,
}
