package com.bible.alive.data.repository

import com.bible.alive.domain.model.Streak
import kotlinx.coroutines.flow.Flow

interface StreakRepository {

    val currentStreak: Flow<Streak>

    suspend fun getStreak(): Streak

    suspend fun updateStreak(): Streak

    suspend fun checkAndUpdateStreak(): Streak

    suspend fun resetStreak()

    suspend fun recordReading()

    suspend fun isStreakActive(): Boolean

    suspend fun hasReadToday(): Boolean

    suspend fun getStreakHistory(): List<Long>

    suspend fun initializeStreakIfNeeded()
}
