package com.luna.ai.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LunaDarkColorScheme = darkColorScheme(
    primary = ElectricPurple,
    onPrimary = PremiumWhite,
    secondary = ElectricBlue,
    onSecondary = PremiumWhite,
    tertiary = CyanGlow,
    background = DeepBlack,
    onBackground = PremiumWhite,
    surface = DeepBlack,
    onSurface = PremiumWhite,
    error = WarningRed
)

@Composable
fun LunaAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LunaDarkColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LunaTypography,
        content = content
    )
}
