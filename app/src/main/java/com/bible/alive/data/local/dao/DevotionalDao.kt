package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bible.alive.data.local.entities.DevotionalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DevotionalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevotional(devotional: DevotionalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevotionals(devotionals: List<DevotionalEntity>)

    @Update
    suspend fun updateDevotional(devotional: DevotionalEntity)

    @Query("SELECT * FROM devotionals WHERE date = :date LIMIT 1")
    suspend fun getDevotionalByDate(date: String): DevotionalEntity?

    @Query("SELECT * FROM devotionals WHERE date = :date LIMIT 1")
    fun observeDevotionalByDate(date: String): Flow<DevotionalEntity?>

    @Query("SELECT * FROM devotionals ORDER BY date DESC")
    fun getAllDevotionals(): Flow<List<DevotionalEntity>>

    @Query("SELECT * FROM devotionals ORDER BY date DESC LIMIT :limit")
    fun getRecentDevotionals(limit: Int): Flow<List<DevotionalEntity>>

    @Query("UPDATE devotionals SET isRead = :isRead WHERE date = :date")
    suspend fun markAsRead(date: String, isRead: Boolean = true)

    @Query("SELECT * FROM devotionals WHERE isRead = 0 ORDER BY date ASC LIMIT 1")
    suspend fun getNextUnreadDevotional(): DevotionalEntity?

    @Query("SELECT COUNT(*) FROM devotionals WHERE isRead = 1")
    suspend fun getReadDevotionalsCount(): Int

    @Query("DELETE FROM devotionals WHERE date < :date")
    suspend fun deleteOldDevotionals(date: String)

    @Query("DELETE FROM devotionals")
    suspend fun clearAllDevotionals()
}
