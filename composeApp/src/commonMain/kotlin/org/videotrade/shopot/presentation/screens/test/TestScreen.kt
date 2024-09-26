package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.preat.peekaboo.image.picker.toImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.VideoPlayer
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.presentation.components.Common.SafeArea


class TestScreen : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        var imagePath by remember { mutableStateOf<String?>(null) }

        MaterialTheme {
            SafeArea {

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        if (imagePath !== null) VideoPlayer(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            filePath =
                            imagePath!!
                        )

                        Button(onClick = {
                            scope.launch {
                                val fileProvider = FileProviderFactory.create()


                                fileProvider.pickGallery()

//                                val filePick = fileProvider
//                                    .pickFile(PickerType.File(listOf("mp4","H.264", "MOV", "MPEG-4")))
//
//                                filePick?.fileAbsolutePath?.let {
//                                    getAndSaveFirstFrame(it) { photoName, photoPath, photoByteArray ->
//                                        println("filePick.fileAbsolutePath ${filePick.fileAbsolutePath}")
//                                        if (photoByteArray !== null && photoPath !== null && photoName !== null) {
//                                            scope.launch {
//                                                imagePath = filePick.fileAbsolutePath
//                                            }
//                                        }
//
//                                    }
//                                }
//

                            }

                        }, content = {
                            Text("AAAAAA")
                        })
                    }
                }
            }
        }
    }
}

