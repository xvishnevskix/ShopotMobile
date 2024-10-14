package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.compose.rememberAsyncImagePainter
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.AnimatedSticker1
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.sticker1


@Composable
fun StickerMessage(
    message: MessageItem,
    attachments: List<Attachment>,
) {


    val imagePainter = rememberImagePainter("${serverUrl}file/plain/${attachments[0].fileId}")


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