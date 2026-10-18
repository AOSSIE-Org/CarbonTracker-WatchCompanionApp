package org.aossie.carbontracker.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.health.services.client.data.ExerciseType


data class ActivityInfo(
    val name: String,
    val icon: ImageVector,
    val intensity: String
)

val activityInfo = mapOf(
    ExerciseType.WALKING to ActivityInfo(
        name = "Walking",
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        intensity = "Low"
    ),
    ExerciseType.RUNNING to ActivityInfo(
        name = "Running",
        icon = Icons.AutoMirrored.Filled.DirectionsRun,
        intensity = "Medium"
    ),
    ExerciseType.BIKING to ActivityInfo(
        name = "Cycling",
        icon = Icons.AutoMirrored.Filled.DirectionsBike,
        intensity = "High"
    )
)
