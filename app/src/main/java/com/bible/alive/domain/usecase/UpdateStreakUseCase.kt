package com.bible.alive.domain.usecase

import com.bible.alive.data.repository.StreakRepository
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Streak
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UpdateStreakUseCase @Inject constructor(
    private val streakRepository: StreakRepository
) {

    val currentStreak: Flow<Streak> = streakRepository.currentStreak

    suspend operator fun invoke(): Result<Streak> {
        return try {
            val streak = streakRepository.updateStreak()
            Result.Success(streak)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun checkAndUpdate(): Result<Streak> {
        return try {
            val streak = streakRepository.checkAndUpdateStreak()
            Result.Success(streak)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getStreak(): Result<Streak> {
        return try {
            val streak = streakRepository.getStreak()
            Result.Success(streak)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun isStreakActive(): Boolean {
        return streakRepository.isStreakActive()
    }

    suspend fun hasReadToday(): Boolean {
        return streakRepository.hasReadToday()
    }

    suspend fun getStreakStatus(): StreakStatus {
        val streak = streakRepository.getStreak()
        return StreakStatus(
            currentStreak = streak.currentStreak,
            longestStreak = streak.longestStreak,
            isActive = streak.isActive,
            hasReadToday = streak.hasReadToday,
            daysToNextMilestone = streak.streakDaysUntilMilestone,
            nextMilestone = streak.nextMilestone,
            message = getStreakMessage(streak)
        )
    }

    private fun getStreakMessage(streak: Streak): String {
        return when {
            streak.currentStreak == 0 -> "¡Comienza tu racha de lectura hoy!"
            !streak.hasReadToday -> "¡Lee hoy para mantener tu racha de ${streak.currentStreak} días!"
            streak.currentStreak == streak.longestStreak && streak.currentStreak > 1 -> 
                "¡Nuevo récord! ${streak.currentStreak} días consecutivos"
            streak.currentStreak >= 7 && streak.currentStreak < 14 -> 
                "¡Una semana leyendo! Sigue así"
            streak.currentStreak >= 30 -> 
                "¡Increíble! ${streak.currentStreak} días de fidelidad"
            else -> "Racha actual: ${streak.currentStreak} días"
        }
    }

    data class StreakStatus(
        val currentStreak: Int,
        val longestStreak: Int,
        val isActive: Boolean,
        val hasReadToday: Boolean,
        val daysToNextMilestone: Int,
        val nextMilestone: Int,
        val message: String
    )
}
