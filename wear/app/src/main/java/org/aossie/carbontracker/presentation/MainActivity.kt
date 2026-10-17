package org.aossie.carbontracker.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.services.client.data.ExerciseType
import androidx.wear.compose.material3.AppScaffold
import org.aossie.carbontracker.presentation.screens.OnboardingScreen
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import org.aossie.carbontracker.presentation.screens.ActivityScreen
import org.aossie.carbontracker.presentation.screens.StopwatchScreen
import org.aossie.carbontracker.presentation.theme.CarbonTrackerTheme

class MainActivity : ComponentActivity() {

    private var pendingExercise by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        pendingExercise = intent?.getStringExtra("exercise_type")
        setContent {
            CarbonTrackerTheme {
                AppScaffold() {
                    WearApp(
                        pendingExercise = pendingExercise,
                        onHandled = { pendingExercise = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingExercise = intent.getStringExtra("exercise_type")
    }

}

fun exerciseTypeFromString(name: String): ExerciseType {
    return when (name) {
        "RUNNING" -> ExerciseType.RUNNING
        "WALKING" -> ExerciseType.WALKING
        "BIKING" -> ExerciseType.BIKING
        else -> ExerciseType.RUNNING
    }
}

@Composable
fun WearApp(pendingExercise: String?, onHandled: () -> Unit) {
    val state = rememberSwipeDismissableNavController()

    LaunchedEffect(pendingExercise) {
        pendingExercise?.let {
            state.navigate("stopwatch/$it") { launchSingleTop = true }
            onHandled()
        }
    }

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

