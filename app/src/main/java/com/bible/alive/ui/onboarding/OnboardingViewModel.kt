package com.bible.alive.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.domain.model.AgeGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val selectedAgeGroup: AgeGroup? = null,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false
)

class OnboardingViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()
    
    fun selectAgeGroup(ageGroup: AgeGroup) {
        _uiState.update { it.copy(selectedAgeGroup = ageGroup) }
    }
    
    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            _uiState.update { it.copy(isLoading = false, isComplete = true) }
            onComplete()
        }
    }
}
