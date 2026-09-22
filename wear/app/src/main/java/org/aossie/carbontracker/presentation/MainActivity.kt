package org.aossie.carbontracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.AppScaffold
import org.aossie.carbontracker.presentation.screens.OnboardingScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import org.aossie.carbontracker.presentation.screens.ActivityScreen
import org.aossie.carbontracker.presentation.screens.StopwatchScreen
import org.aossie.carbontracker.presentation.theme.CarbonTrackerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {

            CarbonTrackerTheme {
                AppScaffold() {
                    WearApp()
                }
            }
        }
    }
}

@Composable
fun WearApp() {
    val state = rememberSwipeDismissableNavController()

    SwipeDismissableNavHost(
        navController = state,
        startDestination = "onboarding"
    )
    {
        composable("onboarding") {
            OnboardingScreen(navController = state)
        }

        composable("activity") {
            ActivityScreen(navController = state)
        }

        composable("stopwatch") {
            StopwatchScreen()
        }

    }
}

