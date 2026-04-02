package com.exa.android.reflekt.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.components.button.FindrOutlinedButton
import com.exa.android.reflekt.ui.components.button.FindrPrimaryButton
import com.exa.android.reflekt.ui.components.button.FindrTextButton

// ─────────────────────────────────────────────────────────────
//  Standard two-action dialog
// ─────────────────────────────────────────────────────────────

/**
 * Standard confirmation dialog with optional icon, title, body, and two actions.
 *
 * @param title           Dialog heading.
 * @param message         Supporting body text.
 * @param confirmLabel    Primary action label (e.g. "Delete", "Confirm").
 * @param dismissLabel    Secondary action label (e.g. "Cancel").
 * @param onConfirm       Invoked when the primary button is tapped.
 * @param onDismiss       Invoked when the secondary button is tapped OR dialog is dismissed.
 * @param icon            Optional icon shown above the title.
 * @param iconTint        Tint for [icon] — use [MaterialTheme.colorScheme.error] for destructive dialogs.
 * @param confirmColor    Background color of the confirm button.
 * @param isDestructive   When true, the confirm button uses the error color scheme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindrDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    isDestructive: Boolean = false,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = iconTint ?: if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FindrOutlinedButton(
                        text = dismissLabel,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        height = 44.dp,
                    )
                    FindrPrimaryButton(
                        text = confirmLabel,
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        height = 44.dp,
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Info dialog  (single dismiss action)
// ─────────────────────────────────────────────────────────────

/**
 * Informational dialog with a single "Got it" dismiss button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindrInfoDialog(
    title: String,
    message: String,
    dismissLabel: String = "Got it",
    onDismiss: () -> Unit,
    icon: ImageVector? = null,
) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(24.dp))

                FindrTextButton(
                    text = dismissLabel,
                    onClick = onDismiss,
                )
            }
        }
    }
}
