package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Translation(
    val shortName: String,
    val fullName: String,
    val language: String,
    val languageEnglish: String? = null,
    val direction: TextDirection = TextDirection.LTR
) : Parcelable {

    enum class TextDirection {
        LTR,
        RTL;

        companion object {
            fun fromString(value: String?): TextDirection {
                return when (value?.uppercase()) {
                    "RTL" -> RTL
                    else -> LTR
                }
            }
        }
    }

    val isRightToLeft: Boolean
        get() = direction == TextDirection.RTL

    companion object {
        val RV1960 = Translation(
            shortName = "RVR1960",
            fullName = "Reina Valera 1960",
            language = "Español",
            languageEnglish = "Spanish"
        )

        val NTV = Translation(
            shortName = "NTV",
            fullName = "Nueva Traducción Viviente",
            language = "Español",
            languageEnglish = "Spanish"
        )

        val NVI = Translation(
            shortName = "NVI",
            fullName = "Nueva Versión Internacional",
            language = "Español",
            languageEnglish = "Spanish"
        )

        val supportedTranslations = listOf(RV1960, NTV, NVI)

        fun fromShortName(shortName: String): Translation? {
            return supportedTranslations.find { it.shortName == shortName }
        }

        fun default(): Translation = RV1960
    }
}
