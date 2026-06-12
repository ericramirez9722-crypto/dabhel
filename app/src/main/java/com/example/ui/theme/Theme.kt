package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF00F5D4), // CyberTeal
    onPrimary = Color(0xFF030712), // DeepBackground
    secondary = Color(0xFF00BBF9), // CyberCyan
    onSecondary = Color(0xFF030712),
    tertiary = Color(0xFF9D4EDD), // CyberPurple
    onTertiary = Color.White,
    background = Color(0xFF030712), // DeepBackground
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F172A), // CardBackground
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B), // CardBorder
    onSurfaceVariant = Color(0xFFA5F3FC),
    outline = Color(0xFF334155) // LightBorder
  )

private val LightColorScheme = DarkColorScheme // S.A.F. is a premium obsidian dark environment by design

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force visual coherence with dark theme
  dynamicColor: Boolean = false, // Disable Android dynamic wallpaper colors to enforce custom visual branding
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
