package com.bubbzeniac.apssof

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.bubbzeniac.apssof.egrf.BubblesZenGlobalLayoutUtil
import com.bubbzeniac.apssof.egrf.bubblesZenSetupSystemBars
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication
import com.bubbzeniac.apssof.egrf.presentation.pushhandler.BubblesZenPushHandler
import org.koin.android.ext.android.inject

class BubblesZenActivity : AppCompatActivity() {

    private val bubblesZenPushHandler by inject<BubblesZenPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bubblesZenSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_bubbles_zen)

        val bubblesZenRootView = findViewById<View>(android.R.id.content)
        BubblesZenGlobalLayoutUtil().bubblesZenAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(bubblesZenRootView) { bubblesZenView, bubblesZenInsets ->
            val bubblesZenSystemBars = bubblesZenInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bubblesZenDisplayCutout = bubblesZenInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val bubblesZenIme = bubblesZenInsets.getInsets(WindowInsetsCompat.Type.ime())


            val bubblesZenTopPadding = maxOf(bubblesZenSystemBars.top, bubblesZenDisplayCutout.top)
            val bubblesZenLeftPadding = maxOf(bubblesZenSystemBars.left, bubblesZenDisplayCutout.left)
            val bubblesZenRightPadding = maxOf(bubblesZenSystemBars.right, bubblesZenDisplayCutout.right)
            window.setSoftInputMode(BubblesZenApplication.bubblesZenInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "ADJUST PUN")
                val bubblesZenBottomInset = maxOf(bubblesZenSystemBars.bottom, bubblesZenDisplayCutout.bottom)

                bubblesZenView.setPadding(bubblesZenLeftPadding, bubblesZenTopPadding, bubblesZenRightPadding, 0)

                bubblesZenView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = bubblesZenBottomInset
                }
            } else {
                Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "ADJUST RESIZE")

                val bubblesZenBottomInset = maxOf(bubblesZenSystemBars.bottom, bubblesZenDisplayCutout.bottom, bubblesZenIme.bottom)

                bubblesZenView.setPadding(bubblesZenLeftPadding, bubblesZenTopPadding, bubblesZenRightPadding, 0)

                bubblesZenView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = bubblesZenBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Activity onCreate()")
        bubblesZenPushHandler.bubblesZenHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            bubblesZenSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        bubblesZenSetupSystemBars()
    }
}