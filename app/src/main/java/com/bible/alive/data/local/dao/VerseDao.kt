package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bible.alive.data.local.entities.VerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VerseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: VerseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<VerseEntity>)

    @Query("SELECT * FROM verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter ORDER BY verse ASC")
    fun getChapterVerses(translation: String, bookNumber: Int, chapter: Int): Flow<List<VerseEntity>>

    @Query("SELECT * FROM verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter ORDER BY verse ASC")
    suspend fun getChapterVersesList(translation: String, bookNumber: Int, chapter: Int): List<VerseEntity>

    @Query("SELECT * FROM verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse LIMIT 1")
    suspend fun getVerse(translation: String, bookNumber: Int, chapter: Int, verse: Int): VerseEntity?

    @Query("SELECT * FROM verses WHERE id = :id")
    suspend fun getVerseById(id: Long): VerseEntity?

    @Query("SELECT * FROM verses WHERE text LIKE '%' || :query || '%' AND translation = :translation ORDER BY bookNumber, chapter, verse")
    fun searchVerses(query: String, translation: String): Flow<List<VerseEntity>>

    @Query("DELETE FROM verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter")
    suspend fun deleteChapterVerses(translation: String, bookNumber: Int, chapter: Int)

    @Query("DELETE FROM verses WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    @Query("SELECT COUNT(*) FROM verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter")
    suspend fun getChapterVerseCount(translation: String, bookNumber: Int, chapter: Int): Int
}
