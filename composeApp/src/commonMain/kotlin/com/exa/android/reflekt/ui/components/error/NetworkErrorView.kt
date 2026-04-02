package com.exa.android.reflekt.ui.components.error

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.components.button.FindrOutlinedButton
import com.exa.android.reflekt.ui.components.button.FindrPrimaryButton

// ─────────────────────────────────────────────────────────────
//  Error type enum
// ─────────────────────────────────────────────────────────────

enum class ErrorType {
    NO_INTERNET,
    NO_CONTENT,
    SERVER_ERROR,
    GENERAL,
}

private data class ErrorConfig(
    val icon: ImageVector,
    val title: String,
    val message: String,
)

private fun errorConfig(type: ErrorType) = when (type) {
    ErrorType.NO_INTERNET -> ErrorConfig(
        icon = Icons.Filled.WifiOff,
        title = "No Internet Connection",
        message = "Please check your network settings and try again.",
    )
    ErrorType.NO_CONTENT -> ErrorConfig(
        icon = Icons.Filled.Search,
        title = "Nothing Here",
        message = "We couldn't find what you were looking for.",
    )
    ErrorType.SERVER_ERROR -> ErrorConfig(
        icon = Icons.Filled.Warning,
        title = "Server Error",
        message = "Something went wrong on our end. Please try again shortly.",
    )
    ErrorType.GENERAL -> ErrorConfig(
        icon = Icons.Filled.Error,
        title = "Something Went Wrong",
        message = "An unexpected error occurred. Please try again.",
    )
}

// ─────────────────────────────────────────────────────────────
//  Full-screen error view
// ─────────────────────────────────────────────────────────────

/**
 * Full-screen error state — shown when a page-level request fails.
 *
 * @param errorType   Determines icon, title, and default message.
 * @param message     Override the default message (e.g. server-provided text).
 * @param onRetry     When non-null, a "Try Again" primary button is shown.
 * @param onSecondaryAction  Optional secondary action (e.g. "Go Back").
 * @param secondaryLabel     Label for the secondary action button.
 */
@Composable
fun NetworkErrorView(
    errorType: ErrorType = ErrorType.GENERAL,
    modifier: Modifier = Modifier,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryLabel: String = "Go Back",
) {
    val config = errorConfig(errorType)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = config.icon,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = config.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = message ?: config.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        if (onRetry != null) {
            Spacer(Modifier.height(32.dp))
            FindrPrimaryButton(
                text = "Try Again",
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (onSecondaryAction != null) {
            Spacer(Modifier.height(12.dp))
            FindrOutlinedButton(
                text = secondaryLabel,
                onClick = onSecondaryAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Inline (partial-screen) error banner
// ─────────────────────────────────────────────────────────────

/**
 * Compact inline error banner — use inside a list/card when a sub-section fails.
 */
@Composable
fun InlineErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            FindrOutlinedButton(
                text = "Retry",
                onClick = onRetry,
            )
        }
    }
}
