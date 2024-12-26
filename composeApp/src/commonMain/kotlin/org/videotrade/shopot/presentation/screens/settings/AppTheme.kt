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
            inversePrimary = Color(0xFF373533) ,
            secondary = Color(0x80FFFFFF),
            onSecondary  = Color(0x4DFFFFFF),
            tertiary = Color(0x80FFFFFF),
            onTertiary = Color(0x80373533),
            background = Color(0xFF373533),
            surface = Color(0xFF373533),
            secondaryContainer = Color(0x1AFFFFFF),
            onBackground = Color(0x1AFFFFFF),
            error = Color(0xFFFF3B30),
            onPrimary = Color(0xFFBBA796),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF373533),
            inversePrimary = Color(0xFFFFFFFF),
            secondary = Color(0x80373533),
            onSecondary = Color(0x80373533),
            tertiary = Color(0xFFF7F7F7),
            onTertiary = Color(0xFFFFFFFF),
            background = Color(0xFFFFFFFF),
            surface = Color(0xFFf9f9f9),
            secondaryContainer  = Color(0x33373533),
            onBackground = Color(0xFFF7F7F7),
            error = Color(0xFFFF3B30),
            onPrimary = Color(0xFFEDDCCC),
        )
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}


