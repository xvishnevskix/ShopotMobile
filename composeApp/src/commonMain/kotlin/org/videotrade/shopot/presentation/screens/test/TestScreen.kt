package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.MusicPlayer
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel


class TestScreen : Screen {
    
    @Composable
    override fun Content() {
        // Создаем экземпляр MusicPlayer
        val musicPlayer = remember { MusicPlayer() }
        
        // Используем состояние для отслеживания, играет ли музыка
        var isPlaying by remember { mutableStateOf(false) }
        
        val scope = rememberCoroutineScope()
        
        MaterialTheme {
            SafeArea {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Кнопка для управления воспроизведением музыки
                    Button(
                        onClick = {
                  
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Меняем текст кнопки в зависимости от состояния
                        Text(if (isPlaying) "STOP" else "PLAY")
                    }
                }
            }
        }
    }
}

