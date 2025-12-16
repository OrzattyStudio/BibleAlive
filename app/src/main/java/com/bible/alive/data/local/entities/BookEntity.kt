package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [
        Index(value = ["translation", "bookNumber"], unique = true),
        Index(value = ["translation"])
    ]
)
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapters: Int,
    val testament: String,
    val cachedAt: Long = System.currentTimeMillis()
)
