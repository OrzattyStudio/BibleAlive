package com.bible.alive.data.repository

import com.bible.alive.data.local.dao.BookDao
import com.bible.alive.data.local.dao.VerseDao
import com.bible.alive.data.local.entities.BookEntity
import com.bible.alive.data.local.entities.VerseEntity
import com.bible.alive.data.remote.api.BollsLifeApi
import com.bible.alive.domain.model.Book
import com.bible.alive.domain.model.Chapter
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BibleRepositoryImpl @Inject constructor(
    private val api: BollsLifeApi,
    private val bookDao: BookDao,
    private val verseDao: VerseDao
) : BibleRepository {

    override fun getBooks(translation: String): Flow<Result<List<Book>>> = flow {
        emit(Result.Loading)
        val cached = bookDao.getBooksList(translation)
        if (cached.isNotEmpty()) {
            emit(Result.Success(cached.map { it.toDomain() }))
        }
        try {
            val response = api.getBooks(translation)
            if (response.isSuccessful && response.body() != null) {
                val books = response.body()!!.mapIndexed { index, dto ->
                    BookEntity(
                        translation = translation,
                        bookNumber = dto.bookId,
                        bookName = dto.name,
                        chapters = dto.chapters,
                        testament = if (dto.bookId <= 39) "OLD" else "NEW"
                    )
                }
                bookDao.insertBooks(books)
                emit(Result.Success(books.map { it.toDomain() }))
            } else if (cached.isEmpty()) {
                emit(Result.Error(Exception("Failed to fetch books: ${response.message()}")))
            }
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(Result.Error(e))
            }
        }
    }.catch { emit(Result.Error(it)) }

    override suspend fun getBooksList(translation: String): Result<List<Book>> {
        return try {
            val cached = bookDao.getBooksList(translation)
            if (cached.isNotEmpty()) {
                return Result.Success(cached.map { it.toDomain() })
            }
            val response = api.getBooks(translation)
            if (response.isSuccessful && response.body() != null) {
                val books = response.body()!!.map { dto ->
                    BookEntity(
                        translation = translation,
                        bookNumber = dto.bookId,
                        bookName = dto.name,
                        chapters = dto.chapters,
                        testament = if (dto.bookId <= 39) "OLD" else "NEW"
                    )
                }
                bookDao.insertBooks(books)
                Result.Success(books.map { it.toDomain() })
            } else {
                Result.Error(Exception("Failed to fetch books: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBook(translation: String, bookNumber: Int): Result<Book> {
        return try {
            val cached = bookDao.getBook(translation, bookNumber)
            if (cached != null) {
                return Result.Success(cached.toDomain())
            }
            getBooksList(translation)
            val book = bookDao.getBook(translation, bookNumber)
            if (book != null) {
                Result.Success(book.toDomain())
            } else {
                Result.Error(Exception("Book not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getChapter(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Flow<Result<Chapter>> = flow {
        emit(Result.Loading)
        val cached = verseDao.getChapterVersesList(translation, bookNumber, chapterNumber)
        val book = bookDao.getBook(translation, bookNumber)
        val bookName = book?.bookName ?: ""

        if (cached.isNotEmpty()) {
            emit(Result.Success(cached.toChapter(translation, bookNumber, bookName, chapterNumber)))
        }

        try {
            val response = api.getChapter(translation, bookNumber, chapterNumber)
            if (response.isSuccessful && response.body() != null) {
                val verses = response.body()!!.mapIndexed { _, dto ->
                    VerseEntity(
                        translation = translation,
                        bookNumber = bookNumber,
                        bookName = bookName,
                        chapter = chapterNumber,
                        verse = dto.verse,
                        text = dto.text
                    )
                }
                verseDao.insertVerses(verses)
                emit(Result.Success(verses.toChapter(translation, bookNumber, bookName, chapterNumber)))
            } else if (cached.isEmpty()) {
                emit(Result.Error(Exception("Failed to fetch chapter: ${response.message()}")))
            }
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(Result.Error(e))
            }
        }
    }.catch { emit(Result.Error(it)) }

    override suspend fun getChapterSync(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Result<Chapter> {
        return try {
            val cached = verseDao.getChapterVersesList(translation, bookNumber, chapterNumber)
            val book = bookDao.getBook(translation, bookNumber)
            val bookName = book?.bookName ?: ""

            if (cached.isNotEmpty()) {
                return Result.Success(cached.toChapter(translation, bookNumber, bookName, chapterNumber))
            }

            val response = api.getChapter(translation, bookNumber, chapterNumber)
            if (response.isSuccessful && response.body() != null) {
                val verses = response.body()!!.map { dto ->
                    VerseEntity(
                        translation = translation,
                        bookNumber = bookNumber,
                        bookName = bookName,
                        chapter = chapterNumber,
                        verse = dto.verse,
                        text = dto.text
                    )
                }
                verseDao.insertVerses(verses)
                Result.Success(verses.toChapter(translation, bookNumber, bookName, chapterNumber))
            } else {
                Result.Error(Exception("Failed to fetch chapter: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getVerse(
        translation: String,
        bookNumber: Int,
        chapter: Int,
        verse: Int
    ): Result<Verse> {
        return try {
            val cached = verseDao.getVerse(translation, bookNumber, chapter, verse)
            if (cached != null) {
                return Result.Success(cached.toDomain())
            }

            val response = api.getVerse(translation, bookNumber, chapter, verse)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val book = bookDao.getBook(translation, bookNumber)
                val verseEntity = VerseEntity(
                    translation = translation,
                    bookNumber = bookNumber,
                    bookName = book?.bookName ?: dto.bookName ?: "",
                    chapter = chapter,
                    verse = verse,
                    text = dto.text
                )
                verseDao.insertVerse(verseEntity)
                Result.Success(verseEntity.toDomain())
            } else {
                Result.Error(Exception("Failed to fetch verse: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRandomVerse(translation: String): Result<Verse> {
        return try {
            val response = api.getRandomVerse(translation)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                val verse = Verse(
                    translation = translation,
                    bookNumber = dto.book,
                    bookName = dto.bookName ?: "",
                    chapter = dto.chapter,
                    verse = dto.verse,
                    text = dto.text
                )
                Result.Success(verse)
            } else {
                Result.Error(Exception("Failed to fetch random verse: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun searchVerses(query: String, translation: String): Flow<Result<List<Verse>>> {
        return verseDao.searchVerses(query, translation)
            .map { entities -> Result.Success(entities.map { it.toDomain() }) as Result<List<Verse>> }
            .catch { emit(Result.Error(it)) }
    }

    override suspend fun refreshBooks(translation: String): Result<Unit> {
        return try {
            val response = api.getBooks(translation)
            if (response.isSuccessful && response.body() != null) {
                bookDao.deleteBooks(translation)
                val books = response.body()!!.map { dto ->
                    BookEntity(
                        translation = translation,
                        bookNumber = dto.bookId,
                        bookName = dto.name,
                        chapters = dto.chapters,
                        testament = if (dto.bookId <= 39) "OLD" else "NEW"
                    )
                }
                bookDao.insertBooks(books)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to refresh books"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun refreshChapter(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Result<Unit> {
        return try {
            val response = api.getChapter(translation, bookNumber, chapterNumber)
            if (response.isSuccessful && response.body() != null) {
                verseDao.deleteChapterVerses(translation, bookNumber, chapterNumber)
                val book = bookDao.getBook(translation, bookNumber)
                val verses = response.body()!!.map { dto ->
                    VerseEntity(
                        translation = translation,
                        bookNumber = bookNumber,
                        bookName = book?.bookName ?: "",
                        chapter = chapterNumber,
                        verse = dto.verse,
                        text = dto.text
                    )
                }
                verseDao.insertVerses(verses)
                Result.Success(Unit)
            } else {
                Result.Error(Exception("Failed to refresh chapter"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun clearCache() {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        verseDao.deleteOldCache(oneWeekAgo)
        bookDao.deleteOldCache(oneWeekAgo)
    }

    override suspend fun getCachedChapterCount(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Int {
        return verseDao.getChapterVerseCount(translation, bookNumber, chapterNumber)
    }

    private fun BookEntity.toDomain(): Book {
        return Book(
            id = id,
            translation = translation,
            bookNumber = bookNumber,
            name = bookName,
            chapters = chapters,
            testament = Book.Testament.fromString(testament)
        )
    }

    private fun VerseEntity.toDomain(): Verse {
        return Verse(
            id = id,
            translation = translation,
            bookNumber = bookNumber,
            bookName = bookName,
            chapter = chapter,
            verse = verse,
            text = text
        )
    }

    private fun List<VerseEntity>.toChapter(
        translation: String,
        bookNumber: Int,
        bookName: String,
        chapterNumber: Int
    ): Chapter {
        return Chapter(
            translation = translation,
            bookNumber = bookNumber,
            bookName = bookName,
            chapterNumber = chapterNumber,
            verses = this.map { it.toDomain() }
        )
    }
}
