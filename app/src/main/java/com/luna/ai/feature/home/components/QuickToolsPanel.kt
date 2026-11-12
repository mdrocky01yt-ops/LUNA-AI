package com.luna.ai.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.ElectricBlue
import com.luna.ai.design.theme.PremiumWhite
import com.luna.ai.design.theme.SuccessGreen
import com.luna.ai.design.theme.WarningRed

@Composable
fun QuickToolsPanel(
    onTasksClick: () -> Unit = {},
    onAppsClick: () -> Unit = {},
    onYoutubeClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(Color(0x66000000), RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ToolItem(icon = Icons.Default.Apps, label = "Apps", color = ElectricBlue, onClick = onAppsClick)
        ToolItem(icon = Icons.Default.PlayArrow, label = "YouTube", color = WarningRed, onClick = onYoutubeClick)
        ToolItem(icon = Icons.Default.Task, label = "Tasks", color = SuccessGreen, onClick = onTasksClick)
        ToolItem(icon = Icons.Default.MoreHoriz, label = "More", color = PremiumWhite, onClick = onMoreClick)
    }
}

@Composable
fun ToolItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0x33FFFFFF), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            color = PremiumWhite,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
