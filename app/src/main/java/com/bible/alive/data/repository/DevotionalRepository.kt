package com.bible.alive.data.repository

import com.bible.alive.domain.model.Devotional
import com.bible.alive.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface DevotionalRepository {

    fun getTodayDevotional(): Flow<Result<Devotional>>

    suspend fun getTodayDevotionalSync(): Result<Devotional>

    fun getDevotionalByDate(date: String): Flow<Result<Devotional>>

    suspend fun getDevotionalByDateSync(date: String): Result<Devotional>

    fun getAllDevotionals(): Flow<List<Devotional>>

    fun getRecentDevotionals(limit: Int = 7): Flow<List<Devotional>>

    suspend fun markAsRead(date: String)

    suspend fun getNextUnread(): Devotional?

    suspend fun getReadCount(): Int

    suspend fun generateDevotional(date: String): Result<Devotional>

    suspend fun saveDevotional(devotional: Devotional): Result<Long>

    suspend fun deleteOldDevotionals(keepDays: Int = 30)

    suspend fun clearAllDevotionals()
}
