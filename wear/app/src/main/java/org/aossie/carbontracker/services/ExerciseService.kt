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
            exerciseClient.startExerciseAsync(config).awaitWithException()
            if (ongoingActivity) return false
            ongoingActivity = true
            coroutineScope.launch {
                currentActivityId = activityDao.startActivity(
                    ActivityEntity(
                        startTime = System.currentTimeMillis(),
                        activityType = exercise.name
                    )
                )

                while (ongoingActivity) {

                    delay(30_000) // 30 seconds

                    val id = currentActivityId ?: continue

                    if (distance != null && calories != null) {

                        activityDao.updateMetrics(
                            id = id,
                            distance = distance!!,
                            calories = calories!!,
                            heartRate = heartRate
                        )
                    }
                }
            }
            return true

        } catch (e: Exception) {
            Log.d("ExerciseService", "Error starting exercise: ${e.message}")
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
                    heartRate = heartRate ?: activity.heartRate
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
                        heartRate = heartRate ?: activity.heartRate
                    )
                }
            }
        } catch (e: Exception) {
            Log.d("ExerciseService", "Error ending exercise: ${e.message}")
            return false
        } finally {
            ongoingActivity = false
            currentActivityId = null
        }
        return true
    }

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.getInstance(applicationContext)
        activityDao = db.activityDao()
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
