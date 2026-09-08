package org.aossie.carbontracker.data.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface ActivityDao {

    @Insert
    suspend fun startActivity(activity: ActivityEntity): Long

    @Query("SELECT * FROM activity_data WHERE endTime IS NULL LIMIT 1")
    suspend fun getActiveActivity(): ActivityEntity?

    @Query(
        """
        UPDATE activity_data
        SET distance = :distance, caloriesBurned = :calories, isSynced = 0
        WHERE id = :id
    """
    )
    suspend fun updateMetrics(id: Long, distance: Float, calories: Float)

    @Query("UPDATE activity_data SET endTime = :endTime WHERE id = :id")
    suspend fun stopActivity(id: Long, endTime: Long)

    @Query("SELECT * FROM activity_data WHERE isSynced = 0")
    suspend fun getUnsyncedActivities(): List<ActivityEntity>

    @Query("UPDATE activity_data SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("DELETE FROM activity_data WHERE isSynced = 1")
    suspend fun clearSynced()
}
