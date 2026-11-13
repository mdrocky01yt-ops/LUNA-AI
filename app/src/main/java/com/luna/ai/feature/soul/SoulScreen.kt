package com.luna.ai.feature.soul

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

data class PersonalityProfile(
    val name: String,
    val prompt: String,
    val icon: ImageVector,
    val description: String
)

@Composable
fun SoulScreen(
    onBackClick: () -> Unit = {},
    viewModel: SoulViewModel = hiltViewModel()
) {
    val currentPrompt by viewModel.currentPersonality.collectAsState()
    var selectedPrompt by remember { mutableStateOf(currentPrompt) }

    val profiles = listOf(
        PersonalityProfile(
            "Friendly Luna",
            "You are Luna, a helpful, friendly, and sweet AI assistant. You reply in Bengali with a warm tone.",
            Icons.Default.Favorite,
            "সবসময় বন্ধুত্বপূর্ণ ও মিষ্টি ভাষায় কথা বলবে।"
        ),
        PersonalityProfile(
            "Professional",
            "You are Luna, a highly professional and efficient AI assistant. You reply in Bengali, keeping answers direct and business-like.",
            Icons.Default.Work,
            "প্রফেশনাল এবং সংক্ষিপ্ত উত্তর দেবে।"
        ),
        PersonalityProfile(
            "Funny & Sarcastic",
            "You are Luna, a witty and slightly sarcastic AI assistant. You reply in Bengali, often adding a touch of humor.",
            Icons.Default.EmojiEmotions,
            "মজার এবং একটু রসিকতা মেশানো কথা বলবে।"
        ),
        PersonalityProfile(
            "Philosopher",
            "You are Luna, a deep-thinking philosopher AI. You reply in Bengali, giving thoughtful and deep answers.",
            Icons.Default.Psychology,
            "গভীর ও দার্শনিক দৃষ্টিভঙ্গি দিয়ে উত্তর দেবে।"
        )
    )

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
            Text(text = "LUNA's Soul", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // প্রোফাইল লিস্ট
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(profiles) { profile ->
                val isSelected = selectedPrompt == profile.prompt
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) ElectricPurple.copy(alpha = 0.3f) else Color(0xFF151520),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedPrompt = profile.prompt }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(profile.icon, contentDescription = null, tint = CyanGlow, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.size(16.dp))
                        Column {
                            Text(text = profile.name, color = if (isSelected) CyanGlow else PremiumWhite, fontWeight = FontWeight.Bold)
                            Text(text = profile.description, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = CyanGlow)
                    }
                }
            }
            
            // কাস্টম প্রম্পট বক্স
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Custom Personality", color = PremiumWhite, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = selectedPrompt,
                    onValueChange = { selectedPrompt = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricPurple,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = PremiumWhite,
                        unfocusedTextColor = PremiumWhite,
                        focusedContainerColor = Color(0xFF151520),
                        unfocusedContainerColor = Color(0xFF151520)
                    )
                )
            }
        }

        // সেভ বাটন
        Button(
            onClick = {
                viewModel.savePersonality(selectedPrompt)
                onBackClick()
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Save Personality", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
