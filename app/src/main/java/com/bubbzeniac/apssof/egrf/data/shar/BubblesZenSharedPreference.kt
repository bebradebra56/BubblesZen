package com.bubbzeniac.apssof.egrf.data.shar

import android.content.Context
import androidx.core.content.edit

class BubblesZenSharedPreference(context: Context) {
    private val bubblesZenPrefs = context.getSharedPreferences("bubblesZenSharedPrefsAb", Context.MODE_PRIVATE)

    var bubblesZenSavedUrl: String
        get() = bubblesZenPrefs.getString(BUBBLES_ZEN_SAVED_URL, "") ?: ""
        set(value) = bubblesZenPrefs.edit { putString(BUBBLES_ZEN_SAVED_URL, value) }

    var bubblesZenExpired : Long
        get() = bubblesZenPrefs.getLong(BUBBLES_ZEN_EXPIRED, 0L)
        set(value) = bubblesZenPrefs.edit { putLong(BUBBLES_ZEN_EXPIRED, value) }

    var bubblesZenAppState: Int
        get() = bubblesZenPrefs.getInt(BUBBLES_ZEN_APPLICATION_STATE, 0)
        set(value) = bubblesZenPrefs.edit { putInt(BUBBLES_ZEN_APPLICATION_STATE, value) }

    var bubblesZenNotificationRequest: Long
        get() = bubblesZenPrefs.getLong(BUBBLES_ZEN_NOTIFICAITON_REQUEST, 0L)
        set(value) = bubblesZenPrefs.edit { putLong(BUBBLES_ZEN_NOTIFICAITON_REQUEST, value) }

    var bubblesZenNotificationState:Int
        get() = bubblesZenPrefs.getInt(BUBBLES_ZEN_NOTIFICATION_STATE, 0)
        set(value) = bubblesZenPrefs.edit { putInt(BUBBLES_ZEN_NOTIFICATION_STATE, value) }

    companion object {
        private const val BUBBLES_ZEN_NOTIFICATION_STATE = "bubblesZenNotificationState"
        private const val BUBBLES_ZEN_SAVED_URL = "bubblesZenSavedUrl"
        private const val BUBBLES_ZEN_EXPIRED = "bubblesZenExpired"
        private const val BUBBLES_ZEN_APPLICATION_STATE = "bubblesZenApplicationState"
        private const val BUBBLES_ZEN_NOTIFICAITON_REQUEST = "bubblesZenNotificationRequest"
    }
}