package com.bubbzeniac.apssof.egrf.data.utils

import android.util.Log
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class BubblesZenPushToken {

    suspend fun bubblesZenGetToken(
        bubblesZenMaxAttempts: Int = 3,
        bubblesZenDelayMs: Long = 1500
    ): String {

        repeat(bubblesZenMaxAttempts - 1) {
            try {
                val bubblesZenToken = FirebaseMessaging.getInstance().token.await()
                return bubblesZenToken
            } catch (e: Exception) {
                Log.e(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(bubblesZenDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}