package com.luna.ai.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Voice : Screen("voice")
    object Settings : Screen("settings")
    object Tools : Screen("tools") // নতুন যুক্ত হলো
}
