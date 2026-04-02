package com.exa.android.reflekt.ui.components.scaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.exa.android.reflekt.ui.components.topbar.FindrTopBar

// ─────────────────────────────────────────────────────────────
//  FindrScaffold — opinionated screen wrapper
// ─────────────────────────────────────────────────────────────

/**
 * Standard screen scaffold that wires together:
 * - [FindrTopBar] with optional back navigation and actions
 * - Scroll-aware top bar behaviour (collapses on scroll)
 * - [SnackbarHost] with branded Snackbar styling
 * - A loading overlay slot (use [FindrFullScreenLoader] here)
 * - Correct IME / navigation bar insets handling
 *
 * Usage:
 * ```kotlin
 * FindrScaffold(
 *     title = "Profile",
 *     onNavigateUp = { navController.navigateUp() },
 *     snackbarHostState = snackbarHostState,
 * ) { padding ->
 *     ProfileContent(modifier = Modifier.padding(padding))
 * }
 * ```
 *
 * @param title             Top bar title.
 * @param onNavigateUp      Back arrow callback — pass null to hide arrow.
 * @param topBarActions     Trailing icon buttons in the top bar.
 * @param snackbarHostState Shared [SnackbarHostState]; create with [remember].
 * @param loadingOverlay    Composable rendered above content while loading.
 * @param bottomBar         Optional bottom bar (e.g. action footer).
 * @param content           Screen content, receives [PaddingValues] from Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindrScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateUp: (() -> Unit)? = null,
    topBarActions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    loadingOverlay: @Composable (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FindrTopBar(
                title = title,
                onNavigateUp = onNavigateUp,
                actions = topBarActions,
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = bottomBar,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding(),
            ) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(12.dp),
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
        containerColor = containerColor,
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            content(paddingValues)
            if (loadingOverlay != null) {
                loadingOverlay()
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Error Snackbar  (branded, slide-up from bottom)
// ─────────────────────────────────────────────────────────────

/**
 * Standalone slide-up error snackbar composable.
 * Compose it inside a [Box] aligned to [Alignment.BottomCenter].
 *
 * @param message   Non-null when visible.
 * @param onDismiss Called when the "Dismiss" action is tapped.
 */
@Composable
fun FindrErrorSnackbar(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String = "Dismiss",
) {
    AnimatedVisibility(
        visible = message != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier,
    ) {
        Snackbar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            shape = RoundedCornerShape(12.dp),
            action = {
                TextButton(onClick = onDismiss) {
                    Text(text = actionLabel, color = MaterialTheme.colorScheme.onError)
                }
            },
        ) {
            Text(text = message.orEmpty())
        }
    }
}
