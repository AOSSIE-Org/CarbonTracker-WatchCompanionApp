package org.aossie.carbontracker.providers

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient

object HealthClientProvider {

    private lateinit var client: HealthServicesClient

    fun initialize(context: Context) {
        client = HealthServices.getClient(context)
    }

    fun getClient(): HealthServicesClient {
        return client
    }
}
