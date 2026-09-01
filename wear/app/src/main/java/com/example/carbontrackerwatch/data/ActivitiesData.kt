package com.example.carbontrackerwatch.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector

data class Activity(
    val name: String,
    val icon: ImageVector,
    val intensity: String
)

val activities = listOf(
    Activity(
        name = "Walking",
        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
        intensity = "Low intensity"
    ),
    Activity(
        name = "Cycling",
        icon = Icons.AutoMirrored.Filled.DirectionsBike,
        intensity = "High intensity"
    ),
    Activity(
        name = "Running",
        icon = Icons.AutoMirrored.Filled.DirectionsRun,
        intensity = "Medium intensity"
    )
)
