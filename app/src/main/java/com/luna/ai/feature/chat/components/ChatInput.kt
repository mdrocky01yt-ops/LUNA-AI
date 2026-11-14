package com.luna.ai.feature.chat.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.ElectricPurple

@Composable
fun ChatInput(
    onSendMessage: (String, Uri?) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // গ্যালারি বাটন
        IconButton(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier
                .padding(end = 8.dp)
                .size(45.dp)
                .background(Color(0xFF151520), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Pick Image",
                tint = if (selectedImageUri != null) Color.Green else ElectricPurple
            )
        }

        // ইনপুট বক্স
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { 
                Text(
                    text = if (selectedImageUri != null) "ছবি সিলেক্ট করা হয়েছে..." else "Type a message...", 
                    color = Color(0xFF888888), 
                    fontSize = 14.sp
                ) 
            },
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF151520), RoundedCornerShape(24.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // সেন্ড বাটন
        IconButton(
            onClick = {
                if (text.isNotBlank() || selectedImageUri != null) {
                    onSendMessage(text, selectedImageUri)
                    text = ""
                    selectedImageUri = null
                }
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(50.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF8B5CF6), Color(0xFF3B82F6))
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}
