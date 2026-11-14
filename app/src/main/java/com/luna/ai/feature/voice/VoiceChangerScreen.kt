package com.luna.ai.feature.voice

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite
import com.luna.ai.voice.VoiceManager

// ডাটা ক্লাস এই ফাইলের ভেতরেই ডিক্লেয়ার করা হলো
data class VoiceProfile(
    val name: String,
    val pitch: Float,
    val speed: Float,
    val description: String
)

@Composable
fun VoiceChangerScreen(
    onBackClick: () -> Unit = {},
    viewModel: VoiceChangerViewModel = hiltViewModel()
) {
    val savedName by viewModel.savedVoiceName.collectAsState()
    val savedPitch by viewModel.savedPitch.collectAsState()
    val savedSpeed by viewModel.savedSpeed.collectAsState()

    var selectedVoice by remember { mutableStateOf(savedName) }
    var pitch by remember { mutableStateOf(savedPitch) }
    var speed by remember { mutableStateOf(savedSpeed) }
    
    val context = LocalContext.current
    val voiceManager = remember { VoiceManager(context) }

    // ১২টি কণ্ঠস্বরের লিস্ট
    val voiceProfiles = listOf(
        VoiceProfile("Luna Default", 1.0f, 1.0f, "Soft & Friendly"),
        VoiceProfile("Cute Girl", 1.3f, 1.1f, "Playful"),
        VoiceProfile("Professional", 0.9f, 1.0f, "Confident"),
        VoiceProfile("Relaxed", 0.95f, 0.9f, "Calm"),
        VoiceProfile("Energetic", 1.1f, 1.2f, "Excited"),
        VoiceProfile("Storyteller", 0.9f, 0.95f, "Slow & Clear"),
        VoiceProfile("Whisper", 0.85f, 0.8f, "Very Soft"),
        VoiceProfile("Sweet", 1.2f, 1.0f, "Pleasant"),
        VoiceProfile("Robot", 0.7f, 1.1f, "Mechanical"),
        VoiceProfile("Deep Voice", 0.6f, 0.9f, "Heavy"),
        VoiceProfile("Male Calm", 0.8f, 0.9f, "Calm Male"),
        VoiceProfile("Male Energetic", 0.85f, 1.1f, "Energetic Male")
    )

    Column(
        modifier = Modifier.fillMaxSize().background(DeepBlack).padding(top = 24.dp)
    ) {
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

        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(voiceProfiles) { profile ->
                val isSelected = selectedVoice == profile.name
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) ElectricPurple.copy(alpha = 0.3f) else Color(0xFF151520),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            selectedVoice = profile.name
                            pitch = profile.pitch
                            speed = profile.speed
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = profile.name, color = if (isSelected) CyanGlow else PremiumWhite, fontWeight = FontWeight.Bold)
                        Text(text = profile.description, color = Color.Gray, fontSize = 12.sp)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            voiceManager.updateVoiceParameters(profile.pitch, profile.speed, profile.name)
                            voiceManager.speak("হ্যালো, আমি লুনা।")
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Preview", tint = CyanGlow)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = CyanGlow)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp).background(Color(0xFF151520), RoundedCornerShape(16.dp)).padding(16.dp)) {
            Text(text = "Speed: ${String.format("%.1f", speed)}x", color = PremiumWhite, fontSize = 14.sp)
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(thumbColor = CyanGlow, activeTrackColor = ElectricPurple)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Pitch: ${String.format("%.1f", pitch)}x", color = PremiumWhite, fontSize = 14.sp)
            Slider(
                value = pitch,
                onValueChange = { pitch = it },
                valueRange = 0.5f..2.0f,
                colors = SliderDefaults.colors(thumbColor = CyanGlow, activeTrackColor = ElectricPurple)
            )
        }

        Button(
            onClick = {
                viewModel.saveVoiceSettings(selectedVoice, pitch, speed)
                onBackClick()
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Apply Changes", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
