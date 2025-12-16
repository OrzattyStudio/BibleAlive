package com.bible.alive.data.repository

import com.bible.alive.data.local.dao.StreakDao
import com.bible.alive.data.local.entities.StreakEntity
import com.bible.alive.domain.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreakRepositoryImpl @Inject constructor(
    private val streakDao: StreakDao
) : StreakRepository {

    override val currentStreak: Flow<Streak> = streakDao.observeStreak().map { entity ->
        entity?.toDomain() ?: Streak.empty()
    }

    override suspend fun getStreak(): Streak {
        return streakDao.getStreak()?.toDomain() ?: Streak.empty()
    }

    override suspend fun updateStreak(): Streak {
        val current = streakDao.getStreak() ?: StreakEntity()
        val now = System.currentTimeMillis()
        val today = getStartOfDay(now)
        val lastReadDay = if (current.lastReadDate > 0) getStartOfDay(current.lastReadDate) else 0L

        val updatedStreak = when {
            lastReadDay == today -> {
                current
            }
            lastReadDay == today - TimeUnit.DAYS.toMillis(1) -> {
                current.copy(
                    currentStreak = current.currentStreak + 1,
                    lastReadDate = now,
                    longestStreak = maxOf(current.longestStreak, current.currentStreak + 1),
                    totalDaysRead = current.totalDaysRead + 1
                )
            }
            else -> {
                current.copy(
                    currentStreak = 1,
                    lastReadDate = now,
                    streakStartDate = now,
                    totalDaysRead = current.totalDaysRead + 1
                )
            }
        }

        streakDao.insertStreak(updatedStreak)
        return updatedStreak.toDomain()
    }

    override suspend fun checkAndUpdateStreak(): Streak {
        val current = streakDao.getStreak() ?: return initializeAndGetStreak()
        val now = System.currentTimeMillis()
        val lastReadDay = if (current.lastReadDate > 0) getStartOfDay(current.lastReadDate) else 0L
        val today = getStartOfDay(now)

        if (lastReadDay > 0 && lastReadDay < today - TimeUnit.DAYS.toMillis(1)) {
            val resetStreak = current.copy(currentStreak = 0)
            streakDao.insertStreak(resetStreak)
            return resetStreak.toDomain()
        }

        return current.toDomain()
    }

    override suspend fun resetStreak() {
        streakDao.resetCurrentStreak()
    }

    override suspend fun recordReading() {
        updateStreak()
    }

    override suspend fun isStreakActive(): Boolean {
        val streak = getStreak()
        return streak.isActive
    }

    override suspend fun hasReadToday(): Boolean {
        val streak = getStreak()
        return streak.hasReadToday
    }

    override suspend fun getStreakHistory(): List<Long> {
        val streak = getStreak()
        if (streak.streakStartDate == 0L) return emptyList()

        val days = mutableListOf<Long>()
        var currentDay = streak.streakStartDate
        val today = getStartOfDay(System.currentTimeMillis())

        while (currentDay <= today && days.size <= streak.currentStreak) {
            days.add(currentDay)
            currentDay += TimeUnit.DAYS.toMillis(1)
        }

        return days
    }

    override suspend fun initializeStreakIfNeeded() {
        if (streakDao.getStreak() == null) {
            streakDao.insertStreak(StreakEntity())
        }
    }

    private suspend fun initializeAndGetStreak(): Streak {
        val newStreak = StreakEntity()
        streakDao.insertStreak(newStreak)
        return newStreak.toDomain()
    }

    private fun getStartOfDay(timestamp: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun StreakEntity.toDomain(): Streak {
        return Streak(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            lastReadDate = lastReadDate,
            streakStartDate = streakStartDate,
            totalDaysRead = totalDaysRead
        )
    }
}
