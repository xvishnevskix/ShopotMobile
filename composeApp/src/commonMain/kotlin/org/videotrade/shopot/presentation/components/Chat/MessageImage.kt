package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO


@Composable
fun MessageImage(message: MessageItem, profile: ProfileDTO) {
    
    val imagePainter =
        rememberImagePainter("${EnvironmentConfig.serverUrl}file/id/${message.attachments?.get(0)?.fileId}")
    
    
    
    Image(
        painter = imagePainter,
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
    )
}