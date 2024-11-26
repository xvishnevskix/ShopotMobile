package org.videotrade.shopot.presentation.screens.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFFFFFFF),
            secondary = Color(0x80FFFFFF),
            background = Color(0xFF373533),
            surface = Color(0xFF373533),
            onPrimary = Color(0xFF000000),
            onSecondary = Color(0x1AFFFFFF),
            onBackground = Color(0x1AFFFFFF),
            onSurface = Color(0xFFFFFFFF),
            error = Color(0xFFFF3B30)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF373533),
            secondary = Color(0x80373533),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFf9f9f9),
            onPrimary = Color(0xFFFFFFFF),
            onSecondary = Color(0x33373533),
            onBackground = Color(0xFFF7F7F7),
            onSurface = Color(0xFF000000),
            error = Color(0xFFFF3B30)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


