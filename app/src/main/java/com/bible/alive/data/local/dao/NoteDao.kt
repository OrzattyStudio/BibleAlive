package com.bible.alive.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bible.alive.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC LIMIT :limit")
    fun getRecentNotes(limit: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter AND verse = :verse")
    fun getNotesForVerse(translation: String, bookNumber: Int, chapter: Int, verse: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE translation = :translation AND bookNumber = :bookNumber AND chapter = :chapter")
    fun getNotesForChapter(translation: String, bookNumber: Int, chapter: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE noteText LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchNotes(query: String): Flow<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNotesCount(): Int

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()
}
