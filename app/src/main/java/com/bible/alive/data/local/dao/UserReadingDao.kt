package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bible.alive.data.local.entities.UserReadingHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserReadingDao {

    @Insert
    suspend fun insertReadingHistory(history: UserReadingHistoryEntity): Long

    @Query("SELECT * FROM user_reading_history ORDER BY readAt DESC")
    fun getAllReadingHistory(): Flow<List<UserReadingHistoryEntity>>

    @Query("SELECT * FROM user_reading_history ORDER BY readAt DESC LIMIT :limit")
    fun getRecentReadingHistory(limit: Int): Flow<List<UserReadingHistoryEntity>>

    @Query("SELECT * FROM user_reading_history ORDER BY readAt DESC LIMIT 1")
    suspend fun getLastReading(): UserReadingHistoryEntity?

    @Query("SELECT * FROM user_reading_history WHERE readAt >= :startOfDay AND readAt < :endOfDay ORDER BY readAt DESC")
    suspend fun getReadingsForDay(startOfDay: Long, endOfDay: Long): List<UserReadingHistoryEntity>

    @Query("SELECT SUM(durationSeconds) FROM user_reading_history WHERE readAt >= :startOfDay AND readAt < :endOfDay")
    suspend fun getTotalReadingTimeForDay(startOfDay: Long, endOfDay: Long): Int?

    @Query("SELECT DISTINCT DATE(readAt / 1000, 'unixepoch') as readDate FROM user_reading_history WHERE readAt >= :startTimestamp ORDER BY readDate DESC")
    suspend fun getDistinctReadDates(startTimestamp: Long): List<String>

    @Query("SELECT COUNT(DISTINCT DATE(readAt / 1000, 'unixepoch')) FROM user_reading_history")
    suspend fun getTotalDaysRead(): Int

    @Query("DELETE FROM user_reading_history WHERE readAt < :timestamp")
    suspend fun deleteOldHistory(timestamp: Long)

    @Query("DELETE FROM user_reading_history")
    suspend fun clearAllHistory()
}
