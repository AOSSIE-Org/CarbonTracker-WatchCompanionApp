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
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.aossie.carbontracker.data.database.ActivityDao
import org.aossie.carbontracker.data.database.ActivityEntity
import org.aossie.carbontracker.data.database.AppDatabase
import org.aossie.carbontracker.providers.ExerciseStateHolder.exerciseUpdate
import org.aossie.carbontracker.providers.HealthClientProvider


class ExerciseService : Service() {

    private var metricWriterJob: Job? = null

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

    private val exerciseCallback = object : ExerciseUpdateCallback {

        override fun onRegistered(): Unit {
            Log.d("ExerciseService", "Exercise callback registered successfully")
        }

        override fun onRegistrationFailed(throwable: Throwable): Unit {
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
                val finalDistance = distance
                val finalCalories = calories
                val finalHeartRate = heartRate

                if (id != null) {
                    coroutineScope.launch {
                        activityDao.stopActivity(id, System.currentTimeMillis())
                        val activity = activityDao.getActivity(id)
                        if (activity != null) {
                            activityDao.updateMetrics(
                                id,
                                distance = finalDistance ?: activity.distance,
                                calories = finalCalories ?: activity.caloriesBurned,
                                heartRate = finalHeartRate ?: activity.heartRate,
                                lastUpdated = System.currentTimeMillis()
                            )
                        }
                    }
                }

                metricWriterJob?.cancel()
                metricWriterJob = null

                ongoingActivity = false
                currentActivityId = null
                return
            }
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

            heartRate = null
            calories = null
            distance = null

            exerciseClient.startExerciseAsync(config).awaitWithException()
            if (ongoingActivity) return false

            currentActivityId = activityDao.startActivity(
                ActivityEntity(
                    startTime = System.currentTimeMillis(),
                    activityType = exercise.name
                )
            )

            ongoingActivity = true

            val sessionId = currentActivityId!!

            metricWriterJob = coroutineScope.launch {
                while (ongoingActivity) {

                    delay(30_000) // 30 seconds

                    if (!ongoingActivity || currentActivityId != sessionId) {
                        break
                    }

                    if (distance != null && calories != null) {

                        activityDao.updateMetrics(
                            id = sessionId,
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
            endExercise()
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
            return true
        } catch (e: Exception) {
            Log.d("ExerciseService", "Error pausing exercise: ${e.message}")
            return false
        }
    }

    suspend fun resumeExercise(): Boolean {
        try {
            exerciseClient.resumeExerciseAsync().awaitWithException()
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

            metricWriterJob?.cancel()
            metricWriterJob = null

            ongoingActivity = false
            currentActivityId = null

            return true

        } catch (e: Exception) {
            Log.d("ExerciseService", "Error ending exercise: ${e.message}")
            return false
        }
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

    override fun onBind(intent: android.content.Intent?): android.os.IBinder = binder
}
