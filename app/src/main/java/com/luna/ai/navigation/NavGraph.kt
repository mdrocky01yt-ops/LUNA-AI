package com.luna.ai.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.luna.ai.feature.apikeys.ApiKeysScreen
import com.luna.ai.feature.chat.ChatScreen
import com.luna.ai.feature.headlines.HeadlinesScreen
import com.luna.ai.feature.home.HomeScreen
import com.luna.ai.feature.memory.MemoryScreen
import com.luna.ai.feature.menu.MenuScreen
import com.luna.ai.feature.onboarding.OnboardingScreen
import com.luna.ai.feature.placeholder.PlaceholderScreen
import com.luna.ai.feature.settings.SettingsScreen
import com.luna.ai.feature.silent.SilentScreen
import com.luna.ai.feature.soul.SoulScreen
import com.luna.ai.feature.splash.SplashScreen
import com.luna.ai.feature.tasks.TaskScreen
import com.luna.ai.feature.tools.ToolsScreen
import com.luna.ai.feature.voice.VoiceChangerScreen
import com.luna.ai.feature.voice.VoiceScreen

@Composable
fun LunaNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
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
                onTasksClick = { navController.navigate(Screen.Tasks.route) },
                onMenuClick = { navController.navigate(Screen.Menu.route) }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onBackClick = { navController.popBackStack() },
                onItemClick = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Chat.route) { ChatScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Voice.route) { VoiceScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Settings.route) { SettingsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Tools.route) { ToolsScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Tasks.route) { TaskScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.ApiKeys.route) { ApiKeysScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.VoiceChanger.route) { VoiceChangerScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Headlines.route) { HeadlinesScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Memory.route) { MemoryScreen(onBackClick = { navController.popBackStack() }) }
        composable(Screen.Silent.route) { SilentScreen(onBackClick = { navController.popBackStack() }) }
        
        // নতুন সোল (Soul) স্ক্রিন
        composable(Screen.Soul.route) { SoulScreen(onBackClick = { navController.popBackStack() }) }

        // বাকি ফাঁকা স্ক্রিনগুলো
        composable(Screen.Device.route) { PlaceholderScreen("Device Control") }
        composable(Screen.Customize.route) { PlaceholderScreen("Customize") }
    }
}
