package com.exa.android.reflekt.feature.post.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.exa.android.reflekt.feature.post.presentation.CreatePostSelectionScreen
import com.exa.android.reflekt.feature.post.presentation.PostCategory
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventScreen
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventViewModel
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectScreen
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectViewModel
import com.exa.android.reflekt.feature.project.presentation.ProjectDetailScreen
import com.exa.android.reflekt.navigation.CreateEventRoute
import com.exa.android.reflekt.navigation.CreatePostSelectionRoute
import com.exa.android.reflekt.navigation.CreateProjectRoute
import com.exa.android.reflekt.navigation.HomeGraph
import com.exa.android.reflekt.navigation.PostGraph
import com.exa.android.reflekt.navigation.ProjectDetailRoute

fun NavGraphBuilder.postGraph(navController: NavController) {
    navigation<PostGraph>(startDestination = CreatePostSelectionRoute) {
        composable<CreatePostSelectionRoute> {
            CreatePostSelectionScreen(
                onCategorySelected = { category ->
                    when (category) {
                        PostCategory.PROJECT -> navController.navigate(CreateProjectRoute)
                        PostCategory.EVENT -> navController.navigate(CreateEventRoute)
                        else -> { /* No-op */ }
                    }
                },
                onDismiss = { navController.popBackStack() },
            )
        }

        composable<CreateProjectRoute> {
            val viewModel = remember { CreateProjectViewModel() }
            CreateProjectScreen(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
                onPostSuccess = {
                    navController.navigate(HomeGraph) {
                        popUpTo(PostGraph) { inclusive = true }
                    }
                },
            )
        }

        composable<CreateEventRoute> {
            val viewModel = remember { CreateEventViewModel() }
            CreateEventScreen(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
                onPostSuccess = {
                    navController.navigate(HomeGraph) {
                        popUpTo(PostGraph) { inclusive = true }
                    }
                },
            )
        }

        composable<ProjectDetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProjectDetailRoute>()
            // Route parameters are available via route.projectId
            
            // Note: ProjectDetailScreen uses its own stubbed logic initially
            ProjectDetailScreen(
                onBack = { navController.popBackStack() },
                onEnroll = { /* Handle enrollment */ },
            )
        }
    }
}
