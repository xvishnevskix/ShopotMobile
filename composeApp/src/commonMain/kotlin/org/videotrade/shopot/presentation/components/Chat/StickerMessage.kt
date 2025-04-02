package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import getImageStorage
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem


@Composable
fun StickerMessage(
    message: MessageItem,
    imageId: String,
    chat: ChatItem
) {
//    val imagePainter = getImageStorage(imageId, imageId, false)
    
    val imagePainter = rememberAsyncImagePainter("${SERVER_URL}file/plain/$imageId")
    
    
//    if (imagePainter.value !== null)
        Image(
            painter = imagePainter,
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
