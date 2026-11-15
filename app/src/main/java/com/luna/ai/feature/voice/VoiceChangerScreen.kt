
package com.luna.ai.feature.voice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

data class VoiceProfile(
    val name: String,
    val description: String
)

@Composable
fun VoiceChangerScreen(
    onBackClick: () -> Unit = {},
    viewModel: VoiceChangerViewModel = hiltViewModel()
) {
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

    val savedVoiceName by viewModel.savedVoiceName.collectAsState()
    val savedPitch by viewModel.savedPitch.collectAsState()
    val savedSpeed by viewModel.savedSpeed.collectAsState()

    var selectedPitch by remember {
        mutableFloatStateOf(1.0f)
    }

    var selectedSpeed by remember {
        mutableFloatStateOf(1.0f)
    }

    LaunchedEffect(savedPitch) {
        selectedPitch = savedPitch
    }

    LaunchedEffect(savedSpeed) {
        selectedSpeed = savedSpeed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = PremiumWhite
                )
            }

            Text(
                text = "Voice Changer",
                color = PremiumWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "আপনার পছন্দের ভয়েস নির্বাচন করুন",
            color = Color.LightGray,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 12.dp,
                bottom = 8.dp
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(voiceProfiles) { profile ->
                val isSelected = savedVoiceName == profile.name

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) {
                                Color(0xFF242039)
                            } else {
                                Color(0xFF151520)
                            },
                            RoundedCornerShape(12.dp)
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(
                                    1.dp,
                                    CyanGlow,
                                    RoundedCornerShape(12.dp)
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable {
                            viewModel.saveVoiceSettings(
                                profile.name,
                                selectedPitch,
                                selectedSpeed
                            )
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = profile.name,
                            color = PremiumWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = profile.description,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = CyanGlow,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF151520))
                .padding(16.dp)
        ) {
            Text(
                text = "Voice Pitch: ${"%.1f".format(selectedPitch)}",
                color = PremiumWhite,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = selectedPitch,
                onValueChange = {
                    selectedPitch = it
                },
                valueRange = 0.5f..2.0f,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = CyanGlow,
                    activeTrackColor = ElectricPurple
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Voice Speed: ${"%.1f".format(selectedSpeed)}",
                color = PremiumWhite,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = selectedSpeed,
                onValueChange = {
                    selectedSpeed = it
                },
                valueRange = 0.5f..2.0f,
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = CyanGlow,
                    activeTrackColor = ElectricPurple
                )
            )

            Button(
                onClick = {
                    viewModel.saveVoiceSettings(
                        savedVoiceName,
                        selectedPitch,
                        selectedSpeed
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                )
            ) {
                Text("Save Voice Settings")
            }
        }
    }
}
