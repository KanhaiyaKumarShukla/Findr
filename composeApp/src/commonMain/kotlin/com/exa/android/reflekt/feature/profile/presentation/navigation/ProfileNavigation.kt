package com.exa.android.reflekt.feature.profile.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.profile.presentation.ProfileScreen
import com.exa.android.reflekt.feature.profile.presentation.ProfileViewModel
import com.exa.android.reflekt.navigation.ProfileGraph
import com.exa.android.reflekt.navigation.ProfileRoute

fun NavGraphBuilder.profileGraph(navController: NavController) {
    navigation<ProfileGraph>(startDestination = ProfileRoute) {
        composable<ProfileRoute> {
            val viewModel = remember { ProfileViewModel() }
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
