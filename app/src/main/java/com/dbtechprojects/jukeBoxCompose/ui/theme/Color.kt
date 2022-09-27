package com.dbtechprojects.jukeBoxCompose.ui.theme

import androidx.compose.ui.graphics.Color

val turntableBackground = Color(0xFF4FB64C)

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val text: Color
) {
    object Night : ThemeColors(
        background = Color(0xFF0C3C98),
        surface = Color(0xFF1635D1),
        primary = Color(0xFF172840),
        text = Color(0xffffffff)
    )
}