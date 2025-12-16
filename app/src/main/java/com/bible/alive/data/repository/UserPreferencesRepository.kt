package com.bible.alive.data.repository

import com.bible.alive.domain.model.AgeGroup
import com.bible.alive.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val userProfile: Flow<UserProfile>

    val preferredTranslation: Flow<String>

    val notificationsEnabled: Flow<Boolean>

    val dailyReminderTime: Flow<String>

    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun updateUserProfile(profile: UserProfile)

    suspend fun updateName(name: String)

    suspend fun updateAgeGroup(ageGroup: AgeGroup)

    suspend fun updatePreferredTranslation(translation: String)

    suspend fun updateNotificationsEnabled(enabled: Boolean)

    suspend fun updateDailyReminderTime(time: String)

    suspend fun markOnboardingComplete()

    suspend fun clearPreferences()

    suspend fun getUserProfileSync(): UserProfile
}
