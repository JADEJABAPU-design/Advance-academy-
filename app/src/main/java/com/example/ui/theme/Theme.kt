package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = AcademyBlueLight,
    onPrimary = Color.White,
    primaryContainer = AcademyBlue,
    onPrimaryContainer = Color.White,
    secondary = AcademyGoldLight,
    onSecondary = Color.Black,
    secondaryContainer = AcademyGold,
    onSecondaryContainer = Color.White,
    tertiary = AcademyEmeraldLight,
    onTertiary = Color.Black,
    background = Color(0xFF0B1329),
    surface = Color(0xFF131D3B),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
    error = AcademyCrimsonLight,
    onError = Color.Black
  )

private val LightColorScheme =
  lightColorScheme(
    primary = AcademyBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = AcademyGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = AcademyEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = SurfaceLight,
    surface = Color.White,
    onBackground = NeutralDark,
    onSurface = NeutralDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = NeutralMedium,
    outline = CardBorderLight,
    error = AcademyCrimson,
    onError = Color.White
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
