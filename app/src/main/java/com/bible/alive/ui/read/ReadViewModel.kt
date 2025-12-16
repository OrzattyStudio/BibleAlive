package com.bible.alive.ui.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.domain.model.Book
import com.bible.alive.domain.model.Chapter
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReadUiState(
    val isLoading: Boolean = true,
    val books: List<Book> = emptyList(),
    val selectedBook: Book? = null,
    val currentChapter: Chapter? = null,
    val fontSize: Int = 16,
    val highlightedVerses: Set<Int> = emptySet(),
    val notedVerses: Set<Int> = emptySet(),
    val selectedVerse: Verse? = null,
    val showVerseOptions: Boolean = false,
    val isSpeaking: Boolean = false,
    val error: String? = null
)

sealed class VerseAction {
    object ReadAloud : VerseAction()
    object Summary : VerseAction()
    object Meaning : VerseAction()
    object Share : VerseAction()
    object Highlight : VerseAction()
    object AddNote : VerseAction()
}

class ReadViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ReadUiState())
    val uiState: StateFlow<ReadUiState> = _uiState.asStateFlow()
    
    init {
        loadBooks()
    }
    
    fun loadBooks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val oldTestamentBooks = listOf(
                    Book(1, "RVR1960", 1, "Génesis", 50, Book.Testament.OLD),
                    Book(2, "RVR1960", 2, "Éxodo", 40, Book.Testament.OLD),
                    Book(3, "RVR1960", 3, "Levítico", 27, Book.Testament.OLD),
                    Book(4, "RVR1960", 4, "Números", 36, Book.Testament.OLD),
                    Book(5, "RVR1960", 5, "Deuteronomio", 34, Book.Testament.OLD),
                    Book(6, "RVR1960", 6, "Josué", 24, Book.Testament.OLD),
                    Book(7, "RVR1960", 7, "Jueces", 21, Book.Testament.OLD),
                    Book(8, "RVR1960", 8, "Rut", 4, Book.Testament.OLD),
                    Book(9, "RVR1960", 9, "1 Samuel", 31, Book.Testament.OLD),
                    Book(10, "RVR1960", 10, "2 Samuel", 24, Book.Testament.OLD),
                    Book(11, "RVR1960", 11, "1 Reyes", 22, Book.Testament.OLD),
                    Book(12, "RVR1960", 12, "2 Reyes", 25, Book.Testament.OLD),
                    Book(13, "RVR1960", 13, "1 Crónicas", 29, Book.Testament.OLD),
                    Book(14, "RVR1960", 14, "2 Crónicas", 36, Book.Testament.OLD),
                    Book(15, "RVR1960", 15, "Esdras", 10, Book.Testament.OLD),
                    Book(16, "RVR1960", 16, "Nehemías", 13, Book.Testament.OLD),
                    Book(17, "RVR1960", 17, "Ester", 10, Book.Testament.OLD),
                    Book(18, "RVR1960", 18, "Job", 42, Book.Testament.OLD),
                    Book(19, "RVR1960", 19, "Salmos", 150, Book.Testament.OLD),
                    Book(20, "RVR1960", 20, "Proverbios", 31, Book.Testament.OLD),
                    Book(21, "RVR1960", 21, "Eclesiastés", 12, Book.Testament.OLD),
                    Book(22, "RVR1960", 22, "Cantares", 8, Book.Testament.OLD),
                    Book(23, "RVR1960", 23, "Isaías", 66, Book.Testament.OLD),
                    Book(24, "RVR1960", 24, "Jeremías", 52, Book.Testament.OLD),
                    Book(25, "RVR1960", 25, "Lamentaciones", 5, Book.Testament.OLD),
                    Book(26, "RVR1960", 26, "Ezequiel", 48, Book.Testament.OLD),
                    Book(27, "RVR1960", 27, "Daniel", 12, Book.Testament.OLD),
                    Book(28, "RVR1960", 28, "Oseas", 14, Book.Testament.OLD),
                    Book(29, "RVR1960", 29, "Joel", 3, Book.Testament.OLD),
                    Book(30, "RVR1960", 30, "Amós", 9, Book.Testament.OLD),
                    Book(31, "RVR1960", 31, "Abdías", 1, Book.Testament.OLD),
                    Book(32, "RVR1960", 32, "Jonás", 4, Book.Testament.OLD),
                    Book(33, "RVR1960", 33, "Miqueas", 7, Book.Testament.OLD),
                    Book(34, "RVR1960", 34, "Nahum", 3, Book.Testament.OLD),
                    Book(35, "RVR1960", 35, "Habacuc", 3, Book.Testament.OLD),
                    Book(36, "RVR1960", 36, "Sofonías", 3, Book.Testament.OLD),
                    Book(37, "RVR1960", 37, "Hageo", 2, Book.Testament.OLD),
                    Book(38, "RVR1960", 38, "Zacarías", 14, Book.Testament.OLD),
                    Book(39, "RVR1960", 39, "Malaquías", 4, Book.Testament.OLD)
                )
                
                val newTestamentBooks = listOf(
                    Book(40, "RVR1960", 40, "Mateo", 28, Book.Testament.NEW),
                    Book(41, "RVR1960", 41, "Marcos", 16, Book.Testament.NEW),
                    Book(42, "RVR1960", 42, "Lucas", 24, Book.Testament.NEW),
                    Book(43, "RVR1960", 43, "Juan", 21, Book.Testament.NEW),
                    Book(44, "RVR1960", 44, "Hechos", 28, Book.Testament.NEW),
                    Book(45, "RVR1960", 45, "Romanos", 16, Book.Testament.NEW),
                    Book(46, "RVR1960", 46, "1 Corintios", 16, Book.Testament.NEW),
                    Book(47, "RVR1960", 47, "2 Corintios", 13, Book.Testament.NEW),
                    Book(48, "RVR1960", 48, "Gálatas", 6, Book.Testament.NEW),
                    Book(49, "RVR1960", 49, "Efesios", 6, Book.Testament.NEW),
                    Book(50, "RVR1960", 50, "Filipenses", 4, Book.Testament.NEW),
                    Book(51, "RVR1960", 51, "Colosenses", 4, Book.Testament.NEW),
                    Book(52, "RVR1960", 52, "1 Tesalonicenses", 5, Book.Testament.NEW),
                    Book(53, "RVR1960", 53, "2 Tesalonicenses", 3, Book.Testament.NEW),
                    Book(54, "RVR1960", 54, "1 Timoteo", 6, Book.Testament.NEW),
                    Book(55, "RVR1960", 55, "2 Timoteo", 4, Book.Testament.NEW),
                    Book(56, "RVR1960", 56, "Tito", 3, Book.Testament.NEW),
                    Book(57, "RVR1960", 57, "Filemón", 1, Book.Testament.NEW),
                    Book(58, "RVR1960", 58, "Hebreos", 13, Book.Testament.NEW),
                    Book(59, "RVR1960", 59, "Santiago", 5, Book.Testament.NEW),
                    Book(60, "RVR1960", 60, "1 Pedro", 5, Book.Testament.NEW),
                    Book(61, "RVR1960", 61, "2 Pedro", 3, Book.Testament.NEW),
                    Book(62, "RVR1960", 62, "1 Juan", 5, Book.Testament.NEW),
                    Book(63, "RVR1960", 63, "2 Juan", 1, Book.Testament.NEW),
                    Book(64, "RVR1960", 64, "3 Juan", 1, Book.Testament.NEW),
                    Book(65, "RVR1960", 65, "Judas", 1, Book.Testament.NEW),
                    Book(66, "RVR1960", 66, "Apocalipsis", 22, Book.Testament.NEW)
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        books = oldTestamentBooks + newTestamentBooks
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error desconocido"
                    )
                }
            }
        }
    }
    
    fun selectBook(book: Book) {
        _uiState.update { it.copy(selectedBook = book) }
    }
    
    fun loadChapter(bookId: Int, chapterNumber: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val book = _uiState.value.books.find { it.bookNumber == bookId }
                
                val sampleVerses = (1..20).map { verseNum ->
                    Verse(
                        id = verseNum.toLong(),
                        translation = "RVR1960",
                        bookNumber = bookId,
                        bookName = book?.name ?: "Libro",
                        chapter = chapterNumber,
                        verse = verseNum,
                        text = "Este es el versículo $verseNum del capítulo $chapterNumber. Lorem ipsum dolor sit amet, consectetur adipiscing elit."
                    )
                }
                
                val chapter = Chapter(
                    translation = "RVR1960",
                    bookNumber = bookId,
                    bookName = book?.name ?: "Libro",
                    chapterNumber = chapterNumber,
                    verses = sampleVerses
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentChapter = chapter,
                        selectedBook = book
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    fun onVerseSelected(verse: Verse) {
        _uiState.update { 
            it.copy(
                selectedVerse = verse,
                showVerseOptions = true
            )
        }
    }
    
    fun dismissVerseOptions() {
        _uiState.update {
            it.copy(
                showVerseOptions = false,
                selectedVerse = null
            )
        }
    }
    
    fun onVerseAction(action: VerseAction) {
        val verse = _uiState.value.selectedVerse ?: return
        
        when (action) {
            VerseAction.Highlight -> {
                val currentHighlights = _uiState.value.highlightedVerses.toMutableSet()
                if (verse.verse in currentHighlights) {
                    currentHighlights.remove(verse.verse)
                } else {
                    currentHighlights.add(verse.verse)
                }
                _uiState.update { it.copy(highlightedVerses = currentHighlights) }
            }
            VerseAction.AddNote -> {
                val currentNotes = _uiState.value.notedVerses.toMutableSet()
                currentNotes.add(verse.verse)
                _uiState.update { it.copy(notedVerses = currentNotes) }
            }
            VerseAction.ReadAloud -> {
                _uiState.update { it.copy(isSpeaking = !it.isSpeaking) }
            }
            else -> {}
        }
        
        dismissVerseOptions()
    }
    
    fun setFontSize(size: Int) {
        _uiState.update { it.copy(fontSize = size.coerceIn(12, 24)) }
    }
    
    fun toggleSpeaking() {
        _uiState.update { it.copy(isSpeaking = !it.isSpeaking) }
    }
}
