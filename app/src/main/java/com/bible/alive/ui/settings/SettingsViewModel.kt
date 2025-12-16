package com.bible.alive.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.domain.model.Translation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val fontSize: Int = 16,
    val selectedTranslation: Translation = Translation.RV1960,
    val availableTranslations: List<Translation> = Translation.supportedTranslations,
    val isDailyVerseEnabled: Boolean = true,
    val isStreakReminderEnabled: Boolean = true,
    val isReadingReminderEnabled: Boolean = true,
    val appVersion: String = "1.0.0"
)

class SettingsViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    fun setDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(isDarkMode = enabled) }
    }
    
    fun setFontSize(size: Int) {
        _uiState.update { it.copy(fontSize = size.coerceIn(12, 24)) }
    }
    
    fun setTranslation(translation: Translation) {
        _uiState.update { it.copy(selectedTranslation = translation) }
    }
    
    fun setDailyVerseEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isDailyVerseEnabled = enabled) }
    }
    
    fun setStreakReminderEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isStreakReminderEnabled = enabled) }
    }
    
    fun setReadingReminderEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isReadingReminderEnabled = enabled) }
    }
}
