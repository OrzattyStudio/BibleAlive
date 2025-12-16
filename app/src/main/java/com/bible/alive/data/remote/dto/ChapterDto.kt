package com.bible.alive.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChapterDto(
    @SerializedName("pk")
    val pk: Int? = null,
    @SerializedName("verse")
    val verse: Int,
    @SerializedName("text")
    val text: String
)
