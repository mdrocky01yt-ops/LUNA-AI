package com.luna.ai.feature.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.PremiumWhite

// ভয়েস প্রোফাইলের ডাটা ক্লাস
data class VoiceProfile(
    val name: String,
    val description: String
)

@Composable
fun VoiceChangerScreen(
    onBackClick: () -> Unit = {}
) {
    // ১২টি ভয়েসের লিস্ট (১০ মেয়ে, ২ ছেলে)
    val voiceProfiles = listOf(
        VoiceProfile("Luna Default", "Soft & Friendly"),
        VoiceProfile("Cute Girl", "Playful"),
        VoiceProfile("Professional", "Confident"),
        VoiceProfile("Relaxed", "Calm"),
        VoiceProfile("Energetic", "Excited"),
        VoiceProfile("Storyteller", "Slow & Clear"),
        VoiceProfile("Whisper", "Very Soft"),
        VoiceProfile("Sweet", "Pleasant"),
        VoiceProfile("Robot", "Mechanical"),
        VoiceProfile("Deep Voice", "Heavy"),
        VoiceProfile("Male Calm", "Calm Male"),
        VoiceProfile("Male Energetic", "Energetic Male")
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
            Text(text = "Voice Changer", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // লিস্ট ভিউ
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            items(voiceProfiles) { profile ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFF151520), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = profile.name, color = PremiumWhite, fontWeight = FontWeight.Bold)
                        Text(text = profile.description, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
