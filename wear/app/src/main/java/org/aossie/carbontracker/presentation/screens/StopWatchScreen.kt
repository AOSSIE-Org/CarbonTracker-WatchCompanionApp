package org.aossie.carbontracker.presentation.screens

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import org.aossie.carbontracker.presentation.ui.LabelGray
import org.aossie.carbontracker.presentation.ui.PauseBg
import org.aossie.carbontracker.presentation.ui.PauseIconColor
import org.aossie.carbontracker.presentation.ui.PrimaryGreen
import org.aossie.carbontracker.presentation.ui.StopBg
import org.aossie.carbontracker.presentation.ui.StopRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.aossie.carbontracker.providers.ExerciseStateHolder
import org.aossie.carbontracker.services.ExerciseService
import android.Manifest
import android.content.pm.PackageManager
import android.health.connect.HealthPermissions
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@Composable
fun StopwatchScreen(exerciseType: ExerciseType) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var elapsedMillis by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var startTime by remember { mutableLongStateOf(0L) }
    var pauseTime by remember { mutableLongStateOf(0L) }
    val exerciseState by ExerciseStateHolder.state.collectAsState()
    var exerciseService by remember { mutableStateOf<ExerciseService?>(null) }
    val ongoingActivity = exerciseState == ExerciseState.ACTIVE ||
        exerciseState == ExerciseState.USER_PAUSED ||
        exerciseState == ExerciseState.USER_PAUSING

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ExerciseService.LocalBinder
                exerciseService = binder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                exerciseService = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, ExerciseService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        onDispose {
            context.unbindService(connection)
        }
    }


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

    val requiredPermissions = remember {
        buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACTIVITY_RECOGNITION)
            if (Build.VERSION.SDK_INT >= 36) {
                add(HealthPermissions.READ_HEART_RATE)
            } else {
                add(Manifest.permission.BODY_SENSORS)
            }
        }
    }

    fun hasAllPermissions(): Boolean =
        requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

    var hasAllPermissions by remember { mutableStateOf(hasAllPermissions()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasAllPermissions = results.values.all { it }
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (!hasAllPermissions) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Please grant all required permissions to use the stopwatch.",
                    color = LabelGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Grant Permissions",
                    color = PrimaryGreen,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable { permissionLauncher.launch(requiredPermissions.toTypedArray()) }
                )
            }
        } else {
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
                    if (ongoingActivity) {
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

                                    exerciseService?.let { service ->
                                        coroutineScope.launch {
                                            if (service.endExercise()) {
                                                elapsedMillis = 0L
                                                isRunning = false
                                                isStarted = false
                                                pauseTime = 0L
                                            }
                                        }
                                    }

                                },
                                enabled = exerciseService != null && exerciseState != ExerciseState.USER_PAUSING

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
                                exerciseService?.let { service ->
                                    coroutineScope.launch {
                                        val ok = if (exerciseState == ExerciseState.USER_PAUSED)
                                            service.resumeExercise()
                                        else
                                            service.startExercise(exerciseType)

                                        if (ok) isRunning = true
                                    }
                                }
                            },
                            enabled = exerciseService != null && exerciseState != ExerciseState.ACTIVE
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
                    if (ongoingActivity) {
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
                                    val currentElapsed = SystemClock.elapsedRealtime() - startTime
                                    exerciseService?.let { service ->
                                        coroutineScope.launch {
                                            if (service.pauseExercise()) {
                                                elapsedMillis = currentElapsed
                                                pauseTime = currentElapsed
                                                isRunning = false
                                                isStarted = false
                                            }
                                        }
                                    }
                                },
                                enabled = exerciseService != null && exerciseState != ExerciseState.USER_PAUSED
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
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(diameter)
            .background(
                if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f),
                CircleShape
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(diameter)
                .clickable(enabled = enabled, onClick = onClick),
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
