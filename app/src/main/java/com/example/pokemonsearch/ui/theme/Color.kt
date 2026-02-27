package com.example.pokemonsearch.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// 用于列表项背景色映射
fun parsePokemonColor(name: String?): Color {
    return when (name?.lowercase()) {
        "red" -> Color(0xFFFFCDD2)
        "blue" -> Color(0xFFBBDEFB)
        "green" -> Color(0xFFC8E6C9)
        "yellow" -> Color(0xFFFFF9C4)
        "purple" -> Color(0xFFE1BEE7)
        "pink" -> Color(0xFFF8BBD0)
        else -> Color(0xFFE0E0E0) // Gray fallback
    }
}