package com.example.wheelsonwheels.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// -------------------- COLORS --------------------

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A2DD5),
    onPrimary = Color.White,

    secondary = Color(0xFFFF6F00),
    onSecondary = Color.White,

    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF111111),

    surface = Color.White,
    onSurface = Color.Black,

    error = Color(0xFFB00020)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF42A5F5),
    onPrimary = Color.Black,

    secondary = Color(0xFFFFB74D),
    onSecondary = Color.Black,

    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,

    error = Color(0xFFCF6679)
)

// -------------------- TYPOGRAPHY --------------------

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
)

// -------------------- SHAPES --------------------

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

// -------------------- THEME --------------------

@Composable
fun WheelsOnWheelsTheme(
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    val colors = if (isDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}