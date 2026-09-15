package org.aossie.carbontracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.services.client.data.ExerciseType
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material3.AppScaffold
import org.aossie.carbontracker.presentation.screens.OnboardingScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import kotlinx.coroutines.launch
import org.aossie.carbontracker.presentation.screens.ActivityScreen
import org.aossie.carbontracker.presentation.screens.StopwatchScreen
import org.aossie.carbontracker.presentation.theme.CarbonTrackerTheme
import org.aossie.carbontracker.providers.HealthClientProvider
import org.aossie.carbontracker.services.HealthServicesManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        HealthClientProvider.initialize(this)

        setContent {

            CarbonTrackerTheme {
                AppScaffold() {
                    WearApp()
                }
            }
        }
    }
}

fun exerciseTypeFromString(name: String): ExerciseType {
    return when (name) {
        "RUNNING" -> ExerciseType.RUNNING
        "WALKING" -> ExerciseType.WALKING
        "BIKING" -> ExerciseType.BIKING
        // add any other exercise types you actually use
        else -> ExerciseType.RUNNING   // fallback default
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

        composable("stopwatch/{exerciseType}") { backStackEntry ->
            val exerciseTypeString = backStackEntry.arguments?.getString("exerciseType")
            StopwatchScreen(exerciseType = exerciseTypeFromString(exerciseTypeString ?: "RUNNING"))
        }

    }
}

