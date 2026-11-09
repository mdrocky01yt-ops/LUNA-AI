package com.luna.ai.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val savedApiKey by viewModel.apiKey.collectAsState()
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // টপ বার
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A10))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Settings",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // সেটিংস অপশনগুলো
            SettingsItem(
                icon = Icons.Default.Key,
                title = "API Configuration",
                subtitle = if (savedApiKey.isBlank()) "Not configured" else "Configured (sk-...)",
                onClick = { showApiKeyDialog = true }
            )

            SettingsItem(
                icon = Icons.Default.Mic,
                title = "Voice Customization",
                subtitle = "Coming soon...",
                onClick = { /* ভবিষ্যতে যুক্ত হবে */ }
            )

            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Theme & Appearance",
                subtitle = "Dark (Default)",
                onClick = { /* ভবিষ্যতে যুক্ত হবে */ }
            )

            SettingsItem(
                icon = Icons.Default.Security,
                title = "Privacy & Security",
                subtitle = "Manage your data",
                onClick = { /* ভবিষ্যতে যুক্ত হবে */ }
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = "About LUNA AI",
                subtitle = "Version 1.0.0",
                onClick = { showAboutDialog = true }
            )
        }
    }

    // API Key ডায়ালগ
    if (showApiKeyDialog) {
        var apiKeyInput by remember { mutableStateOf(savedApiKey) }
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("DeepSeek API Key", color = PremiumWhite) },
            text = {
                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    placeholder = { Text("sk-...", color = Color.DarkGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElectricPurple, RoundedCornerShape(12.dp)),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color(0xFF151520),
                        unfocusedContainerColor = Color(0xFF151520),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveApiKey(apiKeyInput)
                    showApiKeyDialog = false
                }) {
                    Text("Save", color = CyanGlow)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color(0xFF151520)
        )
    }

    // About ডায়ালগ
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("🌙 LUNA AI", color = PremiumWhite) },
            text = { 
                Text(
                    "Your Smart Companion.\n\nDeveloped with Jetpack Compose and DeepSeek AI.\n\nVersion 1.0.0",
                    color = Color.LightGray
                ) 
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = CyanGlow)
                }
            },
            containerColor = Color(0xFF151520)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(Color(0x1AFFFFFF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = ElectricPurple)
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(text = title, color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}
