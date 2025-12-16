package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Verse(
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val text: String
) : Parcelable {

    val reference: String
        get() = "$bookName $chapter:$verse"

    val fullReference: String
        get() = "$bookName $chapter:$verse ($translation)"

    companion object {
        fun empty(): Verse = Verse(
            id = 0,
            translation = "",
            bookNumber = 0,
            bookName = "",
            chapter = 0,
            verse = 0,
            text = ""
        )
    }
}
