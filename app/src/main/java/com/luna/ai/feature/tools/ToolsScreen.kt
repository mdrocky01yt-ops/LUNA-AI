package com.luna.ai.feature.tools

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.luna.ai.core.device.DeviceController
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.PremiumWhite

data class ToolItemData(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ToolsScreen(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val deviceController = remember { DeviceController(context) }
    var batteryLevel by remember { mutableStateOf(deviceController.getBatteryLevel()) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) deviceController.toggleFlashlight()
        else deviceController.showToast("Camera permission required")
    }

    val tools = listOf(
        ToolItemData("YouTube", Icons.Default.PlayArrow, Color.Red),
        ToolItemData("AI Search", Icons.Default.Search, Color(0xFF3B82F6)),
        ToolItemData("Translation", Icons.Default.Translate, Color(0xFF8B5CF6)),
        ToolItemData("Calculator", Icons.Default.Calculate, Color(0xFF10B981)),
        ToolItemData("Weather", Icons.Default.WbSunny, Color(0xFFF59E0B)),
        ToolItemData("Flashlight", Icons.Default.FlashOn, Color(0xFFEF4444)),
        ToolItemData("Battery", Icons.Default.BatteryChargingFull, Color(0xFF22D3EE))
    )

    Box(
        modifier = Modifier.fillMaxSize().background(DeepBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0A0A10)).padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(text = "AI Tools", color = PremiumWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tools) { tool ->
                    Column(
                        modifier = Modifier
                            .background(Color(0xFF151520), RoundedCornerShape(16.dp))
                            .clickable {
                                when (tool.name) {
                                    "YouTube" -> {
                                        // ইউটিউব ওপেন করা
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com"))
                                        context.startActivity(intent)
                                    }
                                    "Flashlight" -> {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                            deviceController.toggleFlashlight()
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                    "Battery" -> {
                                        batteryLevel = deviceController.getBatteryLevel()
                                        deviceController.showToast("Battery: $batteryLevel%")
                                    }
                                    else -> deviceController.showToast("Coming soon: ${tool.name}")
                                }
                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(50.dp).background(tool.color.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = tool.icon, contentDescription = tool.name, tint = tool.color, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = tool.name, color = PremiumWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}
