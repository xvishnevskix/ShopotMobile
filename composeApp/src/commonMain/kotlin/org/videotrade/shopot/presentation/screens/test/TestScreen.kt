package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import com.preat.peekaboo.image.picker.toImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.presentation.components.Common.SafeArea


class TestScreen : Screen {
    
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        
        MaterialTheme {
            SafeArea {
                
                Button(onClick = {
                    scope.launch {
                        val fileProvider = FileProviderFactory.create()
                        
                        val filePick = fileProvider
                            .pickFile(PickerType.File(listOf("mp4")))
                        
                        filePick?.fileAbsolutePath?.let {
                            getAndSaveFirstFrame(it) { photoName, photoPath, photoByteArray ->
                                println("byteArray $photoByteArray")
                                if (photoByteArray !== null && photoPath !== null && photoName !== null) {
                                    scope.launch {
                                        
                                        val fileId = fileProvider.uploadVideoFile(
                                            "file/upload/video",
                                            filePick.fileAbsolutePath,
                                            photoPath,
                                            "video",
                                            filePick.fileName,
                                            photoName
                                        ) {
                                            
                                            println("progress1 ${it / 100f}")
                                            
                                        }
                                    }
                                }
                                
                            }
                        }
                        
                        
                    }
                    
                }, content = {
                    Text("AAAAAA")
                })
                imageBitmap?.let {
                    Image(
                        modifier = Modifier
                            .size(220.dp),
                        bitmap = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                
                
            }
        }
    }
}

