package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DroidsDarkColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color.Black,
  primaryContainer = NeonBlueDark,
  onPrimaryContainer = Color.White,
  secondary = NeonAmber,
  onSecondary = Color.Black,
  secondaryContainer = NeonAmberLight,
  onSecondaryContainer = Color.Black,
  tertiary = NeonCrimson,
  background = CyberNavy,
  onBackground = Color.White,
  surface = CyberDarkSurface,
  onSurface = Color.White
)

@Composable
fun DroidsTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DroidsDarkColorScheme,
    typography = Typography,
    content = content
  )
}
