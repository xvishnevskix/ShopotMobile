package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val commonViewModel: CommonViewModel = koinInject()
        val audioRecorder = remember { AudioFactory.createAudioRecorder() }
        val audioPlayer = remember { AudioFactory.createAudioPlayer() }
        
        var isRecording by remember { mutableStateOf(false) }
        var audioFilePath by remember { mutableStateOf("") }
        
        var text by remember { mutableStateOf(TextFieldValue("Text")) }
        val keyboardController = LocalSoftwareKeyboardController.current
   

        
        MaterialTheme {
            SafeArea {
                Column {
                    Button(content = {
                        Text("BUTTON")
                        
                    }, onClick = {
                    },
                        )
                    
                    TextField(
                        value = text,
                        onValueChange = {
                            text = it
                        },
                        label = { Text("Label") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {keyboardController?.hide()})
                    )
                }
            }
        }
    }
}


