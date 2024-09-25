import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_download
import shopot.composeapp.generated.resources.chat_play


@Composable
fun VideoMessage(
    message: MessageItem,
    attachments: List<Attachment>,
) {
    val scope = rememberCoroutineScope()
    
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    val downloadProgress = viewModel.downloadProgress.collectAsState().value
    
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingSuccess by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var filePath by remember { mutableStateOf("") }
    var isBlurred by remember { mutableStateOf(true) }
    var isStartCipherLoading by remember { mutableStateOf(false) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 30)
    )
    
    
    LaunchedEffect(message) {
        
        if (message.upload !== null) {
            downloadJob?.cancel()
            progress = 0f
            isLoading = true
            
            
            downloadJob = scope.launch {
                isLoading = true
                isStartCipherLoading = true
                message.attachments?.get(0)?.let { attachment ->
                    
                    println("adasdada ${attachment.name} ${attachment.type}")
                    
                    val fileId = FileProviderFactory.create().uploadVideoFile(
                        "file/upload/video",
                        attachment.originalFileDir!!,
                        attachment.photoPath!!,
                        attachment.type,
                        attachment.name,
                        attachment.photoName!!
                    ) {
                        isStartCipherLoading = false
                        
                        println("progress1 ${it / 100f}")
                        
                        progress = it / 100f
                    }
                    
                    
                    if (fileId !== null) {
                        println("fileId ${fileId}")
                        viewModel.sendLargeFileAttachments(
                            message.content,
                            message.fromUser,
                            message.chatId,
                            message.uploadId!!,
                            fileId
                        )
                    }
                    
                    
                }
                
                isLoading = false
                progress = 1f
                isLoading = false
                isLoadingSuccess = true
                isBlurred = false
            }
            
            return@LaunchedEffect
        }


//        println("fileId ${message.attachments?.get(0)?.fileId}")
//        val url = "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
//        val fileName = attachments[0].name
//        println("fileName $fileName")
//
//        val existingFile = audioFile.existingFile(fileName, attachments[0].type)
//
//        if (!existingFile.isNullOrBlank()) {
//            isLoadingSuccess = true
//            downloadJob?.cancel()
//            isLoading = false
//            progress = 1f
//            filePath = existingFile
//        }
    }
    
    
    Box(
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Remove click effect
            ) {
                if (!isLoading && !isLoadingSuccess) {
                    
                    isLoading = true
                    isBlurred = true
                    
                    downloadJob = scope.launch {
                        for (i in 1..100) {
                            delay(40)
                            progress = i / 99f
                        }
                        isLoading = false
                        isLoadingSuccess = true
                        isBlurred = false
                    }
                }
            }
    ) {
        attachments[0].photoByteArray?.toImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (isBlurred) 16.dp else 0.dp)
            )
        }
        
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(45.dp).align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.fillMaxSize(),
                    strokeCap = StrokeCap.Round
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    modifier = Modifier
                        .padding()
                        .clickable {
                            
                            downloadJob?.cancel()
                            isLoading = false
                            isLoadingSuccess = false
                            isBlurred = true
                            progress = 0f
                        },
                    tint = Color.White
                )
            }
        } else if (isLoadingSuccess) {
            Icon(
                painter = painterResource(Res.drawable.chat_play),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(30.dp)
            )
        } else {
            Icon(
                painter = painterResource(Res.drawable.chat_download),
                contentDescription = "Download",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(30.dp)
            )
        }
    }
}
