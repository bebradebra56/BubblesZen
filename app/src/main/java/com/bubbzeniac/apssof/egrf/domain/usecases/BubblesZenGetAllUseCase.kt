package com.bubbzeniac.apssof.egrf.domain.usecases

import android.util.Log
import com.bubbzeniac.apssof.egrf.data.repo.BubblesZenRepository
import com.bubbzeniac.apssof.egrf.data.utils.BubblesZenPushToken
import com.bubbzeniac.apssof.egrf.data.utils.BubblesZenSystemService
import com.bubbzeniac.apssof.egrf.domain.model.BubblesZenEntity
import com.bubbzeniac.apssof.egrf.domain.model.BubblesZenParam
import com.bubbzeniac.apssof.egrf.presentation.app.BubblesZenApplication

class BubblesZenGetAllUseCase(
    private val bubblesZenRepository: BubblesZenRepository,
    private val bubblesZenSystemService: BubblesZenSystemService,
    private val bubblesZenPushToken: BubblesZenPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BubblesZenEntity?{
        val params = BubblesZenParam(
            bubblesZenLocale = bubblesZenSystemService.bubblesZenGetLocale(),
            bubblesZenPushToken = bubblesZenPushToken.bubblesZenGetToken(),
            bubblesZenAfId = bubblesZenSystemService.bubblesZenGetAppsflyerId()
        )
        Log.d(BubblesZenApplication.BUBBLES_ZEN_MAIN_TAG, "Params for request: $params")
        return bubblesZenRepository.bubblesZenGetClient(params, conversion)
    }



}