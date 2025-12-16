package com.bible.alive.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey
    val id: Int = 1,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastReadDate: Long = 0,
    val streakStartDate: Long = 0,
    val totalDaysRead: Int = 0
)
