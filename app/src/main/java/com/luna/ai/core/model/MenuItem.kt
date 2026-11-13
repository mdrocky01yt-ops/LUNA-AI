package com.luna.ai.core.model

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)
