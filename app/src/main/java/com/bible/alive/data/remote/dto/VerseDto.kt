package com.bible.alive.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VerseDto(
    @SerializedName("pk")
    val pk: Int? = null,
    @SerializedName("translation")
    val translation: String? = null,
    @SerializedName("book")
    val book: Int,
    @SerializedName("bookname")
    val bookName: String? = null,
    @SerializedName("chapter")
    val chapter: Int,
    @SerializedName("verse")
    val verse: Int,
    @SerializedName("text")
    val text: String
)
