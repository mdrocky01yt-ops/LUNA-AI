package com.luna.ai.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.ElectricBlue
import com.luna.ai.design.theme.ElectricPurple

@Composable
fun VoiceButton(
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(ElectricBlue, ElectricPurple)
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice Input",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}
