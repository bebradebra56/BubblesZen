package com.bubbzeniac.apssof.egrf.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class BubblesZenDataStore : ViewModel(){
    val bubblesZenViList: MutableList<BubblesZenVi> = mutableListOf()
    var bubblesZenIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var bubblesZenContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var bubblesZenView: BubblesZenVi

}