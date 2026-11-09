package com.luna.ai.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.luna.ai.design.components.EnergyRing
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple

@Composable
fun LunaAvatar() {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // ১. এনার্জি রিং
        EnergyRing(modifier = Modifier.fillMaxSize())

        // ২. এভাটারের প্লেসহোল্ডার (পরে এখানে আপনার এভাটার ইমেজ বসবে)
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2A1442), DeepBlack)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Face,
                contentDescription = "AI Avatar",
                tint = ElectricPurple,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}
