package com.example.skilllink.ui.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Brush.toBrushColor(): Color {
    // Compose Button doesn't support gradient directly, so fallback to solid color for now
    return Color(0xFFB31217)
} 