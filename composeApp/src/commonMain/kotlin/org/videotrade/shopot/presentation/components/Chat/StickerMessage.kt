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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import avatarCache
import com.preat.peekaboo.image.picker.toImageBitmap
import getImageStorage
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.imageAsync


@Composable
fun StickerMessage(
    message: MessageItem,
    imageId: String,
) {
    
    val imageBitmap = remember(imageId) {
        mutableStateOf<ImageBitmap?>(null)
    }
    
    LaunchedEffect(imageId) {
        imageBitmap.value = getImageStorage(imageId, imageId, false)
    }
    
    
    if (imageBitmap.value !== null)
        Image(
            bitmap = imageBitmap.value!!,
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
