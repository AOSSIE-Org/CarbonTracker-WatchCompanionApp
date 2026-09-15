package org.aossie.carbontracker.services

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WearListenerService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()

        Log.d(
            "WearListenerService",
            "SERVICE CREATED"
        )
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.d(
            "WearListenerService",
            "MESSAGE RECEIVED: ${messageEvent.path}"
        )

        if (messageEvent.path == "/requestWatchData") {

            Wearable.getMessageClient(this)
                .sendMessage(
                    messageEvent.sourceNodeId,
                    "/watchData",
                    "Watch Connected".toByteArray()
                )
                .addOnSuccessListener {
                    Log.d(
                        "WearListenerService",
                        "Sent /watchData successfully"
                    )
                }
                .addOnFailureListener {
                    Log.e(
                        "WearListenerService",
                        "Failed to send /watchData",
                        it
                    )
                }
        } else if (messageEvent.path == "/requestHeartRate") {
            // Handle heart rate request
            Log.d(
                "WearListenerService",
                "Received request for heart rate"
            )

            val healthServicesManager = HealthServicesManager()
            CoroutineScope(Dispatchers.IO).launch {

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

            val healthServicesManager = HealthServicesManager()

            val exerciseData = ""

            Wearable.getMessageClient(this)
                .sendMessage(
                    messageEvent.sourceNodeId,
                    "/exerciseData",
                    "$exerciseData".toByteArray()
                )
                .addOnSuccessListener {
                    Log.d(
                        "WearListenerService",
                        "Sent /exerciseData successfully"
                    )
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
