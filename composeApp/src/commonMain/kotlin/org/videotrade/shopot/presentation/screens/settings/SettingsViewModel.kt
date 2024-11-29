package org.videotrade.shopot.presentation.screens.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage

class SettingsViewModel {
    private val _isDarkTheme = mutableStateOf(getThemeMode() == ThemeMode.DARK)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    fun toggleTheme() {
        val newThemeMode = if (_isDarkTheme.value) ThemeMode.LIGHT else ThemeMode.DARK
        _isDarkTheme.value = !_isDarkTheme.value
        saveThemeMode(newThemeMode)
    }
}

fun saveThemeMode(mode: ThemeMode) {
    addValueInStorage("theme_mode", mode.name)
}

fun getThemeMode(): ThemeMode {
    val mode = getValueInStorage("theme_mode") ?: ThemeMode.LIGHT.name
    return ThemeMode.valueOf(mode)
}