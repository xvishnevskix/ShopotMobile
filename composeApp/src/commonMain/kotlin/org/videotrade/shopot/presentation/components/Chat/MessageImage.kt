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
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
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
    val navigator = LocalNavigator.currentOrThrow
    
    val fileName = attachments[0].name
    val fileId = attachments[0].fileId
    
    val imagePainter = remember { mutableStateOf<Painter?>(null) }
    var imageFilePath = remember { mutableStateOf("") }
    
    imagePainter.value = if (getPlatform() == Platform.Android) {
        getImageStorage(fileId, fileId, false).value
    } else {
        
        LaunchedEffect(Unit) {
            val fileType = attachments[0].type
            
            val fileProvider = FileProviderFactory.create()
            
            val imageExist = fileProvider.existingFileInDir(fileId, "image")
            
            
            if (!imageExist.isNullOrBlank()) {
                imageFilePath.value = imageExist
                println("existingFile ${imageExist}")
            } else {
                
                println("imageExist $imageExist")
                
                val filePath = fileProvider.downloadCipherFile(
                    "${serverUrl}file/id/$fileId",
                    "image",
                    fileId,
                    "image"
                ) { _ -> }
                
                if (filePath != null) {
                    imageFilePath.value = filePath
                }
                
                println("filePath $filePath")
            }
            
        }
        
        
        rememberAsyncImagePainter(imageFilePath.value)
        
    }


//    val imageBitmap = remember(fileId) {
//        mutableStateOf<ImageBitmap?>(null)
//    }
//    LaunchedEffect(fileId) {
//        println("Loading image for fileId: $fileId")
//        imageBitmap.value = getImageStorage(fileId, fileName, true)
//    }
//


//    val navigator = LocalNavigator.current
    
    if (imagePainter.value != null) {
        println("imagePainter.value asdada")
        Image(
            painter = imagePainter.value!!,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp, 350.dp)
                .padding(7.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = if (message.fromUser == profile.id) 0.dp else 20.dp,
                        bottomStart = if (message.fromUser == profile.id) 20.dp else 0.dp,
                    )
                ).clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                    if (imagePainter.value !== null)
                        navigator.push(
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
                .padding(7.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomEnd = if (message.fromUser == profile.id) 0.dp else 20.dp,
                        bottomStart = if (message.fromUser == profile.id) 20.dp else 0.dp,
                    )
                )
                .blur(1000.dp) // Применяет блюр к изображению (значение можно настроить)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                    if (imagePainter.value !== null)
                        navigator.push(
                            PhotoViewerScreen(
                                imagePainter,
                                messageSenderName,
                                message.created
                            )
                        )
                }
        )
    }
}