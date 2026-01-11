package com.example.weatherapp.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Color palette
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Weather-specific colors
val SunnyStart = Color(0xFFFDB813)
val SunnyEnd = Color(0xFFF59E0B)

val CloudyStart = Color(0xFF9CA3AF)
val CloudyEnd = Color(0xFF6B7280)

val RainyStart = Color(0xFF60A5FA)
val RainyEnd = Color(0xFF3B82F6)

val StormyStart = Color(0xFF7C3AED)
val StormyEnd = Color(0xFF5B21B6)

val SnowyStart = Color(0xFFA0CAF5)
val SnowyEnd = Color(0xFF3FB4F6)

val MistyStart = Color(0xFFD1D5DB)
val MistyEnd = Color(0xFF9CA3AF)

/**
 * Returns a gradient brush based on weather condition
 */
fun getWeatherGradient(weatherMain: String): Brush {
    return when (weatherMain.lowercase()) {
        "clear" -> Brush.linearGradient(
            colors = listOf(SunnyStart, SunnyEnd)
        )
        "clouds" -> Brush.linearGradient(
            colors = listOf(CloudyStart, CloudyEnd)
        )
        "rain", "drizzle" -> Brush.linearGradient(
            colors = listOf(RainyStart, RainyEnd)
        )
        "thunderstorm" -> Brush.linearGradient(
            colors = listOf(StormyStart, StormyEnd)
        )
        "snow" -> Brush.linearGradient(
            colors = listOf(SnowyStart, SnowyEnd)
        )
        "mist", "fog", "haze", "smoke" -> Brush.linearGradient(
            colors = listOf(MistyStart, MistyEnd)
        )
        else -> Brush.linearGradient(
            colors = listOf(SunnyStart, SunnyEnd)
        )
    }
}
