package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Devotional(
    val id: Long = 0,
    val date: String,
    val title: String,
    val verseReference: String,
    val verseText: String,
    val devotionalText: String,
    val prayerText: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {

    val hasBeenRead: Boolean
        get() = isRead

    val hasPrayer: Boolean
        get() = !prayerText.isNullOrBlank()

    val summary: String
        get() = devotionalText.take(150) + if (devotionalText.length > 150) "..." else ""

    companion object {
        fun empty(): Devotional = Devotional(
            id = 0,
            date = "",
            title = "",
            verseReference = "",
            verseText = "",
            devotionalText = "",
            prayerText = null,
            isRead = false
        )
    }
}
