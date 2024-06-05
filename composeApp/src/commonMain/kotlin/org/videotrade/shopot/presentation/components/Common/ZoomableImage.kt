package org.videotrade.shopot.presentation.components.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.launch

@Composable
fun ZoomableImage() {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val scope = rememberCoroutineScope()
    
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                imageBitmap = it.toImageBitmap()
            }
        }
    )
    
    LaunchedEffect(key1 = Unit) {
        this.launch {
            singleImagePicker.launch()
        }
    }
    
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ -> // Удалите параметр pan
                    scale *= zoom
                    // Удалите строку: offset = Offset(x = offset.x + pan.x, y = offset.y + pan.y)
                }
            }
    ) {
        imageBitmap?.let { img ->
            Image(
                bitmap = img,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = maxOf(1f, minOf(3f, scale)),
                        scaleY = maxOf(1f, minOf(3f, scale))
                        // Удалите строки: translationX = offset.x, translationY = offset.y
                    )
            )
        }
    }
}
