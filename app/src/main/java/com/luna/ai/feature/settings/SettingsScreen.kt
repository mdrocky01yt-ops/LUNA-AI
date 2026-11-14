package com.luna.ai.feature.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun SettingsScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().background(DeepBlack).padding(top = 24.dp).verticalScroll(rememberScrollState())
    ) {
        // টপ বার
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
            }
            Text(text = "Settings & Permissions", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ১. অ্যাক্সেসিবিলিটি পারমিশন (স্ক্রিন রিড ও ক্লিক)
            SettingsItem(
                icon = Icons.Default.Accessibility, 
                title = "Accessibility Service", 
                subtitle = "লুনাকে স্ক্রিন পড়তে ও ক্লিক করতে দিন",
                onClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            )

            // ২. ওভারলে পারমিশন (ফ্লোটিং বাবল)
            SettingsItem(
                icon = Icons.Default.Layers, 
                title = "Display Over Other Apps", 
                subtitle = "ফ্লোটিং বাবল চালু করতে দিন",
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                }
            )

            // ৩. নোটিফিকেশন লিসেনার পারমিশন (নোটিফিকেশন পড়া)
            SettingsItem(
                icon = Icons.Default.Notifications, 
                title = "Notification Access", 
                subtitle = "লুনাকে নোটিফিকেশন পড়ার অনুমতি দিন",
                onClick = {
                    val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                    context.startActivity(intent)
                }
            )

            // ৪. অন্যান্য সেটিংস (ভবিষ্যতের জন্য)
            SettingsItem(
                icon = Icons.Default.Security, 
                title = "Privacy & Security", 
                subtitle = "আপনার ডেটা লোকালি সেভ থাকে",
                onClick = { }
            )
            
            SettingsItem(
                icon = Icons.Default.Info, 
                title = "About LUNA AI", 
                subtitle = "Version 1.0.0 | Made with ❤️",
                onClick = { }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Note: LUNA AI works best when all permissions above are enabled. Your API keys and chat history are stored 100% securely on your device.",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151520), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = CyanGlow, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = title, color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
