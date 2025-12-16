package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "devotionals",
    indices = [
        Index(value = ["date"], unique = true)
    ]
)
data class DevotionalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val title: String,
    val verseReference: String,
    val verseText: String,
    val devotionalText: String,
    val prayerText: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
