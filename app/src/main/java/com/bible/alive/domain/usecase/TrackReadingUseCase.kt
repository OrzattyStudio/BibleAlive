package com.bible.alive.domain.usecase

import com.bible.alive.data.local.dao.UserReadingDao
import com.bible.alive.data.local.entities.UserReadingHistoryEntity
import com.bible.alive.data.repository.StreakRepository
import com.bible.alive.domain.model.ReadingProgress
import com.bible.alive.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class TrackReadingUseCase @Inject constructor(
    private val userReadingDao: UserReadingDao,
    private val streakRepository: StreakRepository
) {

    suspend operator fun invoke(
        translation: String,
        bookNumber: Int,
        bookName: String,
        chapter: Int,
        verse: Int? = null,
        durationSeconds: Int = 0
    ): Result<ReadingProgress> {
        return try {
            val entity = UserReadingHistoryEntity(
                translation = translation,
                bookNumber = bookNumber,
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                durationSeconds = durationSeconds
            )
            val id = userReadingDao.insertReadingHistory(entity)

            streakRepository.recordReading()

            Result.Success(
                ReadingProgress(
                    id = id,
                    translation = translation,
                    bookNumber = bookNumber,
                    bookName = bookName,
                    chapter = chapter,
                    verse = verse,
                    readAt = entity.readAt,
                    durationSeconds = durationSeconds
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getRecentReadings(limit: Int = 10): Flow<List<ReadingProgress>> {
        return userReadingDao.getRecentReadingHistory(limit).map { entities ->
            entities.map { entity ->
                ReadingProgress(
                    id = entity.id,
                    translation = entity.translation,
                    bookNumber = entity.bookNumber,
                    bookName = entity.bookName,
                    chapter = entity.chapter,
                    verse = entity.verse,
                    readAt = entity.readAt,
                    durationSeconds = entity.durationSeconds
                )
            }
        }
    }

    suspend fun getLastReading(): ReadingProgress? {
        return userReadingDao.getLastReading()?.let { entity ->
            ReadingProgress(
                id = entity.id,
                translation = entity.translation,
                bookNumber = entity.bookNumber,
                bookName = entity.bookName,
                chapter = entity.chapter,
                verse = entity.verse,
                readAt = entity.readAt,
                durationSeconds = entity.durationSeconds
            )
        }
    }

    suspend fun getTodayStats(): ReadingStats {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val endOfDay = calendar.timeInMillis

        val readings = userReadingDao.getReadingsForDay(startOfDay, endOfDay)
        val totalTime = userReadingDao.getTotalReadingTimeForDay(startOfDay, endOfDay) ?: 0

        val uniqueChapters = readings
            .map { "${it.translation}-${it.bookNumber}-${it.chapter}" }
            .distinct()
            .size

        val uniqueBooks = readings
            .map { "${it.translation}-${it.bookNumber}" }
            .distinct()
            .size

        return ReadingStats(
            chaptersRead = uniqueChapters,
            booksRead = uniqueBooks,
            totalReadingTimeSeconds = totalTime,
            sessionsCount = readings.size
        )
    }

    suspend fun getTotalDaysRead(): Int {
        return userReadingDao.getTotalDaysRead()
    }

    suspend fun clearOldHistory(daysToKeep: Int = 90) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep)
        userReadingDao.deleteOldHistory(calendar.timeInMillis)
    }

    data class ReadingStats(
        val chaptersRead: Int,
        val booksRead: Int,
        val totalReadingTimeSeconds: Int,
        val sessionsCount: Int
    ) {
        val totalReadingTimeMinutes: Int
            get() = totalReadingTimeSeconds / 60

        val formattedReadingTime: String
            get() {
                val hours = totalReadingTimeSeconds / 3600
                val minutes = (totalReadingTimeSeconds % 3600) / 60
                return when {
                    hours > 0 -> "${hours}h ${minutes}m"
                    minutes > 0 -> "${minutes}m"
                    else -> "< 1m"
                }
            }
    }
}
