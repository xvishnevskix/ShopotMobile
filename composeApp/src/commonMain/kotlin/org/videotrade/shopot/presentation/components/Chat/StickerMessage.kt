package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import getImageStorage
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform


@Composable
fun StickerMessage(
    message: MessageItem,
    imageId: String,
) {
    
    val imagePainter = remember { mutableStateOf<Painter?>(null) }
    
    
    if (getPlatform() == Platform.Android) {
        LaunchedEffect(imageId) {
            val newImageBitmap = getImageStorage(imageId, imageId, false)
            imagePainter.value = newImageBitmap?.let { BitmapPainter(it) }
        }
    } else {
        imagePainter.value = rememberImagePainter(url = "${serverUrl}file/plain/$imageId")
    }
    
    if (imagePainter.value !== null)
        Image(
            painter = imagePainter.value!!,
            contentDescription = "Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(150.dp, 150.dp)
                .padding(7.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Убирает эффект нажатия
                ) {
                }
        )
}
