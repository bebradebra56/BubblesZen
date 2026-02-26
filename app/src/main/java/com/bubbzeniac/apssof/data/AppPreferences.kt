package com.bubbzeniac.apssof.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val bubbleSoundsEnabled: Boolean = true,
    val animationEnabled: Boolean = true,
    val hapticEnabled: Boolean = true,
    val selectedTheme: Int = 0,
    val bubbleSpeed: Float = 1f,
    val ambientVolume: Float = 0.7f,
    val selectedAmbient: String = "SILENCE",
)

class AppPreferences(private val context: Context) {

    private object Keys {
        val BUBBLE_SOUNDS = booleanPreferencesKey("bubble_sounds")
        val ANIMATION_ENABLED = booleanPreferencesKey("animation_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        val SELECTED_THEME = intPreferencesKey("selected_theme")
        val BUBBLE_SPEED = floatPreferencesKey("bubble_speed")
        val AMBIENT_VOLUME = floatPreferencesKey("ambient_volume")
        val SELECTED_AMBIENT = stringPreferencesKey("selected_ambient")
    }

    val settings: Flow<AppSettings> = context.settingsStore.data.map { prefs ->
        AppSettings(
            bubbleSoundsEnabled = prefs[Keys.BUBBLE_SOUNDS] ?: true,
            animationEnabled = prefs[Keys.ANIMATION_ENABLED] ?: true,
            hapticEnabled = prefs[Keys.HAPTIC_ENABLED] ?: true,
            selectedTheme = prefs[Keys.SELECTED_THEME] ?: 0,
            bubbleSpeed = prefs[Keys.BUBBLE_SPEED] ?: 1f,
            ambientVolume = prefs[Keys.AMBIENT_VOLUME] ?: 0.7f,
            selectedAmbient = prefs[Keys.SELECTED_AMBIENT] ?: "SILENCE",
        )
    }

    suspend fun setBubbleSounds(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.BUBBLE_SOUNDS] = enabled }
    }

    suspend fun setAnimationEnabled(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.ANIMATION_ENABLED] = enabled }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.settingsStore.edit { it[Keys.HAPTIC_ENABLED] = enabled }
    }

    suspend fun setSelectedTheme(index: Int) {
        context.settingsStore.edit { it[Keys.SELECTED_THEME] = index }
    }

    suspend fun setBubbleSpeed(speed: Float) {
        context.settingsStore.edit { it[Keys.BUBBLE_SPEED] = speed }
    }

    suspend fun setAmbientVolume(volume: Float) {
        context.settingsStore.edit { it[Keys.AMBIENT_VOLUME] = volume }
    }

    suspend fun setSelectedAmbient(ambient: String) {
        context.settingsStore.edit { it[Keys.SELECTED_AMBIENT] = ambient }
    }

    suspend fun resetToDefaults() {
        context.settingsStore.edit { prefs ->
            prefs[Keys.BUBBLE_SOUNDS] = true
            prefs[Keys.ANIMATION_ENABLED] = true
            prefs[Keys.HAPTIC_ENABLED] = true
            prefs[Keys.SELECTED_THEME] = 0
            prefs[Keys.BUBBLE_SPEED] = 1f
            prefs[Keys.AMBIENT_VOLUME] = 0.7f
            prefs[Keys.SELECTED_AMBIENT] = "SILENCE"
        }
    }
}
