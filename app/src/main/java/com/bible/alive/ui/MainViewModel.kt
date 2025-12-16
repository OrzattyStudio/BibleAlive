package com.bible.alive.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isDarkMode: Boolean = false,
    val isFirstLaunch: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            userPreferencesRepository.userPreferencesFlow.collect { preferences ->
                _uiState.update {
                    it.copy(
                        isDarkMode = preferences.isDarkMode,
                        isFirstLaunch = preferences.isFirstLaunch,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            userPreferencesRepository.updateDarkMode(!_uiState.value.isDarkMode)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setFirstLaunch(false)
        }
    }
}
