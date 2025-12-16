package com.bible.alive.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bible.alive.data.local.dao.UserReadingDao
import com.bible.alive.data.repository.DevotionalRepository
import com.bible.alive.data.repository.StreakRepository
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.domain.model.Devotional
import com.bible.alive.domain.model.ReadingProgress
import com.bible.alive.domain.model.Result
import com.bible.alive.domain.model.Streak
import com.bible.alive.domain.model.Verse
import com.bible.alive.domain.usecase.GetRecommendedVersesUseCase
import com.bible.alive.domain.usecase.GetVerseOfDayUseCase
import com.bible.alive.domain.usecase.UpdateStreakUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val verseOfDay: Verse = Verse.empty(),
    val streak: Streak = Streak.empty(),
    val points: Int = 0,
    val dailyDevotional: Devotional = Devotional.empty(),
    val lastReadProgress: ReadingProgress? = null,
    val recommendedVerses: List<Verse> = emptyList(),
    val greeting: String = "¡Hola!",
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getVerseOfDayUseCase: GetVerseOfDayUseCase,
    private val getRecommendedVersesUseCase: GetRecommendedVersesUseCase,
    private val updateStreakUseCase: UpdateStreakUseCase,
    private val devotionalRepository: DevotionalRepository,
    private val streakRepository: StreakRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val userReadingDao: UserReadingDao
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        checkAndUpdateStreak()
        loadHomeData()
    }
    
    private fun checkAndUpdateStreak() {
        viewModelScope.launch {
            updateStreakUseCase.checkAndUpdate()
        }
    }
    
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val verseDeferred = async { getVerseOfDayUseCase() }
                val streakDeferred = async { streakRepository.getStreak() }
                val devotionalDeferred = async { devotionalRepository.getTodayDevotionalSync() }
                val recommendedDeferred = async { getRecommendedVersesUseCase(5) }
                val lastReadingDeferred = async { userReadingDao.getLastReading() }
                val userProfileDeferred = async { userPreferencesRepository.getUserProfileSync() }
                
                val verseResult = verseDeferred.await()
                val streak = streakDeferred.await()
                val devotionalResult = devotionalDeferred.await()
                val recommendedResult = recommendedDeferred.await()
                val lastReading = lastReadingDeferred.await()
                val userProfile = userProfileDeferred.await()
                
                val verse = when (verseResult) {
                    is Result.Success -> verseResult.data
                    is Result.Error -> Verse.empty()
                    is Result.Loading -> Verse.empty()
                }
                
                val devotional = when (devotionalResult) {
                    is Result.Success -> devotionalResult.data
                    is Result.Error -> {
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val generated = devotionalRepository.generateDevotional(today)
                        when (generated) {
                            is Result.Success -> generated.data
                            else -> Devotional.empty()
                        }
                    }
                    is Result.Loading -> Devotional.empty()
                }
                
                val recommendedVerses = when (recommendedResult) {
                    is Result.Success -> recommendedResult.data.map { it.verse }
                    is Result.Error -> emptyList()
                    is Result.Loading -> emptyList()
                }
                
                val lastReadProgress = lastReading?.let {
                    ReadingProgress(
                        id = it.id,
                        translation = it.translation,
                        bookNumber = it.bookNumber,
                        bookName = it.bookName,
                        chapter = it.chapter,
                        verse = it.verse,
                        readAt = it.readAt,
                        durationSeconds = it.durationSeconds
                    )
                }
                
                val greeting = getGreeting(userProfile.name)
                val points = calculatePoints(streak.totalDaysRead)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        verseOfDay = verse,
                        streak = streak,
                        points = points,
                        dailyDevotional = devotional,
                        lastReadProgress = lastReadProgress,
                        recommendedVerses = recommendedVerses,
                        greeting = greeting,
                        error = null
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
    
    private fun getGreeting(userName: String): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val timeGreeting = when {
            hour < 12 -> "¡Buenos días"
            hour < 18 -> "¡Buenas tardes"
            else -> "¡Buenas noches"
        }
        
        return if (userName.isNotBlank()) {
            "$timeGreeting, $userName!"
        } else {
            "$timeGreeting!"
        }
    }
    
    private fun calculatePoints(totalDaysRead: Int): Int {
        return totalDaysRead * 50
    }
    
    fun refreshData() {
        checkAndUpdateStreak()
        loadHomeData()
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
