import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.formatSize
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.file_message_dark
import shopot.composeapp.generated.resources.file_message_download_dark
import shopot.composeapp.generated.resources.file_message_download_white
import shopot.composeapp.generated.resources.file_message_white


@Composable
fun FileMessage(
    message: MessageItem,
    attachments: List<Attachment>,
) {
    val scope = rememberCoroutineScope()
    
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    val downloadProgress = viewModel.downloadProgress.collectAsState().value
    
    var isLoading by remember { mutableStateOf(false) }
    var isStartCipherLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var filePath by remember { mutableStateOf("") }
    val audioFile by remember { mutableStateOf(FileProviderFactory.create()) }
//    var isUploading by remember { mutableStateOf(false) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 4000)  // Adjust duration as needed
    )
    
    LaunchedEffect(message) {
        
        if (message.upload !== null) {
            downloadJob?.cancel()
            progress = 0f
            isLoading = true
            
            
            downloadJob = scope.launch {
                isLoading = true
                isStartCipherLoading = true
//                delay(2000)
//
//                isStartCipherLoading = false
//                isLoading = true
//
//                // Начинаем анимировать прогресс
//                progress = 1f
//                delay(4000)
//
//                isLoading = false
                message.attachments?.get(0)?.let { attachment ->
                    
                    println("adasdada ${attachment.name} ${attachment.type}")
                    val fileId = FileProviderFactory.create().uploadCipherFile(
                        "file/upload",
                        attachment.originalFileDir!!,
                        attachment.type,
                        attachment.name
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
            }
            
            return@LaunchedEffect
        }
        
        
        
        println("fileId ${message.attachments?.get(0)?.fileId}")
        val url = "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
        val fileName = attachments[0].name
        println("fileName $fileName")
        
        val existingFile = audioFile.existingFile(fileName, attachments[0].type)
        
        if (!existingFile.isNullOrBlank()) {

            downloadJob?.cancel()
            isLoading = false
            progress = 1f
            println("filePath $filePath")
            filePath = existingFile
        }
    }
    
    Row(
        modifier = Modifier
            .widthIn(max = 204.dp)
            .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(45.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    message.attachments?.get(0)?.let { attachment ->
                        if (!isLoading) {
                            downloadJob?.cancel()
                            progress = 0f
                            isLoading = true
                            
                            val url =
                                "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
                            
                            downloadJob = scope.launch {
                                isLoading = true
                                audioFile.downloadCipherFile(
                                    url,
                                    attachment.type,
                                    attachment.name,
                                ) { newProgress ->
                                    isStartCipherLoading = false
                                    progress = newProgress
                                }
                                isLoading = false
                            }
                        } else {
                            downloadJob?.cancel()
                            isLoading = false
                            progress = 0f
                        }
                    }
                },
                modifier = Modifier.size(43.dp)
            ) {
                if (isStartCipherLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(45.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (message.fromUser == profile.id) Color.White else Color.DarkGray,
                            strokeWidth = 2.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding()
                                .pointerInput(Unit) {
                                    isStartCipherLoading = false
                                    viewModel.deleteMessage(message)
                                },
                            tint = if (message.fromUser == profile.id) Color.White else Color.DarkGray
                        )
                    }
                } else {
                    if (isLoading) {
                        CircularProgressIndicator(
                            progress = progress,  // Use animated progress
                            color = if (message.fromUser == profile.id) Color.White else Color.DarkGray,
                            strokeWidth = 2.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding()
                                .pointerInput(Unit) {
                                    isLoading = false
                                    
                                },
                            tint = if (message.fromUser == profile.id) Color.White else Color.DarkGray
                        )
                    } else {
                        Image(
                            painter = painterResource(
                                if (progress == 1f) {
                                    if (message.fromUser == profile.id) Res.drawable.file_message_white
                                    else Res.drawable.file_message_dark
                                } else {
                                    if (message.fromUser == profile.id) Res.drawable.file_message_download_white
                                    else Res.drawable.file_message_download_dark
                                }
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp).pointerInput(Unit) {
                                if (progress != 1f)
                                    isStartCipherLoading = true
                            },
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            message.attachments?.get(0)?.let {
                Text(
                    text = it.name,
                    color = if (message.fromUser == profile.id) Color(0xFFFFFFFF) else Color(
                        0xFF2A293C
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp
                )
            }
            if (attachments[0].size !== null) {
                Text(
                    text = formatSize(attachments[0].size!!),
                    color = if (message.fromUser == profile.id) Color(0xFFD7D4D4) else Color(
                        0xFF37363F
                    ),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp
                )
            }
        }
    }
}
