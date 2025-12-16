package com.bible.alive.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TranslationDto(
    @SerializedName("short_name")
    val shortName: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("language_en")
    val languageEn: String? = null,
    @SerializedName("direction")
    val direction: String? = "ltr"
)
