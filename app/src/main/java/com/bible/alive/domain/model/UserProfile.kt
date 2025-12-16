package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val name: String = "",
    val ageGroup: AgeGroup = AgeGroup.ADULT,
    val preferredTranslation: String = Translation.RV1960.shortName,
    val notificationsEnabled: Boolean = true,
    val dailyReminderTime: String = "08:00",
    val hasCompletedOnboarding: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {

    val hasName: Boolean
        get() = name.isNotBlank()

    val displayName: String
        get() = name.ifBlank { "Usuario" }

    val greeting: String
        get() = when (ageGroup) {
            AgeGroup.YOUNG -> "¡Hola, $displayName!"
            AgeGroup.ADULT -> "Bienvenido, $displayName"
            AgeGroup.ELDERLY -> "Bendiciones, $displayName"
        }

    companion object {
        fun default(): UserProfile = UserProfile()

        fun withAgeGroup(ageGroup: AgeGroup): UserProfile = UserProfile(ageGroup = ageGroup)
    }
}
