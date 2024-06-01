package org.videotrade.shopot.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import org.videotrade.shopot.multiplatform.MediaProviderFactory
import org.videotrade.shopot.multiplatform.loadImage

class TestScreen : Screen {
    @Composable
    override fun Content() {
        MediaPickerSample()
    }
}

@Composable
fun MediaPickerSample() {
    val coroutineScope = rememberCoroutineScope()
    var imageUri: String? by remember { mutableStateOf(null) }
    

    
    Row {
        imageUri?.let {
            val imageBitmap = loadImage(it)
            imageBitmap?.let { bitmap ->
                Image(bitmap = bitmap, contentDescription = null)
            }
        }
        
        Button(
            onClick = {
                try {
                    val mediaProvider = MediaProviderFactory.create()
                    
                    coroutineScope.launch {
                        val mediaUri = mediaProvider.getMedia()
                        println("mediaUri $mediaUri")
                        imageUri = mediaUri
                    }
                } catch (e: Exception) {
                    println("Error: $e")
                }
            },
            modifier = Modifier.size(200.dp)
        
        ) {
            Text(text = "Выбрать фото")
        }
    }
}
