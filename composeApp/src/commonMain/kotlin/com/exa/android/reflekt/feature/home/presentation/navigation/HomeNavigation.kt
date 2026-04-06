package com.exa.android.reflekt.feature.home.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.exa.android.reflekt.feature.home.presentation.HomeScreen
import com.exa.android.reflekt.feature.home.presentation.HomeViewModel
import com.exa.android.reflekt.feature.home.presentation.bug_detail.BugDetailScreen
import com.exa.android.reflekt.feature.home.presentation.event_detail.EventDetailScreen
import com.exa.android.reflekt.feature.home.presentation.poll_detail.PollDetailScreen
import com.exa.android.reflekt.feature.notification.presentation.NotificationScreen
import com.exa.android.reflekt.feature.project.presentation.ProjectDetailScreen
import com.exa.android.reflekt.navigation.BugDetailRoute
import com.exa.android.reflekt.navigation.EventDetailRoute
import com.exa.android.reflekt.navigation.HomeGraph
import com.exa.android.reflekt.navigation.HomeRoute
import com.exa.android.reflekt.navigation.NotificationsRoute
import com.exa.android.reflekt.navigation.PollDetailRoute
import com.exa.android.reflekt.navigation.PostGraph
import com.exa.android.reflekt.navigation.ChatGraph
import com.exa.android.reflekt.navigation.ProfileGraph
import com.exa.android.reflekt.navigation.ProjectDetailRoute

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation<HomeGraph>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            val viewModel = remember { HomeViewModel() }
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCreatePost = { navController.navigate(PostGraph) },
                onNavigateToNotifications = { navController.navigate(NotificationsRoute) },
                onNavigateToProfile = { navController.navigate(ProfileGraph) },
                onNavigateToChat = { navController.navigate(ChatGraph) },
                onNavigateToProjectDetail = { projectId ->
                    navController.navigate(ProjectDetailRoute(projectId))
                },
                onNavigateToBugDetail = { bugId ->
                    navController.navigate(BugDetailRoute(bugId))
                },
                onNavigateToEventDetail = { eventId ->
                    navController.navigate(EventDetailRoute(eventId))
                },
                onNavigateToPollDetail = { pollId ->
                    navController.navigate(PollDetailRoute(pollId))
                },
            )
        }
        composable<ProjectDetailRoute> {
            ProjectDetailScreen(
                onBack = { navController.popBackStack() },
                onEnroll = { navController.popBackStack() },
            )
        }
        composable<BugDetailRoute> {
            BugDetailScreen(
                onBack = { navController.popBackStack() },
                onCollaborate = { navController.popBackStack() },
            )
        }
        composable<EventDetailRoute> {
            EventDetailScreen(
                onBack = { navController.popBackStack() },
                onRegister = { navController.popBackStack() },
            )
        }
        composable<PollDetailRoute> {
            PollDetailScreen(
                onBack = { navController.popBackStack() },
                onClosePoll = { navController.popBackStack() },
            )
        }
        composable<NotificationsRoute> {
            NotificationScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
