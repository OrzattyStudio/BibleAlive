package com.bible.alive.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.domain.model.Streak
import com.bible.alive.domain.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val versesRead: Int = 0,
    val chaptersCompleted: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalPoints: Int = 0,
    val selectedTab: ProfileTab = ProfileTab.FAVORITES,
    val favorites: List<Verse> = emptyList(),
    val notes: List<NoteItem> = emptyList(),
    val highlights: List<Verse> = emptyList(),
    val error: String? = null
)

enum class ProfileTab {
    FAVORITES,
    NOTES,
    HIGHLIGHTS
}

data class NoteItem(
    val id: Long,
    val verse: Verse,
    val noteText: String,
    val createdAt: Long
)

class ProfileViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfileData()
    }
    
    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val sampleFavorites = listOf(
                    Verse(1, "RVR1960", 43, "Juan", 3, 16, "Porque de tal manera amó Dios al mundo, que ha dado a su Hijo unigénito..."),
                    Verse(2, "RVR1960", 19, "Salmos", 23, 1, "Jehová es mi pastor; nada me faltará."),
                    Verse(3, "RVR1960", 50, "Filipenses", 4, 13, "Todo lo puedo en Cristo que me fortalece.")
                )
                
                val sampleNotes = listOf(
                    NoteItem(
                        id = 1,
                        verse = Verse(1, "RVR1960", 43, "Juan", 3, 16, "Porque de tal manera amó Dios al mundo..."),
                        noteText = "Este versículo me recuerda el amor incondicional de Dios.",
                        createdAt = System.currentTimeMillis() - 86400000
                    ),
                    NoteItem(
                        id = 2,
                        verse = Verse(2, "RVR1960", 45, "Romanos", 8, 28, "Y sabemos que a los que aman a Dios..."),
                        noteText = "Confiar en que todo obra para bien.",
                        createdAt = System.currentTimeMillis() - 172800000
                    )
                )
                
                val sampleHighlights = listOf(
                    Verse(1, "RVR1960", 1, "Génesis", 1, 1, "En el principio creó Dios los cielos y la tierra."),
                    Verse(2, "RVR1960", 23, "Isaías", 40, 31, "pero los que esperan a Jehová tendrán nuevas fuerzas...")
                )
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        versesRead = 342,
                        chaptersCompleted = 28,
                        currentStreak = 7,
                        bestStreak = 14,
                        totalPoints = 1250,
                        favorites = sampleFavorites,
                        notes = sampleNotes,
                        highlights = sampleHighlights
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
    
    fun selectTab(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
