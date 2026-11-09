package com.luna.ai.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite
import com.luna.ai.feature.home.components.LunaAvatar
import com.luna.ai.feature.home.components.VoiceButton

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepBlack,
                        Color(0xFF11001C),
                        DeepBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // টপ বার
            TopBar()

            Spacer(modifier = Modifier.weight(1f))

            // এভাটার এবং রিং
            LunaAvatar()

            Spacer(modifier = Modifier.height(32.dp))

            // সেন্ট্রাল ভয়েস বাটন
            VoiceButton()

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Listening...",
                color = CyanGlow,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // বটম নেভিগেশন এখানে আসবে (পরের ব্যাচে)
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "🌙 LUNA AI",
                color = PremiumWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your Smart Companion",
                color = CyanGlow,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .size(45.dp)
                .background(ElectricPurple, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "U",
                color = PremiumWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
