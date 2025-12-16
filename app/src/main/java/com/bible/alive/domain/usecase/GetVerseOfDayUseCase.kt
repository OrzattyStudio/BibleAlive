package com.bible.alive.domain.usecase

import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject

class GetVerseOfDayUseCase @Inject constructor(
    private val bibleRepository: BibleRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    private val popularVerses = listOf(
        VerseLocation(43, 3, 16),    // Juan 3:16
        VerseLocation(19, 23, 1),    // Salmos 23:1
        VerseLocation(20, 3, 5),     // Proverbios 3:5
        VerseLocation(45, 8, 28),    // Romanos 8:28
        VerseLocation(50, 4, 13),    // Filipenses 4:13
        VerseLocation(23, 40, 31),   // Isaías 40:31
        VerseLocation(24, 29, 11),   // Jeremías 29:11
        VerseLocation(19, 46, 1),    // Salmos 46:1
        VerseLocation(6, 1, 9),      // Josué 1:9
        VerseLocation(40, 11, 28),   // Mateo 11:28
        VerseLocation(19, 27, 1),    // Salmos 27:1
        VerseLocation(20, 16, 3),    // Proverbios 16:3
        VerseLocation(58, 11, 1),    // Hebreos 11:1
        VerseLocation(62, 4, 8),     // 1 Juan 4:8
        VerseLocation(19, 119, 105), // Salmos 119:105
        VerseLocation(45, 12, 2),    // Romanos 12:2
        VerseLocation(49, 2, 8),     // Efesios 2:8
        VerseLocation(19, 37, 4),    // Salmos 37:4
        VerseLocation(40, 28, 20),   // Mateo 28:20
        VerseLocation(50, 4, 6),     // Filipenses 4:6
        VerseLocation(46, 13, 4),    // 1 Corintios 13:4
        VerseLocation(23, 41, 10),   // Isaías 41:10
        VerseLocation(19, 91, 1),    // Salmos 91:1
        VerseLocation(48, 5, 22),    // Gálatas 5:22
        VerseLocation(40, 6, 33),    // Mateo 6:33
        VerseLocation(59, 1, 5),     // Santiago 1:5
        VerseLocation(19, 34, 8),    // Salmos 34:8
        VerseLocation(47, 5, 17),    // 2 Corintios 5:17
        VerseLocation(66, 3, 20),    // Apocalipsis 3:20
        VerseLocation(19, 103, 1),   // Salmos 103:1
        VerseLocation(43, 14, 6),    // Juan 14:6
    )

    suspend operator fun invoke(): Result<Verse> {
        val translation = userPreferencesRepository.preferredTranslation.first()
        return getVerseOfDay(translation)
    }

    suspend fun getVerseOfDay(translation: String): Result<Verse> {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val verseIndex = dayOfYear % popularVerses.size
        val verseLocation = popularVerses[verseIndex]

        return bibleRepository.getVerse(
            translation = translation,
            bookNumber = verseLocation.book,
            chapter = verseLocation.chapter,
            verse = verseLocation.verse
        )
    }

    suspend fun getRandomVerse(): Result<Verse> {
        val translation = userPreferencesRepository.preferredTranslation.first()
        return bibleRepository.getRandomVerse(translation)
    }

    suspend fun getRandomVerse(translation: String): Result<Verse> {
        return bibleRepository.getRandomVerse(translation)
    }

    private data class VerseLocation(
        val book: Int,
        val chapter: Int,
        val verse: Int
    )
}
