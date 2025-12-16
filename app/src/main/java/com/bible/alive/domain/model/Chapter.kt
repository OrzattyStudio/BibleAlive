package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chapter(
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapterNumber: Int,
    val verses: List<Verse>
) : Parcelable {

    val verseCount: Int
        get() = verses.size

    val reference: String
        get() = "$bookName $chapterNumber"

    val fullReference: String
        get() = "$bookName $chapterNumber ($translation)"

    val isEmpty: Boolean
        get() = verses.isEmpty()

    fun getVerse(verseNumber: Int): Verse? {
        return verses.find { it.verse == verseNumber }
    }

    fun getVerseRange(start: Int, end: Int): List<Verse> {
        return verses.filter { it.verse in start..end }
    }

    companion object {
        fun empty(): Chapter = Chapter(
            translation = "",
            bookNumber = 0,
            bookName = "",
            chapterNumber = 0,
            verses = emptyList()
        )
    }
}
