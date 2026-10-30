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
import org.aossie.carbontracker.data.models.ExerciseAcknowledgement


class WearListenerService : WearableListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {

        private const val TAG = "WearListenerService"
        private const val MAX_PAYLOAD_BYTES = 16 * 1024
        private const val MAX_ACKS = 500

    }

    override fun onCreate() {
        super.onCreate()

        Log.d(
            TAG,
            "SERVICE CREATED"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(
            TAG,
            "WEAR SERVICE DESTROYED"
        )
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(
            TAG,
            "MESSAGE RECEIVED: ${messageEvent.path}"
        )


        if (messageEvent.data.size > MAX_PAYLOAD_BYTES) {
            Log.w(TAG, "Dropping oversized message on ${messageEvent.path}")
            return
        }


        if (messageEvent.path == "/requestHeartRate") {
            // Handle heart rate request
            Log.d(
                TAG,
                "Received request for heart rate"
            )

            val healthServicesManager = HealthServicesManager()
            serviceScope.launch {

                val supportsHeartRate = healthServicesManager.checkCapabilities()
                if (!supportsHeartRate) {
                    Log.e(
                        TAG,
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
                                TAG,
                                "Sent /heartData successfully"
                            )
                        }
                        .addOnFailureListener {
                            Log.e(
                                TAG,
                                "Failed to send /heartData",
                                it
                            )
                        }
                }
            }

        } else if (messageEvent.path == "/requestExerciseData") {

            // Handle exercise data request
            Log.d(
                TAG,
                "Received request for exercise data"
            )

            val exerciseManager = ExerciseManager(this)

            serviceScope.launch {

                val exerciseData = exerciseManager.getUnsyncedExercises()
                if (exerciseData == null) {
                    Log.e(
                        TAG,
                        "Failed to fetch unsynced exercise data"
                    )
                    return@launch
                }

                val exerciseJson = try {
                    Json.encodeToString(exerciseData)

                } catch (e: Exception) {
                    Log.e(TAG, "Failed to encode exercise data", e)
                    return@launch
                }

                Log.d(
                    TAG,
                    "Encoded ${exerciseData.size} exercise records"
                )

                Wearable.getMessageClient(this@WearListenerService)
                    .sendMessage(
                        messageEvent.sourceNodeId,
                        "/exerciseData",
                        exerciseJson.toByteArray()
                    )
                    .addOnSuccessListener {
                        Log.d(TAG, "Sent /exerciseData successfully")
                    }
                    .addOnFailureListener {
                        Log.e(
                            TAG,
                            "Failed to send /exerciseData",
                            it
                        )
                    }
            }
        } else if (messageEvent.path == "/markExercisesAsSynced") {
            // Handle mark exercises as synced request
            Log.d(
                TAG,
                "Received request to mark exercises as synced"
            )

            val exerciseManager = ExerciseManager(this)

            serviceScope.launch {
                val acknowledgements = try {
                    Json.decodeFromString<List<ExerciseAcknowledgement>>(
                        String(messageEvent.data)
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Failed to decode exercise acknowledgements",
                        e
                    )
                    return@launch
                }

                if (acknowledgements.size > MAX_ACKS) {
                    Log.w(TAG, "Too many exercise acknowledgements")
                    return@launch
                }

                try {
                    acknowledgements.forEach { acknowledgement ->

                        if (acknowledgement.id <= 0) {
                            Log.w(TAG, "Ignoring acknowledgement with invalid ID")
                            return@forEach
                        }

                        if (acknowledgement.lastUpdated <= 0L
                        ) {
                            Log.w(
                                TAG,
                                "Ignoring acknowledgement with invalid timestamp"
                            )
                            return@forEach
                        }

                        exerciseManager.markExercisesAsSynced(
                            acknowledgement.id,
                            acknowledgement.lastUpdated
                        )
                    }

                    Log.d(
                        TAG,
                        "Processed ${acknowledgements.size} exercise acknowledgements"
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Failed to mark exercises as synced",
                        e
                    )
                }
            }
        }
    }
}
