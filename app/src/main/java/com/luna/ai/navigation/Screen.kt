package com.luna.ai.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Menu : Screen("menu")
    object Chat : Screen("chat")
    object Silent : Screen("silent")
    object Voice : Screen("voice")
    object Settings : Screen("settings")
    object Tools : Screen("tools")
    object Tasks : Screen("tasks")
    object ApiKeys : Screen("apikeys")
    object Device : Screen("device")
    object Memory : Screen("memory")
    object Soul : Screen("soul")
    object Headlines : Screen("headlines")
    object Customize : Screen("customize")
}
