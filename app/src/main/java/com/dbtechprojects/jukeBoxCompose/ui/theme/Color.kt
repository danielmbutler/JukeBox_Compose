package com.dbtechprojects.jukeBoxCompose.ui.theme

import androidx.compose.ui.graphics.Color

val dialogColor = Color(0xFF5F5F5F)

sealed class ThemeColors(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val text: Color
) {
    object Night : ThemeColors(
        background = Color(0xFF000000),
        surface = Color(0xFF000000),
        primary = Color(0xFF4FB64C),
        text = Color(0xffffffff)
    )
    object Day : ThemeColors(
        background = Color(0XFFFFFFFF),
        surface = Color(0XFFFFFFFF),
        primary = Color(0xFFFFC107),
        text = Color(0xFF000000)
    )
}