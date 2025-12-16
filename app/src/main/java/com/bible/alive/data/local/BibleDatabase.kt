package com.bible.alive.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bible.alive.data.local.dao.BookDao
import com.bible.alive.data.local.dao.DevotionalDao
import com.bible.alive.data.local.dao.FavoriteDao
import com.bible.alive.data.local.dao.HighlightDao
import com.bible.alive.data.local.dao.NoteDao
import com.bible.alive.data.local.dao.StreakDao
import com.bible.alive.data.local.dao.UserReadingDao
import com.bible.alive.data.local.dao.VerseDao
import com.bible.alive.data.local.entities.BookEntity
import com.bible.alive.data.local.entities.DevotionalEntity
import com.bible.alive.data.local.entities.FavoriteVerseEntity
import com.bible.alive.data.local.entities.HighlightEntity
import com.bible.alive.data.local.entities.NoteEntity
import com.bible.alive.data.local.entities.StreakEntity
import com.bible.alive.data.local.entities.UserReadingHistoryEntity
import com.bible.alive.data.local.entities.VerseEntity

@Database(
    entities = [
        VerseEntity::class,
        BookEntity::class,
        UserReadingHistoryEntity::class,
        StreakEntity::class,
        FavoriteVerseEntity::class,
        NoteEntity::class,
        HighlightEntity::class,
        DevotionalEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BibleDatabase : RoomDatabase() {

    abstract fun verseDao(): VerseDao
    abstract fun bookDao(): BookDao
    abstract fun userReadingDao(): UserReadingDao
    abstract fun streakDao(): StreakDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun noteDao(): NoteDao
    abstract fun highlightDao(): HighlightDao
    abstract fun devotionalDao(): DevotionalDao

    companion object {
        const val DATABASE_NAME = "bible_alive_database"
    }
}
