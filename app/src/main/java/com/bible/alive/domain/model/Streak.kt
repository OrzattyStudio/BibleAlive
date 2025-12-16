package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Parcelize
data class Streak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastReadDate: Long = 0,
    val streakStartDate: Long = 0,
    val totalDaysRead: Int = 0
) : Parcelable {

    val isActive: Boolean
        get() = currentStreak > 0 && !isStreakBroken

    val isStreakBroken: Boolean
        get() {
            if (lastReadDate == 0L) return true
            val now = System.currentTimeMillis()
            val daysSinceLastRead = TimeUnit.MILLISECONDS.toDays(now - lastReadDate)
            return daysSinceLastRead > 1
        }

    val hasReadToday: Boolean
        get() {
            if (lastReadDate == 0L) return false
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            return lastReadDate >= today
        }

    val streakDaysUntilMilestone: Int
        get() {
            val milestones = listOf(7, 14, 21, 30, 60, 90, 100, 180, 365)
            return milestones.firstOrNull { it > currentStreak }?.minus(currentStreak) ?: 0
        }

    val nextMilestone: Int
        get() {
            val milestones = listOf(7, 14, 21, 30, 60, 90, 100, 180, 365)
            return milestones.firstOrNull { it > currentStreak } ?: currentStreak
        }

    companion object {
        fun empty(): Streak = Streak(
            currentStreak = 0,
            longestStreak = 0,
            lastReadDate = 0,
            streakStartDate = 0,
            totalDaysRead = 0
        )
    }
}
