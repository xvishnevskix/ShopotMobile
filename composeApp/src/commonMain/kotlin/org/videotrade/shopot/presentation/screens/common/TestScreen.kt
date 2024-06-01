//package org.videotrade.shopot.presentation.screens.common
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.ImageBitmap
//import androidx.compose.ui.unit.dp
//import cafe.adriel.voyager.core.screen.Screen
//import kotlinx.coroutines.launch
//import org.videotrade.shopot.multiplatform.ContactsProviderFactory
//import org.videotrade.shopot.multiplatform.MediaProviderFactory
//import org.videotrade.shopot.multiplatform.loadImage
//
//class TestScreen : Screen {
//    @Composable
//    override fun Content() {
//        MediaPickerSample()
//    }
//}
//
//@Composable
//fun MediaPickerSample() {
//    val coroutineScope = rememberCoroutineScope()
//    var imageUri: String? by remember { mutableStateOf(null) }
//
//
//
//    Row {
//        imageUri?.let {
//            val imageBitmap = loadImage(it)
//            imageBitmap?.let { bitmap ->
//                Image(bitmap = bitmap, contentDescription = null)
//            }
//        }
//
//        Button(
//            onClick = {
//                try {
////                    val mediaProvider = MediaProviderFactory.create()
//
//                    coroutineScope.launch {
//
//
//
////                        val mediaUri = mediaProvider.getMedia()
////                        println("mediaUri $mediaUri")
////                        imageUri = mediaUri
//                    }
//                } catch (e: Exception) {
//                    println("Error: $e")
//                }
//            },
//            modifier = Modifier.size(200.dp)
//
//        ) {
//            Text(text = "Выбрать фото")
//        }
//    }
//}
package org.videotrade.shopot.presentation.screens.common

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
