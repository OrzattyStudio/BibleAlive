package com.bible.alive.data.repository

import com.bible.alive.domain.model.Book
import com.bible.alive.domain.model.Chapter
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.Flow

interface BibleRepository {

    fun getBooks(translation: String): Flow<Result<List<Book>>>

    suspend fun getBooksList(translation: String): Result<List<Book>>

    suspend fun getBook(translation: String, bookNumber: Int): Result<Book>

    fun getChapter(translation: String, bookNumber: Int, chapterNumber: Int): Flow<Result<Chapter>>

    suspend fun getChapterSync(translation: String, bookNumber: Int, chapterNumber: Int): Result<Chapter>

    suspend fun getVerse(translation: String, bookNumber: Int, chapter: Int, verse: Int): Result<Verse>

    suspend fun getRandomVerse(translation: String): Result<Verse>

    fun searchVerses(query: String, translation: String): Flow<Result<List<Verse>>>

    suspend fun refreshBooks(translation: String): Result<Unit>

    suspend fun refreshChapter(translation: String, bookNumber: Int, chapterNumber: Int): Result<Unit>

    suspend fun clearCache()

    suspend fun getCachedChapterCount(translation: String, bookNumber: Int, chapterNumber: Int): Int
}
