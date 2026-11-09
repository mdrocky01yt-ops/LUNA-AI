package com.luna.ai.design.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.NeonViolet

@Composable
fun EnergyRing(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ring")
    
    // ঘোরার অ্যানিমেশন (Rotation)
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // পালস অ্যানিমেশন (Pulse)
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasSize = size.minDimension
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (canvasSize / 2) * 0.8f * pulse

        // বাইরের গ্লোয়িং রিং
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    ElectricPurple,
                    CyanGlow,
                    Color.Transparent
                )
            ),
            radius = radius,
            center = center,
            style = Stroke(width = 8f, cap = StrokeCap.Round)
        )

        // ভেতরের হালকা রিং
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(NeonViolet, Color.Transparent)
            ),
            startAngle = rotation,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - radius + 20f, center.y - radius + 20f),
            size = Size((radius - 20f) * 2, (radius - 20f) * 2),
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
    }
}
