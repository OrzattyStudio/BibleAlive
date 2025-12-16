package com.bible.alive.domain.model

enum class AgeGroup {
    YOUNG,
    ADULT,
    ELDERLY;

    companion object {
        fun fromString(value: String): AgeGroup {
            return when (value.uppercase()) {
                "YOUNG" -> YOUNG
                "ADULT" -> ADULT
                "ELDERLY" -> ELDERLY
                else -> ADULT
            }
        }
    }
}
