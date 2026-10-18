package org.aossie.carbontracker.presentation.theme


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.MaterialTheme
import org.aossie.carbontracker.presentation.ui.PrimaryGreen

@Composable
fun CarbonTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Color.Black,
            primary = PrimaryGreen
        ),
        content = content
    )
}
