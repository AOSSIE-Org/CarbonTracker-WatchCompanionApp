package org.aossie.carbontracker.services

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService

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
        }
    }
}
