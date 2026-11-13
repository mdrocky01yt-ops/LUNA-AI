package com.luna.ai.design.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.luna.ai.design.theme.CyanGlow

@Composable
fun AudioWaveform(isSpeaking: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "anim1"
    )
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "anim2"
    )
    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "anim3"
    )

    Canvas(modifier = modifier.fillMaxWidth().height(60.dp)) {
        if (!isSpeaking) return@Canvas
        
        val centerY = size.height / 2
        val barWidth = 8f
        val gap = 12f
        val barsCount = 15
        val startX = (size.width - ((barWidth + gap) * barsCount)) / 2

        for (i in 0 until barsCount) {
            val heightMultiplier = when (i % 3) {
                0 -> anim1
                1 -> anim2
                else -> anim3
            }
            val barHeight = size.height * heightMultiplier
            val x = startX + (i * (barWidth + gap))
            
            drawLine(
                color = CyanGlow,
                start = Offset(x, centerY - barHeight / 2),
                end = Offset(x, centerY + barHeight / 2),
                strokeWidth = barWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
