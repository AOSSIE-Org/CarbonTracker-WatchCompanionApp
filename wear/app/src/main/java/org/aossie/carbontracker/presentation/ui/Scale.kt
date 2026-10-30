package org.aossie.carbontracker.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration


@Composable
fun scaleFactor(): Float {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    return (screenWidth / 192f).coerceIn(0.85f, 1.2f)
}

