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
import getImageStorage
import org.videotrade.shopot.domain.model.MessageItem


@Composable
fun StickerMessage(
    message: MessageItem,
    imageId: String,
) {
    val imagePainter = getImageStorage(imageId, imageId, false)
    
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
