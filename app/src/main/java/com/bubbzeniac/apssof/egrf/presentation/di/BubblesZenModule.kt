package com.bubbzeniac.apssof.egrf.presentation.di

import com.bubbzeniac.apssof.egrf.data.repo.BubblesZenRepository
import com.bubbzeniac.apssof.egrf.data.shar.BubblesZenSharedPreference
import com.bubbzeniac.apssof.egrf.data.utils.BubblesZenPushToken
import com.bubbzeniac.apssof.egrf.data.utils.BubblesZenSystemService
import com.bubbzeniac.apssof.egrf.domain.usecases.BubblesZenGetAllUseCase
import com.bubbzeniac.apssof.egrf.presentation.pushhandler.BubblesZenPushHandler
import com.bubbzeniac.apssof.egrf.presentation.ui.load.BubblesZenLoadViewModel
import com.bubbzeniac.apssof.egrf.presentation.ui.view.BubblesZenViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bubblesZenModule = module {
    factory {
        BubblesZenPushHandler()
    }
    single {
        BubblesZenRepository()
    }
    single {
        BubblesZenSharedPreference(get())
    }
    factory {
        BubblesZenPushToken()
    }
    factory {
        BubblesZenSystemService(get())
    }
    factory {
        BubblesZenGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BubblesZenViFun(get())
    }
    viewModel {
        BubblesZenLoadViewModel(get(), get(), get())
    }
}