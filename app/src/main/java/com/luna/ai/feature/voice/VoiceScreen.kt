package com.luna.ai.feature.voice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.luna.ai.core.model.AIState
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite
import com.luna.ai.feature.home.components.LunaAvatar

@Composable
fun VoiceScreen(
    onBackClick: () -> Unit = {},
    viewModel: VoiceViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val userQuery by viewModel.userQuery.collectAsState()
    val lunaResponse by viewModel.lunaResponse.collectAsState()

    // মাইক্রোফোন পারমিশন রিকোয়েস্ট
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    val startListeningWithPermission = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.startListening()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // এভাটারের স্টেট নির্ধারণ
    val aiState = when {
        isListening -> AIState.LISTENING
        isSpeaking -> AIState.SPEAKING
        lunaResponse == "Thinking..." -> AIState.THINKING
        else -> AIState.IDLE
    }

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
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // টপ বার
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(text = "Voice Assistant", color = PremiumWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(48.dp)) // ব্যালেন্সের জন্য
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // এভাটার
            LunaAvatar(aiState = aiState)

            Spacer(modifier = Modifier.height(32.dp))

            // ইউজার যা বলেছে
            if (userQuery.isNotEmpty()) {
                Text(
                    text = "\"$userQuery\"",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // লুনার উত্তর বা স্টেটাস
            Text(
                text = lunaResponse,
                color = if (isListening) CyanGlow else Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // কন্ট্রোল বাটন (Stop এবং Mic)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // স্টপ বাটন
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(0x33FFFFFF), CircleShape)
                        .clickable { 
                            viewModel.stopListening()
                            onBackClick() 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Stop", tint = Color.White)
                }

                Spacer(modifier = Modifier.size(32.dp))

                // মাইক বাটন
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(
                            brush = if (isListening) Brush.linearGradient(listOf(CyanGlow, Color.Blue)) 
                                    else Brush.linearGradient(listOf(ElectricPurple, Color.Blue)),
                            shape = CircleShape
                        )
                        .clickable { 
                            if (isListening) viewModel.stopListening() 
                            else startListeningWithPermission() 
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}
