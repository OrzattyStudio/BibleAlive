package com.bible.alive.domain.usecase

import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.domain.model.Book
import com.bible.alive.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {

    operator fun invoke(translation: String): Flow<Result<List<Book>>> {
        return bibleRepository.getBooks(translation)
    }

    suspend fun getSync(translation: String): Result<List<Book>> {
        return bibleRepository.getBooksList(translation)
    }

    suspend fun getBook(translation: String, bookNumber: Int): Result<Book> {
        return bibleRepository.getBook(translation, bookNumber)
    }

    fun getOldTestamentBooks(books: List<Book>): List<Book> {
        return books.filter { it.isOldTestament }
    }

    fun getNewTestamentBooks(books: List<Book>): List<Book> {
        return books.filter { it.isNewTestament }
    }

    suspend fun refresh(translation: String): Result<Unit> {
        return bibleRepository.refreshBooks(translation)
    }
}
