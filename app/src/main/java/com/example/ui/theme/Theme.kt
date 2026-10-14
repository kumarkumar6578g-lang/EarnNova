package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
  primary = Emerald600,
  onPrimary = PureWhite,
  primaryContainer = Emerald100,
  onPrimaryContainer = Emerald900,
  secondary = Gold600,
  onSecondary = PureWhite,
  secondaryContainer = Gold100,
  onSecondaryContainer = Gold700,
  tertiary = Slate700,
  onTertiary = PureWhite,
  background = Slate50,
  onBackground = Slate900,
  surface = PureWhite,
  onSurface = Slate900,
  surfaceVariant = Slate100,
  onSurfaceVariant = Slate700,
  outline = Slate200,
  error = Rose600,
  onError = PureWhite,
  errorContainer = Rose100,
  onErrorContainer = Rose600
)

private val DarkColorScheme = darkColorScheme(
  primary = Emerald400,
  onPrimary = Emerald900,
  primaryContainer = Emerald800,
  onPrimaryContainer = Emerald100,
  secondary = Gold400,
  onSecondary = Slate900,
  secondaryContainer = Gold700,
  onSecondaryContainer = Gold100,
  tertiary = Slate400,
  onTertiary = Slate900,
  background = Slate900,
  onBackground = Slate50,
  surface = Slate800,
  onSurface = Slate50,
  surfaceVariant = Slate700,
  onSurfaceVariant = Slate200,
  outline = Slate600,
  error = Rose600,
  onError = PureWhite,
  errorContainer = Rose600,
  onErrorContainer = Rose100
)

@Composable
fun EarnNovaTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) = EarnNovaTheme(darkTheme = darkTheme, content = content)
