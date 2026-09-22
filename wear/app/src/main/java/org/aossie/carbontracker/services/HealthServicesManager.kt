package org.aossie.carbontracker.services

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.DataType
import androidx.health.services.client.awaitWithException
import androidx.health.services.client.data.Availability
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DeltaDataType
import org.aossie.carbontracker.providers.HealthClientProvider

class HealthServicesManager() {

    private val healthClient = HealthClientProvider.getClient()
    private val measureClient = healthClient.measureClient


    suspend fun checkCapabilities(): Boolean {

        var supportsHeartRate = false
        try {
            val capabilities =
                healthClient.measureClient.getCapabilitiesAsync().awaitWithException()

            supportsHeartRate =
                DataType.HEART_RATE_BPM in capabilities.supportedDataTypesMeasure

            Log.d(
                "HealthServices",
                "Supports heart rate: $supportsHeartRate"
            )

        } catch (exception: Exception) {
            Log.e(
                "HealthServices",
                "Failed to get capabilities",
                exception
            )
        }

        return supportsHeartRate
    }

    fun getHeartRate(onResult: (Double?) -> Unit) {

        // Check if the device supports heart rate measurement

        var heartRate: Double? = null
        var alreadyResponded = false

        Log.d("HealthServices", "Starting heart rate measurement")

        val callback = object : MeasureCallback {

            override fun onAvailabilityChanged(
                dataType: DeltaDataType<*, *>,
                availability: Availability
            ) {
                Log.d(
                    "HealthServices",
                    "Heart rate availability: $availability"
                )
            }

            override fun onDataReceived(data: DataPointContainer) {
                val heartRateData = data.getData(DataType.HEART_RATE_BPM)
                if (heartRateData.isNotEmpty() && !alreadyResponded) {
                    val value = heartRateData.last().value
                    Log.d("HealthServices", "Received heart rate data: $value")

                    alreadyResponded = true
                    measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, this)
                    onResult(value)
                }
            }
        }

        Log.d("HealthServices", "Registering heart rate callback")

        measureClient.registerMeasureCallback(
            DataType.HEART_RATE_BPM,
            callback
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if (!alreadyResponded) {
                Log.d("HealthServices", "Heart rate request timed out")
                alreadyResponded = true
                measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
                onResult(null)
            }
        }, 5000)
    }
}
