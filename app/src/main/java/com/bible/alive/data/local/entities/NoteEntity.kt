package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    indices = [
        Index(value = ["translation", "bookNumber", "chapter", "verse"]),
        Index(value = ["createdAt"])
    ]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val translation: String,
    val bookNumber: Int,
    val bookName: String,
    val chapter: Int,
    val verse: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
