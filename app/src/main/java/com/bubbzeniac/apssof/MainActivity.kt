package com.bubbzeniac.apssof

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.bubbzeniac.apssof.navigation.AppNavigation
import com.bubbzeniac.apssof.ui.theme.BubblesZenTheme
import com.bubbzeniac.apssof.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            BubblesZenTheme {
                AppNavigation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appViewModel.onSessionResume()
    }

    override fun onPause() {
        super.onPause()
        appViewModel.onSessionPause()
    }
}
