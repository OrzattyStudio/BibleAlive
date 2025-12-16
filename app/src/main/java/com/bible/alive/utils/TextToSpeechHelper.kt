package com.bible.alive.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.bible.alive.domain.model.AgeGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextToSpeechHelper @Inject constructor() {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var currentSpeechRate = Constants.TextToSpeech.DEFAULT_SPEECH_RATE
    private var currentPitch = Constants.TextToSpeech.DEFAULT_PITCH

    fun initialize(context: Context, onReady: (() -> Unit)? = null) {
        if (isInitialized) {
            onReady?.invoke()
            return
        }

        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("es", "ES"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.getDefault())
                }
                isInitialized = true
                _isReady.value = true
                setupListener()
                onReady?.invoke()
            }
        }
    }

    private fun setupListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
            }
        })
    }

    fun setSpeechRateForAgeGroup(ageGroup: AgeGroup) {
        currentSpeechRate = when (ageGroup) {
            AgeGroup.ELDERLY -> Constants.TextToSpeech.ELDERLY_SPEECH_RATE
            AgeGroup.YOUNG -> Constants.TextToSpeech.YOUNG_SPEECH_RATE
            AgeGroup.ADULT -> Constants.TextToSpeech.DEFAULT_SPEECH_RATE
        }
        textToSpeech?.setSpeechRate(currentSpeechRate)
    }

    fun setSpeechRate(rate: Float) {
        currentSpeechRate = rate.coerceIn(0.5f, 2.0f)
        textToSpeech?.setSpeechRate(currentSpeechRate)
    }

    fun setPitch(pitch: Float) {
        currentPitch = pitch.coerceIn(0.5f, 2.0f)
        textToSpeech?.setPitch(currentPitch)
    }

    fun speak(text: String) {
        if (!isInitialized || text.isBlank()) return
        
        textToSpeech?.apply {
            setSpeechRate(currentSpeechRate)
            setPitch(currentPitch)
            speak(text, TextToSpeech.QUEUE_FLUSH, null, UUID.randomUUID().toString())
        }
    }

    fun speakVerse(bookName: String, chapter: Int, verse: Int, text: String) {
        val fullText = buildString {
            append("$bookName, capítulo $chapter, versículo $verse. ")
            append(text)
        }
        speak(fullText)
    }

    fun speakVerses(verses: List<String>, reference: String) {
        val fullText = buildString {
            append("$reference. ")
            verses.forEach { verse ->
                append(verse)
                append(" ")
            }
        }
        speak(fullText)
    }

    fun queueSpeak(text: String) {
        if (!isInitialized || text.isBlank()) return
        textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, UUID.randomUUID().toString())
    }

    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun pause() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
        _isReady.value = false
        _isSpeaking.value = false
    }

    fun isSpeakingNow(): Boolean = _isSpeaking.value
}
