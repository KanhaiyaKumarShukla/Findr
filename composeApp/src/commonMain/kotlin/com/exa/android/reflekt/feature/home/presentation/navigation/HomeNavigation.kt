package com.exa.android.reflekt.feature.home.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.home.presentation.HomeScreen
import com.exa.android.reflekt.feature.home.presentation.HomeViewModel
import com.exa.android.reflekt.feature.notification.presentation.NotificationScreen
import com.exa.android.reflekt.navigation.CreatePostSelectionRoute
import com.exa.android.reflekt.navigation.HomeGraph
import com.exa.android.reflekt.navigation.HomeRoute
import com.exa.android.reflekt.navigation.NotificationsRoute

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            val viewModel = remember { HomeViewModel() }
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCreatePost = { navController.navigate(CreatePostSelectionRoute) },
                onNavigateToNotifications = { navController.navigate(NotificationsRoute) },
            )
        }

        composable<NotificationsRoute> {
            NotificationScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
