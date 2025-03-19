package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import getImageStorage
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person


@Composable
fun MessageImage(
    message: MessageItem, profile: ProfileDTO,
    attachments: List<Attachment>,
    messageSenderName: String? = null
) {
    if (getPlatform() == Platform.Android) {
        val navigator = LocalNavigator.currentOrThrow
        
        val fileName = attachments[0].name
        val fileId = attachments[0].fileId
        
        val imagePainter = remember { mutableStateOf<Painter?>(null) }
        var imageFilePath = remember { mutableStateOf("") }
        
        imagePainter.value = getImageStorage(fileId, fileId, false).value
        
        
        if (imagePainter.value != null) {
            println("imagePainter.value asdada")
            Image(
                painter = imagePainter.value!!,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp, 350.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    ).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // Убирает эффект нажатия
                    ) {
                        if (imagePainter.value !== null)
                            navigateToScreen(navigator,
                                PhotoViewerScreen(
                                    imagePainter,
                                    messageSenderName,
                                    message.created
                                )
                            )
                    }
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.person), // Замените на ваш источник изображения
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(250.dp, 350.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    )
                    .blur(1000.dp) // Применяет блюр к изображению (значение можно настроить)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // Убирает эффект нажатия
                    ) {
                        if (imagePainter.value !== null)
                            navigateToScreen(navigator,
                                PhotoViewerScreen(
                                    imagePainter,
                                    messageSenderName,
                                    message.created
                                )
                            )
                    }
            )
        }
    } else {
        val imageFilePath = remember(attachments[0].fileId) { mutableStateOf("") }
        
        val imagePainter = rememberAsyncImagePainter(imageFilePath.value)
        
        val imageState = remember { mutableStateOf(imagePainter) }
        
        val navigator = LocalNavigator.current
        val url =
            "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"
        LaunchedEffect(attachments[0].fileId) {
            val fileId = attachments[0].fileId
            val fileType = attachments[0].type
            
            val fileProvider = FileProviderFactory.create()
            val existingFile =
                fileProvider.existingFileInDir(fileId, fileType)
            
            if (!existingFile.isNullOrBlank()) {
                imageFilePath.value = existingFile
                println("existingFile ${existingFile}")
            } else {
                val filePath = fileProvider.downloadCipherFile(
                    url,
                    "image",
                    fileId,
                    "image"
                ) { newProgress ->
                    println("newProgress $newProgress")
                }
                if (filePath != null) {
                    imageFilePath.value = filePath
                }
                println("filePath $filePath")
            }
        }
        
        Image(
            painter = imagePainter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp, 350.dp)
                .padding(
                    4.dp
                )
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                ).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                    if (imageFilePath.value.isNotBlank())
                        navigator?.let {
                            navigateToScreen(
                                it,
                                PhotoViewerScreen(
                                    imageState,
                                    messageSenderName,
                                )
                            )
                        }
                }
        )
    }
}


