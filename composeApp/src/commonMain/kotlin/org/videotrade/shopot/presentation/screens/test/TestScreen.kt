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
                        val filePick = FileProviderFactory.create()
                            .pickFile(PickerType.File(listOf("mp4")))
                        
                        
                        println("${filePick?.fileAbsolutePath} filePath")

                        
                        filePick?.fileAbsolutePath?.let {
                            getAndSaveFirstFrame(it) { byteArray ->
                                println("byteArray $byteArray")
                                
                                imageBitmap = byteArray?.toImageBitmap()
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

