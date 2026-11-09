package com.luna.ai.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.PremiumWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), label = "alpha"
    )
    
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1500), label = "scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500) // ২.৫ সেকেন্ড পর হোম স্ক্রিনে যাবে
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepBlack, Color(0xFF11001C), DeepBlack)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🌙",
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "LUNA AI",
                color = PremiumWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Always With You",
                color = CyanGlow,
                fontSize = 14.sp,
                modifier = Modifier
                    .scale(scaleAnim)
                    .alpha(alphaAnim)
            )
        }
    }
}
