package com.dbtechprojects.jukeBoxCompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColors(
    primary = ThemeColors.Night.primary,
    onPrimary = ThemeColors.Night.text,
    surface = ThemeColors.Night.surface,
    background = ThemeColors.Night.background
)

@Composable
fun JukeBoxComposeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        typography = Typography,
        content = content,
        colors = DarkColorScheme
    )
}