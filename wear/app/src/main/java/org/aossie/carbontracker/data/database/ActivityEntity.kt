package org.aossie.carbontracker.data.database

import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(tableName = "activity_data")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityType: String,
    val startTime: Long,
    val endTime: Long? = null,
    val distance: Float = 0f,
    val caloriesBurned: Float = 0f,
    val isSynced: Boolean = false
)
