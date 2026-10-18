package org.aossie.carbontracker

import android.app.Application
import org.aossie.carbontracker.providers.HealthClientProvider

class CarbonTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        HealthClientProvider.initialize(this)
    }
}
