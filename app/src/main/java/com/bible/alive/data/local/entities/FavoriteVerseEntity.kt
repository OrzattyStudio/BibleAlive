package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_verses",
    indices = [
        Index(value = ["translation", "bookNumber", "chapter", "verse"], unique = true)
    ]
)
data class FavoriteVerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val text: String,
    val addedAt: Long = System.currentTimeMillis()
)
