package com.example.carbontrackerwatch.presentation.screens

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import com.example.carbontrackerwatch.presentation.ui.LabelGray
import com.example.carbontrackerwatch.presentation.ui.PauseBg
import com.example.carbontrackerwatch.presentation.ui.PauseIconColor
import com.example.carbontrackerwatch.presentation.ui.PrimaryGreen
import com.example.carbontrackerwatch.presentation.ui.StopBg
import com.example.carbontrackerwatch.presentation.ui.StopRed
import kotlinx.coroutines.delay

@Composable
fun StopwatchScreen() {

    var elapsedMillis by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }
    var pauseTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            if (!isStarted) {
                if (pauseTime > 0L) {
                    startTime = SystemClock.elapsedRealtime() - pauseTime
                    pauseTime = 0L
                } else {
                    startTime = SystemClock.elapsedRealtime()
                }
                isStarted = true
            }
            elapsedMillis = SystemClock.elapsedRealtime() - startTime
            delay(1000L)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "TIME ELAPSED",
                color = LabelGray,
                fontSize = 13.sp,
            )

            Text(
                text = formatElapsed(elapsedMillis),
                color = PrimaryGreen,
                fontSize = 34.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f),
                ) {
                    RoundControlButton(
                        backgroundColor = StopBg,
                        iconColor = StopRed,
                        icon = Icons.Filled.Stop,
                        contentDescription = "Stop",
                        diameter = 46.dp,
                        onClick = {
                            elapsedMillis = 0L
                            isRunning = false
                            isStarted = false
                            pauseTime = 0L
                        },
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Stop",
                        color = StopRed,
                        fontSize = 10.sp,
                        modifier = Modifier.width(52.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f),
                ) {
                    RoundControlButton(
                        backgroundColor = PrimaryGreen,
                        iconColor = Color.White,
                        icon = Icons.Filled.PlayArrow,
                        contentDescription = "Start",
                        diameter = 54.dp,
                        onClick = {
                            isRunning = true
                        },
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Start",
                        color = PrimaryGreen,
                        fontSize = 10.sp,
                        modifier = Modifier.width(64.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f),
                ) {
                    RoundControlButton(
                        backgroundColor = PauseBg,
                        iconColor = PauseIconColor,
                        icon = Icons.Filled.Pause,
                        contentDescription = "Pause",
                        diameter = 46.dp,
                        onClick = {
                            isRunning = false
                            isStarted = false
                            pauseTime = elapsedMillis
                        },
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Pause",
                        color = LabelGray,
                        fontSize = 10.sp,
                        modifier = Modifier.width(52.dp),
                        textAlign = TextAlign.Center
                    )

                }


            }
        }
    }
}

@Composable
private fun RoundControlButton(
    backgroundColor: Color,
    iconColor: Color,
    icon: ImageVector,
    contentDescription: String,
    diameter: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(diameter)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(diameter)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(diameter * 0.4f),
            )
        }
    }
}

private fun formatElapsed(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
