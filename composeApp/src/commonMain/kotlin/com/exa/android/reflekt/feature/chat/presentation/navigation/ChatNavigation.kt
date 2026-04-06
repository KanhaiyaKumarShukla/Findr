package com.exa.android.reflekt.feature.chat.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.chat.presentation.ChatDetailScreen
import com.exa.android.reflekt.feature.chat.presentation.ChatDetailViewModel
import com.exa.android.reflekt.feature.chat.presentation.ChatScreen
import com.exa.android.reflekt.feature.chat.presentation.ChatViewModel
import com.exa.android.reflekt.navigation.ChatDetailRoute
import com.exa.android.reflekt.navigation.ChatGraph
import com.exa.android.reflekt.navigation.ChatRoute

fun NavGraphBuilder.chatGraph(navController: NavController) {
    navigation<ChatGraph>(startDestination = ChatRoute) {
        composable<ChatRoute> {
            val viewModel = remember { ChatViewModel() }
            ChatScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { conversationId ->
                    navController.navigate(ChatDetailRoute(conversationId))
                },
            )
        }
        composable<ChatDetailRoute> {
            val viewModel = remember { ChatDetailViewModel() }
            ChatDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
