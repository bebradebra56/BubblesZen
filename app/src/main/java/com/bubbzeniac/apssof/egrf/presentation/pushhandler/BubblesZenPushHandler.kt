package com.bubbzeniac.apssof.egrf.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication

class BubblesZenPushHandler {
    fun bubblesZenHandlePush(extras: Bundle?) {
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map: MutableMap<String, String?> = HashMap()
            val ks = extras.keySet()
            val iterator: Iterator<String> = ks.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = extras.getString(key)
            }
            Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Map from Push = $map")
            map.let {
                if (map.containsKey("url")) {
                    BubblesZenApplication.BUBBLES_ZEN_FB_LI = map["url"]
                    Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Push data no!")
        }
    }

}