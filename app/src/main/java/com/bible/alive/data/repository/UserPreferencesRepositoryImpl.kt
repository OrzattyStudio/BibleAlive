package com.bible.alive.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bible.alive.domain.model.AgeGroup
import com.bible.alive.domain.model.Translation
import com.bible.alive.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val AGE_GROUP = stringPreferencesKey("age_group")
        val PREFERRED_TRANSLATION = stringPreferencesKey("preferred_translation")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DAILY_REMINDER_TIME = stringPreferencesKey("daily_reminder_time")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val CREATED_AT = longPreferencesKey("created_at")
    }

    override val userProfile: Flow<UserProfile> = dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[PreferencesKeys.USER_NAME] ?: "",
            ageGroup = AgeGroup.fromString(preferences[PreferencesKeys.AGE_GROUP] ?: "ADULT"),
            preferredTranslation = preferences[PreferencesKeys.PREFERRED_TRANSLATION] ?: Translation.RV1960.shortName,
            notificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            dailyReminderTime = preferences[PreferencesKeys.DAILY_REMINDER_TIME] ?: "08:00",
            hasCompletedOnboarding = preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false,
            createdAt = preferences[PreferencesKeys.CREATED_AT] ?: System.currentTimeMillis()
        )
    }

    override val preferredTranslation: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PREFERRED_TRANSLATION] ?: Translation.RV1960.shortName
    }

    override val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
    }

    override val dailyReminderTime: Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DAILY_REMINDER_TIME] ?: "08:00"
    }

    override val hasCompletedOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = profile.name
            preferences[PreferencesKeys.AGE_GROUP] = profile.ageGroup.name
            preferences[PreferencesKeys.PREFERRED_TRANSLATION] = profile.preferredTranslation
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = profile.notificationsEnabled
            preferences[PreferencesKeys.DAILY_REMINDER_TIME] = profile.dailyReminderTime
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = profile.hasCompletedOnboarding
        }
    }

    override suspend fun updateName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    override suspend fun updateAgeGroup(ageGroup: AgeGroup) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.AGE_GROUP] = ageGroup.name
        }
    }

    override suspend fun updatePreferredTranslation(translation: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREFERRED_TRANSLATION] = translation
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override suspend fun updateDailyReminderTime(time: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_TIME] = time
        }
    }

    override suspend fun markOnboardingComplete() {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
            if (preferences[PreferencesKeys.CREATED_AT] == null) {
                preferences[PreferencesKeys.CREATED_AT] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun clearPreferences() {
        dataStore.edit { it.clear() }
    }

    override suspend fun getUserProfileSync(): UserProfile {
        return userProfile.first()
    }
}
