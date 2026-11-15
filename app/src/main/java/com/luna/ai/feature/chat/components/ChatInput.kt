
package com.luna.ai.feature.chat.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.ElectricPurple
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private fun calculateSampleSize(
    options: BitmapFactory.Options,
    maxSize: Int = 1280
): Int {

    val height = options.outHeight
    val width = options.outWidth

    var sampleSize = 1

    if (height > maxSize || width > maxSize) {

        var halfHeight = height / 2
        var halfWidth = width / 2

        while (
            halfHeight / sampleSize >= maxSize &&
            halfWidth / sampleSize >= maxSize
        ) {
            sampleSize *= 2
        }
    }

    return sampleSize
}

private suspend fun galleryImageToBase64(
    context: Context,
    uri: Uri
): String? = withContext(Dispatchers.IO) {

    try {

        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return@withContext null
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(bounds)
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val bitmap = context.contentResolver
            .openInputStream(uri)
            ?.use {
                BitmapFactory.decodeStream(it, null, options)
            } ?: return@withContext null

        val outputStream = ByteArrayOutputStream()

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            85,
            outputStream
        )

        bitmap.recycle()

        Base64.encodeToString(
            outputStream.toByteArray(),
            Base64.NO_WRAP
        )

    } catch (e: Exception) {

        null
    }
}

@Composable
fun ChatInput(
    onSendMessage: (String, String?) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var text by remember {
        mutableStateOf("")
    }

    var selectedImageBase64 by remember {
        mutableStateOf<String?>(null)
    }

    var isProcessingImage by remember {
        mutableStateOf(false)
    }

    // Gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {

            scope.launch {

                isProcessingImage = true

                selectedImageBase64 =
                    galleryImageToBase64(context, uri)

                isProcessingImage = false
            }
        }
    }

    // Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->

        if (bitmap != null) {

            scope.launch(Dispatchers.Default) {

                val encodedImage = try {

                    val outputStream = ByteArrayOutputStream()

                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        85,
                        outputStream
                    )

                    Base64.encodeToString(
                        outputStream.toByteArray(),
                        Base64.NO_WRAP
                    )

                } catch (e: Exception) {

                    null
                }

                withContext(Dispatchers.Main) {

                    selectedImageBase64 = encodedImage
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Camera
        IconButton(
            onClick = {
                cameraLauncher.launch(null)
            },
            modifier = Modifier
                .padding(end = 4.dp)
                .size(40.dp)
                .background(
                    Color(0xFF151520),
                    CircleShape
                )
        ) {

            Icon(
                Icons.Default.CameraAlt,
                contentDescription = "Camera",
                tint = ElectricPurple
            )
        }

        // Gallery
        IconButton(
            onClick = {
                if (!isProcessingImage) {
                    galleryLauncher.launch("image/*")
                }
            },
            modifier = Modifier
                .padding(end = 8.dp)
                .size(40.dp)
                .background(
                    Color(0xFF151520),
                    CircleShape
                )
        ) {

            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Gallery",
                tint = if (
                    selectedImageBase64 != null ||
                    isProcessingImage
                ) {
                    Color.Green
                } else {
                    ElectricPurple
                }
            )
        }

        // Text input
        TextField(
            value = text,
            onValueChange = {
                text = it
            },
            placeholder = {

                Text(
                    text = when {
                        isProcessingImage -> "ছবি প্রস্তুত হচ্ছে..."
                        selectedImageBase64 != null -> "ছবি প্রস্তুত..."
                        else -> "Type or scan..."
                    },
                    color = Color(0xFF888888),
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .background(
                    Color(0xFF151520),
                    RoundedCornerShape(24.dp)
                )
                .border(
                    1.dp,
                    Color(0x33FFFFFF),
                    RoundedCornerShape(24.dp)
                ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        // Send
        IconButton(
            enabled = !isProcessingImage,
            onClick = {

                if (
                    text.isNotBlank() ||
                    selectedImageBase64 != null
                ) {

                    onSendMessage(
                        text,
                        selectedImageBase64
                    )

                    text = ""
                    selectedImageBase64 = null
                }
            },
            modifier = Modifier
                .padding(start = 8.dp)
                .size(50.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8B5CF6),
                            Color(0xFF3B82F6)
                        )
                    ),
                    shape = CircleShape
                )
        ) {

            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}
