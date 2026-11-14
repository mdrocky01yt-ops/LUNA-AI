package com.luna.ai.feature.customize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun CustomizeScreen(onBackClick: () -> Unit = {}) {
    var isGlowEnabled by remember { mutableStateOf(true) }
    var isAnimationEnabled by remember { mutableStateOf(true) }
    var isSoundEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().background(DeepBlack).padding(top = 24.dp)
    ) {
        // টপ বার
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
            }
            Text(text = "Customize", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // গ্লো ইফেক্ট
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF151520), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ColorLens, contentDescription = "Glow", tint = CyanGlow, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                    Column {
                        Text(text = "RGB Neon Glow", color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "অ্যাপের চারপাশে নিয়ন আলো দেখাবে", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Switch(
                    checked = isGlowEnabled,
                    onCheckedChange = { isGlowEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = ElectricPurple)
                )
            }

            // অ্যানিমেশন ইফেক্ট
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF151520), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Waves, contentDescription = "Animation", tint = CyanGlow, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                    Column {
                        Text(text = "Smooth Animations", color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "এনার্জি রিং ও ভয়েস অ্যানিমেশন", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Switch(
                    checked = isAnimationEnabled,
                    onCheckedChange = { isAnimationEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = ElectricPurple)
                )
            }
            
            // সাউন্ড ইফেক্ট
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF151520), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Waves, contentDescription = "Sound", tint = CyanGlow, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.size(16.dp))
                    Column {
                        Text(text = "Sound Effects", color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = "বাটনে ক্লিক করলে সাউন্ড হবে", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { isSoundEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = CyanGlow, checkedTrackColor = ElectricPurple)
                )
            }
        }
    }
}
