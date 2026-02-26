package com.bubbzeniac.apssof.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Sounds : Screen("sounds")
    data object Themes : Screen("themes")
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
}
