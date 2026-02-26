package com.bubbzeniac.apssof.egrf.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bubbzeniac.apssof.egrf.data.shar.BubblesZenSharedPreference
import com.bubbzeniac.apssof.egrf.data.utils.BubblesZenSystemService
import com.bubbzeniac.apssof.egrf.domain.usecases.BubblesZenGetAllUseCase
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenAppsFlyerState
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BubblesZenLoadViewModel(
    private val bubblesZenGetAllUseCase: BubblesZenGetAllUseCase,
    private val bubblesZenSharedPreference: BubblesZenSharedPreference,
    private val bubblesZenSystemService: BubblesZenSystemService
) : ViewModel() {

    private val _bubblesZenHomeScreenState: MutableStateFlow<BubblesZenHomeScreenState> =
        MutableStateFlow(BubblesZenHomeScreenState.BubblesZenLoading)
    val bubblesZenHomeScreenState = _bubblesZenHomeScreenState.asStateFlow()

    private var bubblesZenGetApps = false


    init {
        viewModelScope.launch {
            when (bubblesZenSharedPreference.bubblesZenAppState) {
                0 -> {
                    if (bubblesZenSystemService.bubblesZenIsOnline()) {
                        BubblesZenApplication.bubblesZenConversionFlow.collect {
                            when(it) {
                                BubblesZenAppsFlyerState.BubblesZenDefault -> {}
                                BubblesZenAppsFlyerState.BubblesZenError -> {
                                    bubblesZenSharedPreference.bubblesZenAppState = 2
                                    _bubblesZenHomeScreenState.value =
                                        BubblesZenHomeScreenState.BubblesZenError
                                    bubblesZenGetApps = true
                                }
                                is BubblesZenAppsFlyerState.BubblesZenSuccess -> {
                                    if (!bubblesZenGetApps) {
                                        bubblesZenGetData(it.bubblesZenData)
                                        bubblesZenGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _bubblesZenHomeScreenState.value =
                            BubblesZenHomeScreenState.BubblesZenNotInternet
                    }
                }
                1 -> {
                    if (bubblesZenSystemService.bubblesZenIsOnline()) {
                        if (BubblesZenApplication.BUBBLES_ZEN_FB_LI != null) {
                            _bubblesZenHomeScreenState.value =
                                BubblesZenHomeScreenState.BubblesZenSuccess(
                                    BubblesZenApplication.BUBBLES_ZEN_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > bubblesZenSharedPreference.bubblesZenExpired) {
                            Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Current time more then expired, repeat request")
                            BubblesZenApplication.bubblesZenConversionFlow.collect {
                                when(it) {
                                    BubblesZenAppsFlyerState.BubblesZenDefault -> {}
                                    BubblesZenAppsFlyerState.BubblesZenError -> {
                                        _bubblesZenHomeScreenState.value =
                                            BubblesZenHomeScreenState.BubblesZenSuccess(
                                                bubblesZenSharedPreference.bubblesZenSavedUrl
                                            )
                                        bubblesZenGetApps = true
                                    }
                                    is BubblesZenAppsFlyerState.BubblesZenSuccess -> {
                                        if (!bubblesZenGetApps) {
                                            bubblesZenGetData(it.bubblesZenData)
                                            bubblesZenGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Current time less then expired, use saved url")
                            _bubblesZenHomeScreenState.value =
                                BubblesZenHomeScreenState.BubblesZenSuccess(
                                    bubblesZenSharedPreference.bubblesZenSavedUrl
                                )
                        }
                    } else {
                        _bubblesZenHomeScreenState.value =
                            BubblesZenHomeScreenState.BubblesZenNotInternet
                    }
                }
                2 -> {
                    _bubblesZenHomeScreenState.value =
                        BubblesZenHomeScreenState.BubblesZenError
                }
            }
        }
    }


    private suspend fun bubblesZenGetData(conversation: MutableMap<String, Any>?) {
        val bubblesZenData = bubblesZenGetAllUseCase.invoke(conversation)
        if (bubblesZenSharedPreference.bubblesZenAppState == 0) {
            if (bubblesZenData == null) {
                bubblesZenSharedPreference.bubblesZenAppState = 2
                _bubblesZenHomeScreenState.value =
                    BubblesZenHomeScreenState.BubblesZenError
            } else {
                bubblesZenSharedPreference.bubblesZenAppState = 1
                bubblesZenSharedPreference.apply {
                    bubblesZenExpired = bubblesZenData.bubblesZenExpires
                    bubblesZenSavedUrl = bubblesZenData.bubblesZenUrl
                }
                _bubblesZenHomeScreenState.value =
                    BubblesZenHomeScreenState.BubblesZenSuccess(bubblesZenData.bubblesZenUrl)
            }
        } else  {
            if (bubblesZenData == null) {
                _bubblesZenHomeScreenState.value =
                    BubblesZenHomeScreenState.BubblesZenSuccess(bubblesZenSharedPreference.bubblesZenSavedUrl)
            } else {
                bubblesZenSharedPreference.apply {
                    bubblesZenExpired = bubblesZenData.bubblesZenExpires
                    bubblesZenSavedUrl = bubblesZenData.bubblesZenUrl
                }
                _bubblesZenHomeScreenState.value =
                    BubblesZenHomeScreenState.BubblesZenSuccess(bubblesZenData.bubblesZenUrl)
            }
        }
    }


    sealed class BubblesZenHomeScreenState {
        data object BubblesZenLoading : BubblesZenHomeScreenState()
        data object BubblesZenError : BubblesZenHomeScreenState()
        data class BubblesZenSuccess(val data: String) : BubblesZenHomeScreenState()
        data object BubblesZenNotInternet: BubblesZenHomeScreenState()
    }
}