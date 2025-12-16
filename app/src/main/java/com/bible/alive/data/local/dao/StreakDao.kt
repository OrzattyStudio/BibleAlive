package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bible.alive.data.local.entities.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Update
    suspend fun updateStreak(streak: StreakEntity)

    @Query("SELECT * FROM streaks WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): StreakEntity?

    @Query("SELECT * FROM streaks WHERE id = 1 LIMIT 1")
    fun observeStreak(): Flow<StreakEntity?>

    @Query("UPDATE streaks SET currentStreak = :currentStreak, lastReadDate = :lastReadDate WHERE id = 1")
    suspend fun updateCurrentStreak(currentStreak: Int, lastReadDate: Long)

    @Query("UPDATE streaks SET longestStreak = :longestStreak WHERE id = 1")
    suspend fun updateLongestStreak(longestStreak: Int)

    @Query("UPDATE streaks SET totalDaysRead = totalDaysRead + 1 WHERE id = 1")
    suspend fun incrementTotalDaysRead()

    @Query("UPDATE streaks SET currentStreak = 0 WHERE id = 1")
    suspend fun resetCurrentStreak()
}
