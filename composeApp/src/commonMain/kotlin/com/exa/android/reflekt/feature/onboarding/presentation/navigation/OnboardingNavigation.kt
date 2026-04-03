package com.exa.android.reflekt.feature.onboarding.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.exa.android.reflekt.feature.onboarding.OnboardingScreen
import com.exa.android.reflekt.navigation.AuthGraph
import com.exa.android.reflekt.navigation.OnboardingGraph
import com.exa.android.reflekt.navigation.OnboardingRoute

fun NavGraphBuilder.onboardingGraph(navController: NavController) {
    navigation<OnboardingGraph>(startDestination = OnboardingRoute) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(AuthGraph) {
                        popUpTo(OnboardingGraph) { inclusive = true }
                    }
                },
            )
        }
    }
}
