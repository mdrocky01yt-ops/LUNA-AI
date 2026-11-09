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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Icon
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
import com.luna.ai.feature.home.components.BottomNavBar
import com.luna.ai.feature.home.components.LunaAvatar
import com.luna.ai.feature.home.components.QuickToolsPanel
import com.luna.ai.feature.home.components.VoiceButton

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlack, Color(0xFF11001C), DeepBlack)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar()
            Spacer(modifier = Modifier.weight(1f))

            // এভাটার এবং পাশের বাটন
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chat Button (Left)
                Box(
                    modifier = Modifier.size(50.dp).background(Color(0x33FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = PremiumWhite)
                }

                LunaAvatar()

                // Tools Button (Right)
                Box(
                    modifier = Modifier.size(50.dp).background(Color(0x33FFFFFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.GridView, contentDescription = "Tools", tint = PremiumWhite)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            VoiceButton()
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Listening...", color = CyanGlow, fontSize = 14.sp)

            Spacer(modifier = Modifier.weight(1f))

            // কুইক টুলস প্যানেল (Apps, YouTube, Tasks, More)
            QuickToolsPanel()

            // বটম নেভিগেশন বার
            BottomNavBar()
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "🌙 LUNA AI", color = PremiumWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Your Smart Companion", color = CyanGlow, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier.size(45.dp).background(ElectricPurple, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "U", color = PremiumWhite, fontWeight = FontWeight.Bold)
        }
    }
}
