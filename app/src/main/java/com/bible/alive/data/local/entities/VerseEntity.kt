package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "verses",
    indices = [
        Index(value = ["translation", "bookNumber", "chapter", "verse"], unique = true),
        Index(value = ["translation"]),
        Index(value = ["bookNumber"])
    ]
)
data class VerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val cachedAt: Long = System.currentTimeMillis()
)
