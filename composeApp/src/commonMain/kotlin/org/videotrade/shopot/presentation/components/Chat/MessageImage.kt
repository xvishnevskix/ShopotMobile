package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Image
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
import coil3.compose.rememberAsyncImagePainter
import com.seiko.imageloader.rememberImagePainter
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory


@Composable
fun MessageImage(
    message: MessageItem, profile: ProfileDTO,
    attachments: List<Attachment>
) {
    var imageFilePath by remember { mutableStateOf("") }

//    val imagePainter =
//        rememberImagePainter("${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}")
    
    val imagePainter = rememberAsyncImagePainter(imageFilePath)
    
    
    LaunchedEffect(Unit) {
        val fileName = attachments[0].name
        val fileType = attachments[0].type
        
        val fileProvider = FileProviderFactory.create()
        val existingFile =
            fileProvider.existingFile(fileName, fileType)
        
        if (!existingFile.isNullOrBlank()) {
            imageFilePath = existingFile
            println("existingFile ${existingFile}")
        } else {
            val url =
                "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
            
            val filePath = fileProvider.downloadCipherFile(
                url,
                "image",
                fileName,
                "image"
            ) { newProgress ->
                println("newProgress $newProgress")
            }
            
            
            if (filePath != null) {
                imageFilePath = filePath
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