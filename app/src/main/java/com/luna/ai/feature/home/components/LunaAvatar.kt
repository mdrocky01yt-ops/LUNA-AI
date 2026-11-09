package com.luna.ai.feature.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.luna.ai.core.model.AIState
import com.luna.ai.design.components.EnergyRing

@Composable
fun LunaAvatar(aiState: AIState = AIState.IDLE) {
    // Breathing animation (শ্বাস-প্রশ্বাসের মতো ওঠানামা)
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ), label = "avatarScale"
    )

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // ১. এনার্জি রিং
        EnergyRing(modifier = Modifier.fillMaxSize(), aiState = aiState)

        // ২. এভাটারের ছবি (আপাতত একটি ডেমো ছবি, পরে আপনার নিজের ছবি বসাতে পারবেন)
        AsyncImage(
            model = "https://raw.githubusercontent.com/mdroccky01yt-ops/LUNA-AI/main/avatar.png", // প্লেসহোল্ডার লিংক
            contentDescription = "AI Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF2A1442), Color(0xFF05060A))
                    )
                )
        )
    }
}
