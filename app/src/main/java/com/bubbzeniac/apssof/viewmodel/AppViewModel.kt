package com.bubbzeniac.apssof.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bubbzeniac.apssof.data.AppPreferences
import com.bubbzeniac.apssof.data.AppSettings
import com.bubbzeniac.apssof.data.ProgressData
import com.bubbzeniac.apssof.data.SessionRepository
import com.bubbzeniac.apssof.sound.AmbientType
import com.bubbzeniac.apssof.sound.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val appPreferences = AppPreferences(application)
    private val sessionRepository = SessionRepository(application)
    val soundManager = SoundManager(application)

    val settings: StateFlow<AppSettings> = appPreferences.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val progressData: StateFlow<ProgressData> = sessionRepository.progressData.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressData()
    )

    private val _currentAmbient = MutableStateFlow(AmbientType.SILENCE)
    val currentAmbient: StateFlow<AmbientType> = _currentAmbient.asStateFlow()

    private val _isAmbientPlaying = MutableStateFlow(false)
    val isAmbientPlaying: StateFlow<Boolean> = _isAmbientPlaying.asStateFlow()

    private var sessionStartTime = System.currentTimeMillis()
    private var accumulatedMinutes = 0

    init {
        startSessionSavingLoop()
    }

    private fun startSessionSavingLoop() {
        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                val elapsedMinutes = ((System.currentTimeMillis() - sessionStartTime) / 60_000).toInt()
                val newMinutes = elapsedMinutes - accumulatedMinutes
                if (newMinutes > 0) {
                    accumulatedMinutes = elapsedMinutes
                    sessionRepository.addMinutesToday(newMinutes)
                }
            }
        }
    }

    fun onSessionResume() {
        sessionStartTime = System.currentTimeMillis()
        accumulatedMinutes = 0
        if (_isAmbientPlaying.value && _currentAmbient.value != AmbientType.SILENCE) {
            soundManager.resumeAmbient()
        }
    }

    fun onSessionPause() {
        viewModelScope.launch {
            val elapsedMinutes = ((System.currentTimeMillis() - sessionStartTime) / 60_000).toInt()
            val newMinutes = elapsedMinutes - accumulatedMinutes
            if (newMinutes > 0) {
                accumulatedMinutes = elapsedMinutes
                sessionRepository.addMinutesToday(newMinutes)
            }
        }
        soundManager.pauseAmbient()
    }

    fun playBubblePop() {
        if (settings.value.bubbleSoundsEnabled) {
            soundManager.playBubblePop()
        }
    }

    fun setAmbient(type: AmbientType) {
        _currentAmbient.value = type
        val volume = settings.value.ambientVolume
        if (type == AmbientType.SILENCE) {
            soundManager.stopAmbient()
            _isAmbientPlaying.value = false
        } else {
            soundManager.startAmbient(type, volume)
            _isAmbientPlaying.value = true
        }
        viewModelScope.launch {
            appPreferences.setSelectedAmbient(type.name)
        }
    }

    fun toggleAmbient() {
        val current = _currentAmbient.value
        if (current == AmbientType.SILENCE) return
        if (_isAmbientPlaying.value) {
            soundManager.pauseAmbient()
            _isAmbientPlaying.value = false
        } else {
            soundManager.resumeAmbient()
            _isAmbientPlaying.value = true
        }
    }

    fun setAmbientVolume(volume: Float) {
        soundManager.setVolume(volume)
        viewModelScope.launch {
            appPreferences.setAmbientVolume(volume)
        }
    }

    fun setSelectedTheme(index: Int) {
        viewModelScope.launch { appPreferences.setSelectedTheme(index) }
    }

    fun setBubbleSpeed(speed: Float) {
        viewModelScope.launch { appPreferences.setBubbleSpeed(speed) }
    }

    fun setBubbleSounds(enabled: Boolean) {
        soundManager.setEnabled(enabled)
        viewModelScope.launch { appPreferences.setBubbleSounds(enabled) }
    }

    fun setAnimationEnabled(enabled: Boolean) {
        viewModelScope.launch { appPreferences.setAnimationEnabled(enabled) }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch { appPreferences.setHapticEnabled(enabled) }
    }

    fun clearProgress() {
        viewModelScope.launch { sessionRepository.clearAllProgress() }
    }

    fun resetSettings() {
        viewModelScope.launch { appPreferences.resetToDefaults() }
    }

    override fun onCleared() {
        super.onCleared()
        soundManager.release()
    }
}
