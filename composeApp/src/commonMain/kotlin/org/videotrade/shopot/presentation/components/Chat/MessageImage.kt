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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import getImageStorage
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person


@Composable
fun MessageImage(
    message: MessageItem, profile: ProfileDTO,
    attachments: List<Attachment>,
    messageSenderName: String? = null
) {
//    var imageFilePath by remember { mutableStateOf("") }
    val fileName = attachments[0].name
    val fileId = attachments[0].fileId
    
    val imageBitmap = remember(fileId) {
        mutableStateOf<ImageBitmap?>(null)
    }
// val imagePainter =
// rememberImagePainter("${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}")
//    val imagePainter = rememberAsyncImagePainter(imageFilePath)


//    val imagePainter = getImageStorage(attachments[0].fileId, fileName, true)
    
    LaunchedEffect(fileId) {
        imageBitmap.value = getImageStorage(fileId, fileName, true)
    }
    
    val navigator = LocalNavigator.current
//    val url =
//        "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
//    LaunchedEffect(Unit) {
//        val fileType = attachments[0].type
//        val fileProvider = FileProviderFactory.create()
//        val existingFile =
//            fileProvider.existingFile(fileName, fileType)
//        if (!existingFile.isNullOrBlank()) {
//            imageFilePath = existingFile
//            println("existingFile ${existingFile}")
//        } else {
//            val filePath = fileProvider.downloadCipherFile(
//                url,
//                "image",
//                fileName,
//                "image"
//            ) { newProgress ->
//                println("newProgress $newProgress")
//            }
//            if (filePath != null) {
//                imageFilePath = filePath
//            }
//            println("filePath $filePath")
//        }
//    }
    if (imageBitmap.value != null) {
        Image(
            bitmap = imageBitmap.value!!,
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
                    if (imageBitmap.value !== null)
                        navigator?.push(
                            PhotoViewerScreen(
                                imageBitmap.value!!,
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
                    if (imageBitmap.value !== null)
                        navigator?.push(
                            PhotoViewerScreen(
                                imageBitmap.value!!,
                                messageSenderName,
                                message.created
                            )
                        )
                }
        )
    }
}