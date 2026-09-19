package org.aossie.carbontracker.services

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class WearListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        Log.d(
            "WearListenerService",
            "SERVICE CREATED"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(
            "WearListenerService",
            "WEAR SERVICE DESTROYED"
        )
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(
            "WearListenerService",
            "MESSAGE RECEIVED: ${messageEvent.path}"
        )

        if (messageEvent.path == "/requestHeartRate") {
            // Handle heart rate request
            Log.d(
                "WearListenerService",
                "Received request for heart rate"
            )

            val healthServicesManager = HealthServicesManager()
            serviceScope.launch {

                val supportsHeartRate = healthServicesManager.checkCapabilities()
                if (!supportsHeartRate) {
                    Log.e(
                        "WearListenerService",
                        "Device does not support heart rate measurement"
                    )
                    return@launch
                }

                healthServicesManager.getHeartRate { heartRate ->
                    Wearable.getMessageClient(this@WearListenerService)
                        .sendMessage(
                            messageEvent.sourceNodeId,
                            "/heartRateData",
                            "$heartRate".toByteArray()
                        )
                        .addOnSuccessListener {
                            Log.d(
                                "WearListenerService",
                                "Sent /heartData successfully"
                            )
                        }
                        .addOnFailureListener {
                            Log.e(
                                "WearListenerService",
                                "Failed to send /heartData",
                                it
                            )
                        }
                }
            }

        } else if (messageEvent.path == "/requestExerciseData") {
            // Handle exercise data request
            Log.d(
                "WearListenerService",
                "Received request for exercise data"
            )

            val exerciseManager = ExerciseManager(this)

            serviceScope.launch {

                val exerciseData = exerciseManager.getUnsyncedExercises()
                if (exerciseData == null) {
                    Log.e(
                        "WearListenerService",
                        "Failed to fetch unsynced exercise data"
                    )
                    return@launch
                }

                val exerciseJson = try {
                    Json.encodeToString(exerciseData)

                } catch (e: Exception) {
                    Log.e("WearListenerService", "Failed to encode exercise data", e)
                    return@launch
                }

                Log.d("WearListenerService", "Encoded exercise data to JSON: $exerciseJson")

                Wearable.getMessageClient(this@WearListenerService)
                    .sendMessage(
                        messageEvent.sourceNodeId,
                        "/exerciseData",
                        exerciseJson.toByteArray()
                    )
                    .addOnSuccessListener {
                        Log.d("WearListenerService", "Sent /exerciseData successfully")
                        serviceScope.launch {
                            try {
                                exerciseManager.markExercisesAsSynced(exerciseData.map { it.id })
                            } catch (e: Exception) {
                                Log.e(
                                    "WearListenerService",
                                    "Failed to mark exercises as synced",
                                    e
                                )
                            }
                        }
                    }
                    .addOnFailureListener {
                        Log.e(
                            "WearListenerService",
                            "Failed to send /exerciseData",
                            it
                        )
                    }
            }
        }
    }
}
