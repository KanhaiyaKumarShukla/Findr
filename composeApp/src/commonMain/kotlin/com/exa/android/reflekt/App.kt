package com.exa.android.reflekt

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.exa.android.reflekt.feature.auth.presentation.navigation.authGraph
import com.exa.android.reflekt.feature.home.presentation.navigation.homeGraph
import com.exa.android.reflekt.feature.onboarding.presentation.navigation.onboardingGraph
import com.exa.android.reflekt.feature.post.presentation.navigation.postGraph
import com.exa.android.reflekt.navigation.OnboardingGraph
import com.exa.android.reflekt.ui.theme.FindrTheme

@Composable
@Preview
fun App() {
    FindrTheme(darkTheme = true) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = OnboardingGraph) {
            onboardingGraph(navController)
            authGraph(navController)
            homeGraph(navController)
            postGraph(navController)
        }
    }


}
