package com.example.carbontrackerwatch.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.AppScaffold
import com.example.carbontrackerwatch.presentation.screens.OnboardingScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.carbontrackerwatch.presentation.screens.ActivityScreen
import com.example.carbontrackerwatch.presentation.screens.StopwatchScreen
import com.example.carbontrackerwatch.presentation.theme.CarbonTrackerTheme


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

