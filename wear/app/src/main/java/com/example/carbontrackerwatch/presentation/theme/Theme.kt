package com.example.carbontrackerwatch.presentation.theme


import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.MaterialTheme
import com.example.carbontrackerwatch.presentation.ui.BackgroundCream
import com.example.carbontrackerwatch.presentation.ui.PrimaryGreen

@Composable
fun CarbonTrackerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = PrimaryGreen,
            background = BackgroundCream,
        ),
        content = content
    )
}
