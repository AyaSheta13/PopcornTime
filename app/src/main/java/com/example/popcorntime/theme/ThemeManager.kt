package com.example.popcorntime.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// استخدم CompositionLocal لنقل حالة السمة
val LocalThemeManager = staticCompositionLocalOf { ThemeManager }

object ThemeManager {
    val isDarkTheme = mutableStateOf(true)

    // ألوان السمة الداكنة
    val darkColors = AppColors(
        primary = Color(0xFF9B27B0),
        background = Color(0xFF100A10),
        surface = Color(0xFF1E131F),
        cardBackground = Color(0xFF1E131F),
        onBackground = Color.White,
        onSurface = Color.White,
        onCard = Color.White,
        secondary = Color(0xFFA0A0A0)
    )

    // ألوان السمة الفاتحة
    val lightColors = AppColors(
        primary = Color(0xFF9B27B0),
        background = Color(0xFFF5F5F5),
        surface = Color(0xFFFFFFFF),
        cardBackground = Color(0xFFFFFFFF),
        onBackground = Color(0xFF333333),
        onSurface = Color(0xFF333333),
        onCard = Color(0xFF333333),
        secondary = Color(0xFF666666)
    )

    val currentColors: AppColors
        @Composable get() = if (isDarkTheme.value) darkColors else lightColors

    fun toggleTheme() {
        isDarkTheme.value = !isDarkTheme.value
    }

    fun setDarkTheme(dark: Boolean) {
        isDarkTheme.value = dark
    }
}

data class AppColors(
    val primary: Color,
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onCard: Color,
    val secondary: Color
)