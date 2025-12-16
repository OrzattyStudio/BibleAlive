package com.bible.alive.data.repository

import com.bible.alive.data.local.dao.DevotionalDao
import com.bible.alive.data.local.entities.DevotionalEntity
import com.bible.alive.domain.model.Devotional
import com.bible.alive.domain.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevotionalRepositoryImpl @Inject constructor(
    private val devotionalDao: DevotionalDao
) : DevotionalRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun getTodayDevotional(): Flow<Result<Devotional>> = flow {
        emit(Result.Loading)
        val today = dateFormat.format(Date())
        val devotional = devotionalDao.getDevotionalByDate(today)
        if (devotional != null) {
            emit(Result.Success(devotional.toDomain()))
        } else {
            emit(Result.Error(NoDevotionalException("No devotional found for today")))
        }
    }.catch { emit(Result.Error(it)) }

    override suspend fun getTodayDevotionalSync(): Result<Devotional> {
        return try {
            val today = dateFormat.format(Date())
            val devotional = devotionalDao.getDevotionalByDate(today)
            if (devotional != null) {
                Result.Success(devotional.toDomain())
            } else {
                Result.Error(NoDevotionalException("No devotional found for today"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getDevotionalByDate(date: String): Flow<Result<Devotional>> {
        return devotionalDao.observeDevotionalByDate(date)
            .map { entity ->
                if (entity != null) {
                    Result.Success(entity.toDomain()) as Result<Devotional>
                } else {
                    Result.Error(NoDevotionalException("No devotional found for date: $date"))
                }
            }
            .catch { emit(Result.Error(it)) }
    }

    override suspend fun getDevotionalByDateSync(date: String): Result<Devotional> {
        return try {
            val devotional = devotionalDao.getDevotionalByDate(date)
            if (devotional != null) {
                Result.Success(devotional.toDomain())
            } else {
                Result.Error(NoDevotionalException("No devotional found for date: $date"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getAllDevotionals(): Flow<List<Devotional>> {
        return devotionalDao.getAllDevotionals().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentDevotionals(limit: Int): Flow<List<Devotional>> {
        return devotionalDao.getRecentDevotionals(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun markAsRead(date: String) {
        devotionalDao.markAsRead(date, true)
    }

    override suspend fun getNextUnread(): Devotional? {
        return devotionalDao.getNextUnreadDevotional()?.toDomain()
    }

    override suspend fun getReadCount(): Int {
        return devotionalDao.getReadDevotionalsCount()
    }

    override suspend fun generateDevotional(date: String): Result<Devotional> {
        return try {
            val existingDevotional = devotionalDao.getDevotionalByDate(date)
            if (existingDevotional != null) {
                return Result.Success(existingDevotional.toDomain())
            }

            val devotional = DevotionalEntity(
                date = date,
                title = "Devocional del día",
                verseReference = "Salmos 119:105",
                verseText = "Lámpara es a mis pies tu palabra, y lumbrera a mi camino.",
                devotionalText = "La Palabra de Dios ilumina nuestro camino cada día. En momentos de incertidumbre, podemos confiar en que Sus enseñanzas nos guiarán hacia la verdad y la paz.",
                prayerText = "Señor, gracias por tu Palabra que ilumina mi vida. Ayúdame a caminar siempre en tu luz. Amén."
            )
            val id = devotionalDao.insertDevotional(devotional)
            Result.Success(devotional.copy(id = id).toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveDevotional(devotional: Devotional): Result<Long> {
        return try {
            val entity = DevotionalEntity(
                id = devotional.id,
                date = devotional.date,
                title = devotional.title,
                verseReference = devotional.verseReference,
                verseText = devotional.verseText,
                devotionalText = devotional.devotionalText,
                prayerText = devotional.prayerText,
                isRead = devotional.isRead,
                createdAt = devotional.createdAt
            )
            val id = devotionalDao.insertDevotional(entity)
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteOldDevotionals(keepDays: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -keepDays)
        val cutoffDate = dateFormat.format(calendar.time)
        devotionalDao.deleteOldDevotionals(cutoffDate)
    }

    override suspend fun clearAllDevotionals() {
        devotionalDao.clearAllDevotionals()
    }

    private fun DevotionalEntity.toDomain(): Devotional {
        return Devotional(
            id = id,
            date = date,
            title = title,
            verseReference = verseReference,
            verseText = verseText,
            devotionalText = devotionalText,
            prayerText = prayerText,
            isRead = isRead,
            createdAt = createdAt
        )
    }

    class NoDevotionalException(message: String) : Exception(message)
}
