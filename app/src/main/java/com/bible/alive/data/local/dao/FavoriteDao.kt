package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bible.alive.data.local.entities.FavoriteVerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteVerseEntity): Long

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteVerseEntity)

    @Query("DELETE FROM favorite_verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse")
    suspend fun deleteFavoriteByReference(translation: String, bookNumber: Int, chapter: Int, verse: Int)

    @Query("SELECT * FROM favorite_verses ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteVerseEntity>>

    @Query("SELECT * FROM favorite_verses ORDER BY addedAt DESC LIMIT :limit")
    fun getRecentFavorites(limit: Int): Flow<List<FavoriteVerseEntity>>

    @Query("SELECT * FROM favorite_verses WHERE id = :id")
    suspend fun getFavoriteById(id: Long): FavoriteVerseEntity?

    @Query("SELECT * FROM favorite_verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse LIMIT 1")
    suspend fun getFavoriteByReference(translation: String, bookNumber: Int, chapter: Int, verse: Int): FavoriteVerseEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse)")
    suspend fun isFavorite(translation: String, bookNumber: Int, chapter: Int, verse: Int): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_verses WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse)")
    fun observeIsFavorite(translation: String, bookNumber: Int, chapter: Int, verse: Int): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM favorite_verses")
    suspend fun getFavoritesCount(): Int

    @Query("DELETE FROM favorite_verses")
    suspend fun clearAllFavorites()
}
