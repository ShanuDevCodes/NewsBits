@file:Suppress("DEPRECATION")

package com.shanudevcodes.newsbits.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Yellow,
    secondary = Blue,
    tertiary = Red,
    surface = Black90,
    surfaceContainerLow = Black80,
    surfaceContainerHigh = Black80,
    secondaryContainer = Color(0xFFFFD18A).copy(alpha = 0.5f),
    onSecondaryContainer = Color(0xFF3A2600)

)

private val LightColorScheme = lightColorScheme(
    primary = Yellow,
    secondary = Blue,
    tertiary = Red,
    surface = White100,
    surfaceContainerLow = White80,
    surfaceContainerHigh = White80,
    secondaryContainer = Color(0xFFFFD18A).copy(alpha = 0.5f),
    onSecondaryContainer = Color(0xFF3A2600)
)

@Composable
fun NewsBitsTheme(
    themeOption: ThemeOptions,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeOption) {
        ThemeOptions.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        ThemeOptions.LIGHT -> false
        ThemeOptions.DARK -> true
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set status bar color to match your app theme
            window.statusBarColor = colorScheme.surface.toArgb()

            // Set navigation bar color to match your app theme
            window.navigationBarColor = colorScheme.surface.toArgb()

            // Make icons dark/light based on YOUR app theme, not system theme
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkTheme
                isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}