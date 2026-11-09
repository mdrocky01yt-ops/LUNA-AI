package com.luna.ai.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun BottomNavBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xCC0A0A10)) // Glassmorphism effect
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(30.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(icon = Icons.Default.Home, label = "Home", isSelected = true)
            NavItem(icon = Icons.Default.Psychology, label = "Memory")
            
            // Central AI Button
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ElectricPurple, CyanGlow)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            NavItem(icon = Icons.Default.Star, label = "Automation")
            NavItem(icon = Icons.Default.Settings, label = "Settings")
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, isSelected: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) CyanGlow else PremiumWhite.copy(alpha = 0.5f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = if (isSelected) CyanGlow else PremiumWhite.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
