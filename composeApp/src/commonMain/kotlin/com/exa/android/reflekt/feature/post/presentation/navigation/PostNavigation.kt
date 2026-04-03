package com.exa.android.reflekt.feature.post.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.post.presentation.CreatePostSelectionScreen
import com.exa.android.reflekt.feature.post.presentation.PostCategory
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventScreen
import com.exa.android.reflekt.feature.post.presentation.create_event.CreateEventViewModel
import com.exa.android.reflekt.feature.post.presentation.create_bug_report.CreateBugReportScreen
import com.exa.android.reflekt.feature.post.presentation.create_bug_report.CreateBugReportViewModel
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectScreen
import com.exa.android.reflekt.feature.post.presentation.create_project.CreateProjectViewModel
import com.exa.android.reflekt.navigation.CreateBugReportRoute
import com.exa.android.reflekt.navigation.CreateEventRoute
import com.exa.android.reflekt.navigation.CreatePostSelectionRoute
import com.exa.android.reflekt.navigation.CreateProjectRoute
import com.exa.android.reflekt.navigation.PostGraph

fun NavGraphBuilder.postGraph(navController: NavController) {
    navigation<PostGraph>(startDestination = CreatePostSelectionRoute) {
        composable<CreatePostSelectionRoute> {
            CreatePostSelectionScreen(
                onCategorySelected = { category ->
                    when (category) {
                        PostCategory.PROJECT -> navController.navigate(CreateProjectRoute)
                        PostCategory.EVENT -> navController.navigate(CreateEventRoute)
                        PostCategory.BUG_REPORT -> navController.navigate(CreateBugReportRoute)
                        else -> { /* TODO: handle POST */ }
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
                    navController.popBackStack(CreatePostSelectionRoute, inclusive = true)
                },
            )
        }
        composable<CreateBugReportRoute> {
            val viewModel = remember { CreateBugReportViewModel() }
            CreateBugReportScreen(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
                onPostSuccess = {
                    navController.popBackStack(CreatePostSelectionRoute, inclusive = true)
                },
            )
        }
        composable<CreateEventRoute> {
            val viewModel = remember { CreateEventViewModel() }
            CreateEventScreen(
                viewModel = viewModel,
                onCancel = { navController.popBackStack() },
                onPostSuccess = {
                    navController.popBackStack(CreatePostSelectionRoute, inclusive = true)
                },
            )
        }
    }
}
