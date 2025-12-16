package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_reading_history",
    indices = [
        Index(value = ["translation", "bookNumber", "chapter"]),
        Index(value = ["readAt"])
    ]
)
data class UserReadingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int? = null,
    val readAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0
)
