package com.luna.ai.feature.home

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.core.model.AIState
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite
import com.luna.ai.feature.home.components.BottomNavBar
import com.luna.ai.feature.home.components.LunaAvatar
import com.luna.ai.service.FloatingBubbleService

@Composable
fun HomeScreen(
    onChatClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onToolsClick: () -> Unit = {},
    onTasksClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSilentClick: () -> Unit = {}
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
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
            // টপ বার
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = PremiumWhite,
                    modifier = Modifier.size(28.dp).clickable { onMenuClick() }
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌙", fontSize = 20.sp)
                        Text(text = " LUNA AI", color = PremiumWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Text(text = "Your Smart Companion", color = CyanGlow, fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "Premium", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.size(12.dp))
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = PremiumWhite, modifier = Modifier.size(24.dp).clickable { onSettingsClick() })
                    Spacer(modifier = Modifier.size(12.dp))
                    Icon(imageVector = Icons.Default.Widgets, contentDescription = "Bubble", tint = CyanGlow, modifier = Modifier.size(24.dp).clickable {
                        if (Settings.canDrawOverlays(context)) {
                            context.startService(Intent(context, FloatingBubbleService::class.java))
                        } else {
                            context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                        }
                    })
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // এভাটার (কোনো সাইড বাটন নেই)
            LunaAvatar(aiState = AIState.LISTENING)

            Spacer(modifier = Modifier.weight(1f))

            // স্ট্যাটাস লেখা
            Text(
                text = "Listening...", 
                color = CyanGlow, 
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // নতুন বটম নেভিগেশন বার
            BottomNavBar(
                onHomeClick = {},
                onChatClick = onChatClick,
                onToolsClick = onToolsClick,
                onSilentClick = onSilentClick
            )
        }
    }
}
