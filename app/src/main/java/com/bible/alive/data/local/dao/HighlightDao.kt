package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bible.alive.data.local.entities.HighlightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity): Long

    @Update
    suspend fun updateHighlight(highlight: HighlightEntity)

    @Delete
    suspend fun deleteHighlight(highlight: HighlightEntity)

    @Query("DELETE FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse")
    suspend fun deleteHighlightByReference(translation: String, bookNumber: Int, chapter: Int, verse: Int)

    @Query("SELECT * FROM highlights ORDER BY highlightedAt DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights ORDER BY highlightedAt DESC LIMIT :limit")
    fun getRecentHighlights(limit: Int): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights WHERE id = :id")
    suspend fun getHighlightById(id: Long): HighlightEntity?

    @Query("SELECT * FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse LIMIT 1")
    suspend fun getHighlightByReference(translation: String, bookNumber: Int, chapter: Int, verse: Int): HighlightEntity?

    @Query("SELECT * FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter ORDER BY verse ASC")
    fun getHighlightsForChapter(translation: String, bookNumber: Int, chapter: Int): Flow<List<HighlightEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse)")
    suspend fun isHighlighted(translation: String, bookNumber: Int, chapter: Int, verse: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse)")
    fun observeIsHighlighted(translation: String, bookNumber: Int, chapter: Int, verse: Int): Flow<Boolean>

    @Query("SELECT colorHex FROM highlights WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse")
    suspend fun getHighlightColor(translation: String, bookNumber: Int, chapter: Int, verse: Int): String?

    @Query("SELECT COUNT(*) FROM highlights")
    suspend fun getHighlightsCount(): Int

    @Query("DELETE FROM highlights")
    suspend fun clearAllHighlights()
}
