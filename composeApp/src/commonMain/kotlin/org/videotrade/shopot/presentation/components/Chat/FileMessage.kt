import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_file_message
import shopot.composeapp.generated.resources.download


@Composable
fun FileMessage(
    message: MessageItem,
    attachments: List<Attachment>,
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    val downloadProgress = viewModel.downloadProgress.collectAsState().value
    val fileProvider = FileProviderFactory.create()
    var isLoading by remember { mutableStateOf(false) }
    var isLoadingSuccess by remember { mutableStateOf(false) }
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
                            listOf(fileId),
                            fileType = message.attachments!![0].type
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
        val url = "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"
        val fileName = attachments[0].name
        println("fileName $fileName")
        
        val existingFile = audioFile.existingFileInDir(fileName, attachments[0].type)
        
        if (!existingFile.isNullOrBlank() ) {
            downloadJob?.cancel()
            isLoadingSuccess = true
            isLoading = false
            isStartCipherLoading = false
            progress = 1f
            filePath = existingFile
        }
    }
    
    Row(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
                top = 16.dp
            )
            .widthIn(max = 260.dp)
            .clickable {
                val fileName = attachments[0].name
                val existingFile = fileProvider.existingFileInDir(fileName, attachments[0].type)
                if (existingFile != null) {
                    fileProvider.openFileOrDirectory(existingFile)
                } else {
                    println("File not found in the system: $fileName")
                }
            }
            ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(Color.White,
                shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    
                    if (isLoadingSuccess) return@IconButton
                    
                    message.attachments?.get(0)?.let { attachment ->
                        if (!isLoading) {
                            downloadJob?.cancel()
                            progress = 0f
                            isLoading = true
                            
                            val url =
                                "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"
                            
                            downloadJob = scope.launch {
                                isLoading = true
                                audioFile.downloadCipherFile(
                                    url,
                                    attachment.type,
                                    attachment.name,
                                    "file"
                                ) { newProgress ->
                                    isStartCipherLoading = false
                                    progress = newProgress
                                }
                                isLoadingSuccess = true
                                isLoading = false
                            }
                        } else {
                            downloadJob?.cancel()
                            isLoading = false
                            progress = 0f
                        }
                    }
                },
                modifier = Modifier

            ) {
                if (isStartCipherLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(4.dp)
                    ) {
                        CircularProgressIndicator(
                            color =

                            if (message.fromUser != profile.id) Color(0xFF373533) else Color(
                                0xFFCAB7A3
                            )
                            ,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            modifier = Modifier
                                .padding(4.dp)
                                .pointerInput(Unit) {
                                    isStartCipherLoading = false
                                    viewModel.deleteMessage(message)
                                },
                            tint =
                            if (message.fromUser != profile.id) Color(0xFF373533) else Color(
                                0xFFCAB7A3
                            )
                        )
                    }
                } else {
                    if (isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = progress,  // Use animated progress
                                color =
                                if (message.fromUser != profile.id) Color(0xFF373533) else Color(
                                    0xFFCAB7A3
                                ),
                                strokeWidth = 2.dp,
                                modifier = Modifier
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier
                                    .padding(4.dp)
                                    .pointerInput(Unit) {
                                        isLoading = false

                                    },
                                if (message.fromUser != profile.id) Color(0xFF373533) else Color(
                                    0xFFCAB7A3
                                )
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.background(
                                color =  Color.White
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(
                                    if (progress == 1f) {
                                        Res.drawable.chat_file_message
                                    } else {
                                         Res.drawable.download
                                    }
                                ),
                                contentDescription = null,
                                colorFilter = if (message.fromUser == profile.id) ColorFilter.tint(Color(0xFFCAB7A3))  else ColorFilter.tint(Color(0xFF373533)) ,
                                modifier = Modifier.size(
                                    width = if (progress == 1f) 14.dp else 16.dp,
                                    height = if (progress == 1f) 16.dp else 18.dp
                                ).pointerInput(Unit) {
                                    println("dasdadadaaaaaa ${progress != 1f}")
                                    if (progress != 1f)
                                        isStartCipherLoading = true
                                }
                                ,
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(verticalArrangement = Arrangement.Top) {
            message.attachments?.get(0)?.let {
                Text(
                    text = it.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (message.fromUser == profile.id) Color(0xFFFFFFFF) else colors.primary,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (attachments[0].size !== null) {
                Text(
                    text = formatSize(attachments[0].size!!),
                    color = if (message.fromUser == profile.id) Color(0xFFF7F7F7) else colors.secondary,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                )
            }
        }
    }
}
