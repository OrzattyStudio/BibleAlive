package com.bible.alive.domain.usecase

import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.domain.model.Chapter
import com.bible.alive.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChapterUseCase @Inject constructor(
    private val bibleRepository: BibleRepository
) {

    operator fun invoke(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Flow<Result<Chapter>> {
        return bibleRepository.getChapter(translation, bookNumber, chapterNumber)
    }

    suspend fun getSync(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Result<Chapter> {
        return bibleRepository.getChapterSync(translation, bookNumber, chapterNumber)
    }

    suspend fun refresh(
        translation: String,
        bookNumber: Int,
        chapterNumber: Int
    ): Result<Unit> {
        return bibleRepository.refreshChapter(translation, bookNumber, chapterNumber)
    }
}
