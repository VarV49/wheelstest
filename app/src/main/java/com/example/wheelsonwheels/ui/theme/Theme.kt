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

object AppColors {
    val RedPrimary  = Color(0xFFE5001C)
    val RedMuted    = Color(0xFF8C0011)
    val BlackDeep   = Color(0xFF0A0A0A)
    val BlackCard   = Color(0xFF141414)
    val BlackBorder = Color(0xFF242424)
    val OffWhite    = Color(0xFFF0EDE8)
    val GrayMuted   = Color(0xFF888888)
}

private val DarkColors = darkColorScheme(
    primary            = AppColors.RedPrimary,
    onPrimary          = Color.White,

    secondary          = AppColors.RedMuted,
    onSecondary        = Color.White,

    background         = AppColors.BlackDeep,
    onBackground       = AppColors.OffWhite,

    surface            = AppColors.BlackCard,
    onSurface          = AppColors.OffWhite,

    surfaceVariant     = AppColors.BlackBorder,
    onSurfaceVariant   = AppColors.GrayMuted,

    outline            = AppColors.BlackBorder,

    error              = Color(0xFFFF3B3B),
    onError            = Color.White
)

// Light mode mirrors the dark scheme but brightened —
// keeps the red brand identity on white backgrounds.
private val LightColors = lightColorScheme(
    primary            = AppColors.RedPrimary,
    onPrimary          = Color.White,

    secondary          = AppColors.RedMuted,
    onSecondary        = Color.White,

    background         = Color(0xFFF5F2EF),
    onBackground       = Color(0xFF0D0D0D),

    surface            = Color(0xFFD0D0D0),
    onSurface          = Color(0xFF0D0D0D),

    surfaceVariant     = Color(0xFFEAE7E4),
    onSurfaceVariant   = Color(0xFF666666),

    outline            = Color(0xFFD8D5D2),

    error              = Color(0xFFB00020),
    onError            = Color.White
)

// -------------------- TYPOGRAPHY --------------------

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    ),
    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontSize = 13.sp,
    fontWeight = FontWeight.W600,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.W700,
        letterSpacing = 2.sp  // matches the ALL-CAPS section labels
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
