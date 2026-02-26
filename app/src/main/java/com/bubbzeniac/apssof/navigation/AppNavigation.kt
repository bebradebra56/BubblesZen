package com.bubbzeniac.apssof.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bubbzeniac.apssof.ui.components.BubbleBottomNav
import com.bubbzeniac.apssof.ui.screens.MainScreen
import com.bubbzeniac.apssof.ui.screens.ProgressScreen
import com.bubbzeniac.apssof.ui.screens.SettingsScreen
import com.bubbzeniac.apssof.ui.screens.SoundsScreen
import com.bubbzeniac.apssof.ui.screens.ThemesScreen
import com.bubbzeniac.apssof.ui.theme.LocalAppTheme
import com.bubbzeniac.apssof.ui.theme.themeColorsForIndex
import com.bubbzeniac.apssof.viewmodel.AppViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()

    val settings by appViewModel.settings.collectAsState()
    val themeColors = remember(settings.selectedTheme) {
        themeColorsForIndex(settings.selectedTheme)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentScreen = when (currentRoute) {
        Screen.Main.route -> Screen.Main
        Screen.Sounds.route -> Screen.Sounds
        Screen.Themes.route -> Screen.Themes
        Screen.Progress.route -> Screen.Progress
        Screen.Settings.route -> Screen.Settings
        else -> Screen.Main
    }

    CompositionLocalProvider(LocalAppTheme provides themeColors) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(themeColors.bgColors)
                )
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route,
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.95f)
                    },
                    exitTransition = {
                        fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.98f)
                    },
                ) {
                    composable(Screen.Main.route) {
                        MainScreen(viewModel = appViewModel)
                    }
                    composable(Screen.Sounds.route) {
                        SoundsScreen(viewModel = appViewModel)
                    }
                    composable(Screen.Themes.route) {
                        ThemesScreen(viewModel = appViewModel)
                    }
                    composable(Screen.Progress.route) {
                        ProgressScreen(viewModel = appViewModel)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(viewModel = appViewModel)
                    }
                }
            }

            BubbleBottomNav(
                currentScreen = currentScreen,
                onNavigate = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
