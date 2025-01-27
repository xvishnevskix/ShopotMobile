package org.videotrade.shopot.presentation.screens.settings

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage

class SettingsViewModel : ViewModel() {
    private val _isDarkTheme = mutableStateOf(getThemeMode() == ThemeMode.DARK)
    val isDarkTheme: State<Boolean> = _isDarkTheme

    private val _isScreenDimmed = MutableStateFlow(false)
    val isScreenDimmed: StateFlow<Boolean> = _isScreenDimmed.asStateFlow()


    fun toggleTheme() {
        val newThemeMode = if (_isDarkTheme.value) ThemeMode.LIGHT else ThemeMode.DARK
        _isDarkTheme.value = !_isDarkTheme.value
        saveThemeMode(newThemeMode)
    }

    fun setScreenDimmed(dimmed: Boolean) {
        viewModelScope.launch {
            _isScreenDimmed.emit(dimmed)
        }
    }
}

fun saveThemeMode(mode: ThemeMode) {
    addValueInStorage("theme_mode", mode.name)
}

fun getThemeMode(): ThemeMode {
    val mode = getValueInStorage("theme_mode") ?: ThemeMode.LIGHT.name
    return ThemeMode.valueOf(mode)




}