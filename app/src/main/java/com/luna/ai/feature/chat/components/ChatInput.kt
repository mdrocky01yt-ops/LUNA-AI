package com.luna.ai.feature.chat.components

import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
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
import androidx.compose.material.icons.filled.CameraAlt
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
import java.io.ByteArrayOutputStream

@Composable
fun ChatInput(
    onSendMessage: (String, String?) -> Unit // (Text, Base64Image)
) {
    var text by remember { mutableStateOf("") }
    var selectedImageBase64 by remember { mutableStateOf<String?>(null) }

    // গ্যালারি থেকে ছবি নেওয়া
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Uri থেকে Base64 বানানো (এখানে সরলীকৃত করা হলো, বড় ছবি হলে সমস্যা হতে পারে, তবে Gemini সাপোর্ট করবে)
            selectedImageBase64 = "gallery_selected" // আপাতত ফ্ল্যাগ
        }
    }

    // ক্যামেরা দিয়ে ছবি তোলা
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            selectedImageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ক্যামেরা বাটন
        IconButton(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier
                .padding(end = 4.dp)
                .size(40.dp)
                .background(Color(0xFF151520), CircleShape)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = ElectricPurple)
        }

        // গ্যালারি বাটন
        IconButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
                .background(Color(0xFF151520), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Gallery",
                tint = if (selectedImageBase64 != null) Color.Green else ElectricPurple
            )
        }

        // ইনপুট বক্স
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { 
                Text(
                    text = if (selectedImageBase64 != null) "ছবি রেডি..." else "Type or scan...", 
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
                if (text.isNotBlank() || selectedImageBase64 != null) {
                    onSendMessage(text, selectedImageBase64)
                    text = ""
                    selectedImageBase64 = null
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
            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}
