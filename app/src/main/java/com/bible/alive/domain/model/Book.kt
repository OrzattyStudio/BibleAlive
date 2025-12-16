package com.bible.alive.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val name: String,
    val chapters: Int,
    val testament: Testament
) : Parcelable {

    enum class Testament {
        OLD,
        NEW;

        companion object {
            fun fromString(value: String): Testament {
                return when (value.uppercase()) {
                    "OLD", "AT", "ANTIGUO" -> OLD
                    "NEW", "NT", "NUEVO" -> NEW
                    else -> OLD
                }
            }
        }
    }

    val isOldTestament: Boolean
        get() = testament == Testament.OLD

    val isNewTestament: Boolean
        get() = testament == Testament.NEW

    companion object {
        fun empty(): Book = Book(
            id = 0,
            translation = "",
            bookNumber = 0,
            name = "",
            chapters = 0,
            testament = Testament.OLD
        )
    }
}
