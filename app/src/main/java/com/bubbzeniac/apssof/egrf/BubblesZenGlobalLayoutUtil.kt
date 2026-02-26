package com.bubbzeniac.apssof.egrf

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication

class BubblesZenGlobalLayoutUtil {

    private var bubblesZenMChildOfContent: View? = null
    private var bubblesZenUsableHeightPrevious = 0

    fun bubblesZenAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        bubblesZenMChildOfContent = content.getChildAt(0)

        bubblesZenMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val bubblesZenUsableHeightNow = bubblesZenComputeUsableHeight()
        if (bubblesZenUsableHeightNow != bubblesZenUsableHeightPrevious) {
            val bubblesZenUsableHeightSansKeyboard = bubblesZenMChildOfContent?.rootView?.height ?: 0
            val bubblesZenHeightDifference = bubblesZenUsableHeightSansKeyboard - bubblesZenUsableHeightNow

            if (bubblesZenHeightDifference > (bubblesZenUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BubblesZenApplication.bubblesZenInputMode)
            } else {
                activity.window.setSoftInputMode(BubblesZenApplication.bubblesZenInputMode)
            }
//            mChildOfContent?.requestLayout()
            bubblesZenUsableHeightPrevious = bubblesZenUsableHeightNow
        }
    }

    private fun bubblesZenComputeUsableHeight(): Int {
        val r = Rect()
        bubblesZenMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}