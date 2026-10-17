package org.aossie.carbontracker.services

import android.app.Service
import android.os.Binder
import android.util.Log
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.ExerciseUpdateCallback
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseLapSummary
import androidx.health.services.client.data.ExerciseState
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.ExerciseUpdate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.aossie.carbontracker.data.database.ActivityDao
import org.aossie.carbontracker.data.database.ActivityEntity
import org.aossie.carbontracker.data.database.AppDatabase
import org.aossie.carbontracker.providers.ExerciseStateHolder.exerciseUpdate
import org.aossie.carbontracker.providers.HealthClientProvider
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import org.aossie.carbontracker.R
import org.aossie.carbontracker.presentation.MainActivity


class ExerciseService : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): ExerciseService = this@ExerciseService
    }

    private val binder = LocalBinder()

    private lateinit var exerciseClient: ExerciseClient

    private lateinit var activityDao: ActivityDao

    private val coroutineScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var currentActivityId: Long? = null

    @Volatile
    private var heartRate: Double? = null

    @Volatile
    private var calories: Double? = null

    @Volatile
    private var distance: Double? = null

    @Volatile
    private var ongoingActivity = false
    private val channelId = "exercise_channel"
    private val notifId = 1001

    @Volatile
    private var startElapsed = 0L

    @Volatile
    private var pausedAccum = 0L

    @Volatile
    private var pauseStarted = 0L

    fun elapsedMillis(): Long {
        if (startElapsed == 0L) return 0L
        val end = if (pauseStarted != 0L) pauseStarted else SystemClock.elapsedRealtime()
        return end - startElapsed - pausedAccum
    }

    private fun startOngoing(exercise: ExerciseType) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(channelId, "Workout", NotificationManager.IMPORTANCE_LOW)
        )

        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    putExtra("exercise_type", exercise.name)
                },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("CarbonTracker")
            .setContentText("Tracking ${exercise.name}")
            .setSmallIcon(R.drawable.ic_exercise)
            .setCategory(NotificationCompat.CATEGORY_WORKOUT)
            .setContentIntent(pi)
            .setOngoing(true)

        val status = Status.Builder()
            .addTemplate("#type# #time#")
            .addPart("type", Status.TextPart("Workout"))
            .addPart("time", Status.StopwatchPart(SystemClock.elapsedRealtime()))
            .build()

        OngoingActivity.Builder(applicationContext, notifId, builder)
            .setTouchIntent(pi)
            .setStatus(status)
            .setStaticIcon(R.drawable.ic_exercise)
            .build()
            .apply(applicationContext)

        val type = if (Build.VERSION.SDK_INT >= 34) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH or ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        }

        ServiceCompat.startForeground(this, notifId, builder.build(), type)
    }

    private val exerciseCallback = object : ExerciseUpdateCallback {

        override fun onRegistered() {
            Log.d("ExerciseService", "Exercise callback registered successfully")
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            Log.d("ExerciseService", "Exercise callback registration failed: ${throwable.message}")
        }

        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {

            exerciseUpdate(update.exerciseStateInfo.state)

            val exerciseStateInfo = update.exerciseStateInfo
            val latestMetrics = update.latestMetrics

            heartRate = latestMetrics.getData(DataType.HEART_RATE_BPM).lastOrNull()?.value
            calories = latestMetrics.getData(DataType.CALORIES_TOTAL)?.total
            distance = latestMetrics.getData(DataType.DISTANCE).lastOrNull()?.value

            if (exerciseStateInfo.state == ExerciseState.USER_PAUSED) {
                return
            }

            if (exerciseStateInfo.state == ExerciseState.ENDING || exerciseStateInfo.state == ExerciseState.ENDED) {
                val id = currentActivityId
                if (id != null) {
                    coroutineScope.launch {
                        activityDao.stopActivity(id, System.currentTimeMillis())
                        val activity = activityDao.getActivity(id)
                        if (activity != null) {
                            activityDao.updateMetrics(
                                id,
                                distance = distance ?: activity.distance,
                                calories = calories ?: activity.caloriesBurned,
                                heartRate = heartRate ?: activity.heartRate,
                                lastUpdated = System.currentTimeMillis()
                            )
                        }
                    }
                }
                ongoingActivity = false
                currentActivityId = null
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return
            }

            Log.d("ExerciseService", "Exercise state: ${exerciseStateInfo.state}")
            Log.d("ExerciseService", "Heart rate: $heartRate")
            Log.d("ExerciseService", "Calories: $calories")
            Log.d("ExerciseService", "Distance: $distance")
        }


        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {
            // For ExerciseTypes that support laps, this is called when a lap is marked.
        }

        override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
            // handle GPS/sensor availability changes
        }
    }


    suspend fun startExercise(exercise: ExerciseType): Boolean {

        if (ongoingActivity) {
            Log.d(
                "ExerciseService",
                "An exercise is already ongoing. Please end it before starting a new one."
            )
            return false
        }

        // Types for which we want to receive metrics.
        val dataTypes = setOf(
            DataType.HEART_RATE_BPM,
            DataType.CALORIES_TOTAL,
            DataType.DISTANCE
        )

        val config = ExerciseConfig(
            exerciseType = exercise,
            dataTypes = dataTypes,
            isAutoPauseAndResumeEnabled = false,
            isGpsEnabled = true,
        )
        try {
            startOngoing(exercise)
            exerciseClient.startExerciseAsync(config).awaitWithException()
            if (ongoingActivity) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return false
            }
            ongoingActivity = true
            startElapsed = SystemClock.elapsedRealtime()
            pausedAccum = 0L
            pauseStarted = 0L
            currentActivityId = activityDao.startActivity(
                ActivityEntity(
                    startTime = System.currentTimeMillis(),
                    activityType = exercise.name
                )
            )
            coroutineScope.launch {

                while (ongoingActivity) {

                    delay(30_000) // 30 seconds

                    val id = currentActivityId ?: continue

                    if (distance != null && calories != null) {

                        activityDao.updateMetrics(
                            id = id,
                            distance = distance!!,
                            calories = calories!!,
                            heartRate = heartRate,
                            lastUpdated = System.currentTimeMillis()
                        )
                    }
                }
            }
            return true

        } catch (e: Exception) {
            Log.d("ExerciseService", "Error starting exercise: ${e.message}")
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return false
        }

    }

    suspend fun pauseExercise(): Boolean {
        try {
            val activity =
                currentActivityId?.let { activityDao.getActivity(currentActivityId!!) }

            if (activity != null) {
                activityDao.updateMetrics(
                    currentActivityId as Long,
                    distance = distance ?: activity.distance,
                    calories = calories ?: activity.caloriesBurned,
                    heartRate = heartRate ?: activity.heartRate,
                    lastUpdated = System.currentTimeMillis()
                )
            }
            exerciseClient.pauseExerciseAsync().awaitWithException()
            pauseStarted = SystemClock.elapsedRealtime()
            return true
        } catch (e: Exception) {
            Log.d("ExerciseService", "Error pausing exercise: ${e.message}")
            return false
        }
    }

    suspend fun resumeExercise(): Boolean {
        try {
            exerciseClient.resumeExerciseAsync().awaitWithException()
            pausedAccum += SystemClock.elapsedRealtime() - pauseStarted
            pauseStarted = 0L
            return true
        } catch (e: Exception) {
            Log.d("ExerciseService", "Error resuming exercise: ${e.message}")
            return false
        }
    }

    suspend fun endExercise(): Boolean {
        try {
            exerciseClient.endExerciseAsync().awaitWithException()
            val id = currentActivityId
            if (id != null) {
                activityDao.stopActivity(id, System.currentTimeMillis())

                val activity =
                    activityDao.getActivity(id)

                if (activity != null) {
                    activityDao.updateMetrics(
                        id,
                        distance = distance ?: activity.distance,
                        calories = calories ?: activity.caloriesBurned,
                        heartRate = heartRate ?: activity.heartRate,
                        lastUpdated = System.currentTimeMillis(),
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("ExerciseService", "Error ending exercise: ${e.message}")
            return false
        } finally {
            ongoingActivity = false
            currentActivityId = null
            startElapsed = 0L
            pausedAccum = 0L
            pauseStarted = 0L
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return true
    }

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(applicationContext)
        activityDao = db.activityDao()
        coroutineScope.launch {
            val orphanedActivities = activityDao.getAllOrphanedActivities()
            Log.d(
                "ExerciseService",
                "Found ${orphanedActivities.size} orphaned activities. Closing them."
            )
            orphanedActivities.forEach { activity ->
                Log.d("ExerciseService", "Closing orphaned activity with ID: ${activity.id}")
                activityDao.closeOrphanedActivity(activity.id)
            }
        }

        exerciseClient = HealthClientProvider.getClient().exerciseClient
        registerExerciseCallback()
    }

    override fun onDestroy() {
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun registerExerciseCallback() {
        exerciseClient.setUpdateCallback(exerciseCallback)
    }

    override fun onBind(intent: Intent?): android.os.IBinder = binder
}
