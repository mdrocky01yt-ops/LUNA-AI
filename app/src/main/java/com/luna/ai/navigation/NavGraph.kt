package com.luna.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luna.ai.feature.chat.ChatScreen
import com.luna.ai.feature.home.HomeScreen
import com.luna.ai.feature.onboarding.OnboardingScreen
import com.luna.ai.feature.settings.SettingsScreen
import com.luna.ai.feature.splash.SplashScreen
import com.luna.ai.feature.tasks.TaskScreen
import com.luna.ai.feature.tools.ToolsScreen
import com.luna.ai.feature.voice.VoiceScreen

@Composable
fun LunaNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } })
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onFinished = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onChatClick = { navController.navigate(Screen.Chat.route) },
                onVoiceClick = { navController.navigate(Screen.Voice.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onToolsClick = { navController.navigate(Screen.Tools.route) },
                onTasksClick = { navController.navigate(Screen.Tasks.route) } // টাস্ক স্ক্রিনে যাওয়ার লিংক
            )
        }
        composable(Screen.Chat.route) { ChatScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Voice.route) { VoiceScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Tools.route) { ToolsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Tasks.route) { TaskScreen(onBackClick = { navController.popBackStack() }) }
    }
}
