package com.luna.ai.feature.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack

@Composable
fun VoiceScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Voice Screen Coming Soon...", color = CyanGlow)
    }
}
