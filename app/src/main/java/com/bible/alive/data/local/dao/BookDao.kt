package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bible.alive.data.local.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Query("SELECT * FROM books WHERE translation = :translation ORDER BY bookNumber ASC")
    fun getBooks(translation: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE translation = :translation ORDER BY bookNumber ASC")
    suspend fun getBooksList(translation: String): List<BookEntity>

    @Query("SELECT * FROM books WHERE translation = :translation AND bookNumber = :bookNumber LIMIT 1")
    suspend fun getBook(translation: String, bookNumber: Int): BookEntity?

    @Query("SELECT * FROM books WHERE translation = :translation AND testament = :testament ORDER BY bookNumber ASC")
    fun getBooksByTestament(translation: String, testament: String): Flow<List<BookEntity>>

    @Query("DELETE FROM books WHERE translation = :translation")
    suspend fun deleteBooks(translation: String)

    @Query("SELECT COUNT(*) FROM books WHERE translation = :translation")
    suspend fun getBookCount(translation: String): Int

    @Query("DELETE FROM books WHERE cachedAt < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)
}
