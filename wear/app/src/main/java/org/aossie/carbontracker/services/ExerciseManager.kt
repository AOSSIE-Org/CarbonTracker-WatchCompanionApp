package org.aossie.carbontracker.services

import android.content.Context
import android.util.Log
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseType
import org.aossie.carbontracker.data.database.ActivityEntity
import org.aossie.carbontracker.data.database.AppDatabase
import org.aossie.carbontracker.providers.HealthClientProvider

class ExerciseManager(private val context: Context) {

    val healthClient = HealthClientProvider.getClient()
    val exerciseClient = healthClient.exerciseClient

    suspend fun checkAvailableExercises(): List<ExerciseType> {

        val supportedExercises = mutableListOf<ExerciseType>()
        try {
            val capabilities = exerciseClient.getCapabilitiesAsync().awaitWithException()
            val activities = listOf(

                ExerciseType.WALKING,

                ExerciseType.RUNNING,

                ExerciseType.BIKING

            )

            val requiredMetrics = setOf(
                DataType.HEART_RATE_BPM,
                DataType.CALORIES_TOTAL,
                DataType.DISTANCE
            )

            for (activity in activities) {
                if (activity !in capabilities.supportedExerciseTypes) {
                    Log.d("Exercise", "$activity is NOT supported")
                    continue
                }

                val exerciseCapabilities = capabilities.getExerciseTypeCapabilities(activity)

                val supportedMetrics = exerciseCapabilities.supportedDataTypes

                val allMetricsSupported = requiredMetrics.all { it in supportedMetrics }

                if (allMetricsSupported) {
                    Log.d("Exercise", "$activity is supported with all required metrics")
                    supportedExercises.add(activity)
                }

            }
        } catch (exception: Exception) {
            // Handle exception
            Log.d("ExerciseService", "Error checking exercise capabilities: ${exception.message}")
        }

        return supportedExercises
    }

    suspend fun getUnsyncedExercises(): List<ActivityEntity>? {
        try {
            val db = AppDatabase.getInstance(context).activityDao()
            val unsyncedExercises = db.getUnsyncedActivities()

            Log.d("ExerciseService", "Unsynced exercises: $unsyncedExercises")
            return unsyncedExercises

        } catch (exception: Exception) {
            Log.d(
                "ExerciseService",
                "Error fetching unsynced exercises: ${exception.message}"
            )
            return null
        }
    }

    suspend fun markExercisesAsSynced(ids: List<Long>) {
        try {
            val db = AppDatabase.getInstance(context).activityDao()
            db.markSynced(ids)
            Log.d("ExerciseService", "Marked exercises as synced: $ids")
        } catch (exception: Exception) {
            Log.d(
                "ExerciseService",
                "Error marking exercises as synced: ${exception.message}"
            )
        }

    }


}

