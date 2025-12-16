package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadingProgress(
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int? = null,
    val readAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
) : Parcelable {

    val reference: String
        get() = if (verse != null) {
            "$bookName $chapter:$verse"
        } else {
            "$bookName $chapter"
        }

    val fullReference: String
        get() = "${reference} ($translation)"

    val hasVerseProgress: Boolean
        get() = verse != null

    companion object {
        fun empty(): ReadingProgress = ReadingProgress(
            id = 0,
            translation = "",
            bookNumber = 0,
            bookName = "",
            chapter = 0,
            verse = null,
            readAt = 0,
            durationSeconds = 0
        )

        fun fromLastReading(
            translation: String,
            bookNumber: Int,
            bookName: String,
            chapter: Int,
            verse: Int? = null
        ): ReadingProgress {
            return ReadingProgress(
                translation = translation,
                bookNumber = bookNumber,
                bookName = bookName,
                chapter = chapter,
                verse = verse,
                readAt = System.currentTimeMillis()
            )
        }
    }
}
