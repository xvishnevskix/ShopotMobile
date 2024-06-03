package org.videotrade.shopot.presentation.screens.test

//import org.videotrade.shopot.multiplatform.MediaProviderFactory
//import org.videotrade.shopot.multiplatform.loadImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.launch
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.presentation.components.Common.SafeArea

class TestScreen : Screen {
    @Composable
    override fun Content() {
        MediaPickerSample()
    }
}

@Composable
fun MediaPickerSample() {
    val scope = rememberCoroutineScope()
    val byteArray = remember { mutableStateOf<ByteArray?>(null) }
    var images by remember { mutableStateOf<ImageBitmap?>(null) }
    
    
    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                // Process the selected images' ByteArrays.


                scope.launch {
                    origin().sendFile("file/upload", it, "image")
                }

                println(it)
                images = it.toImageBitmap()

                byteArray.value = it
            }
        }
    )
    
    
    
    SafeArea {
        Row {
            images?.let {
                Image(
                    modifier = Modifier
                        .size(220.dp),
                    bitmap = it,
                    
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Button(
                onClick = {
                    singleImagePicker.launch()
                    
                    
                }
            ) {
                Text("Pick Single Image")
            }
        }
        
    }
    
}
