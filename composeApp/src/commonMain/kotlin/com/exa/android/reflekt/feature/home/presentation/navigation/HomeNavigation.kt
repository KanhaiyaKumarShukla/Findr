package com.exa.android.reflekt.feature.home.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.exa.android.reflekt.feature.home.presentation.HomeScreen
import com.exa.android.reflekt.feature.home.presentation.HomeViewModel
import com.exa.android.reflekt.feature.notification.presentation.NotificationScreen
import com.exa.android.reflekt.feature.project.presentation.ProjectDetailScreen
import com.exa.android.reflekt.navigation.HomeGraph
import com.exa.android.reflekt.navigation.HomeRoute
import com.exa.android.reflekt.navigation.NotificationsRoute
import com.exa.android.reflekt.navigation.PostGraph
import com.exa.android.reflekt.navigation.ProjectDetailRoute

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            val viewModel = remember { HomeViewModel() }
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCreatePost = { navController.navigate(PostGraph) },
                onNavigateToNotifications = { navController.navigate(NotificationsRoute) },
                onNavigateToProjectDetail = { projectId ->
                    navController.navigate(ProjectDetailRoute(projectId))
                },
            )
        }
        composable<ProjectDetailRoute> {
            ProjectDetailScreen(
                onBack = { navController.popBackStack() },
                onEnroll = { navController.popBackStack() },
            )
        }
        composable<NotificationsRoute> {
            NotificationScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
