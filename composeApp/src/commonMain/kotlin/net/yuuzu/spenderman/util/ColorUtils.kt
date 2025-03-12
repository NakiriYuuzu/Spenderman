package net.yuuzu.spenderman.util

import androidx.compose.ui.graphics.Color

/**
 * Parses a hex color string to a Color object.
 * Supports formats: #RRGGBB and #AARRGGBB
 */
fun parseColor(colorString: String): Color {
    val colorStr = colorString.trim()
    
    return try {
        when {
            colorStr.startsWith("#") && colorStr.length == 7 -> {
                // #RRGGBB format
                val r = colorStr.substring(1, 3).toInt(16)
                val g = colorStr.substring(3, 5).toInt(16)
                val b = colorStr.substring(5, 7).toInt(16)
                Color(r, g, b)
            }
            colorStr.startsWith("#") && colorStr.length == 9 -> {
                // #AARRGGBB format
                val a = colorStr.substring(1, 3).toInt(16)
                val r = colorStr.substring(3, 5).toInt(16)
                val g = colorStr.substring(5, 7).toInt(16)
                val b = colorStr.substring(7, 9).toInt(16)
                Color(r, g, b, a)
            }
            else -> {
                // Default to primary color
                Color.Gray
            }
        }
    } catch (e: Exception) {
        Color.Gray
    }
}
