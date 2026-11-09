package com.luna.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luna.ai.feature.chat.ChatScreen
import com.luna.ai.feature.home.HomeScreen
import com.luna.ai.feature.settings.SettingsScreen
import com.luna.ai.feature.voice.VoiceScreen

@Composable
fun LunaNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onChatClick = { navController.navigate(Screen.Chat.route) },
                onVoiceClick = { navController.navigate(Screen.Voice.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Chat.route) {
            ChatScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Voice.route) {
            VoiceScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onSaveApiKey = { key ->
                    // এখানে API Key সেভ করার লজিক পরে যোগ হবে
                    navController.popBackStack()
                }
            )
        }
    }
}
