package com.luna.ai.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Chat : Screen("chat")
    object Voice : Screen("voice")
    object Settings : Screen("settings")
    object Tools : Screen("tools")
}
