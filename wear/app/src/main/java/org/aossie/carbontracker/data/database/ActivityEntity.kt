package org.aossie.carbontracker.data.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "activity_data")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String,
    val startTime: Long,
    val heartRate: Double? = null,
    val endTime: Long? = null,
    val distance: Double = 0.0,
    val caloriesBurned: Double = 0.0,
    val isSynced: Boolean = false
)
