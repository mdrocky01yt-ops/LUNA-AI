package com.luna.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.luna.ai.design.theme.LunaAITheme
import com.luna.ai.feature.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LunaAITheme {
                // এখন অ্যাপ ওপেন হলে সরাসরি হোম স্ক্রিন দেখাবে
                HomeScreen()
            }
        }
    }
}
