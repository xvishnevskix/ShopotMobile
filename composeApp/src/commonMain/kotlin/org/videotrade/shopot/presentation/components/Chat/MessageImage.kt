package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.formatTimeOnly
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.message_double_check
import shopot.composeapp.generated.resources.message_single_check


@Composable
fun MessageImage(
    message: MessageItem, profile: ProfileDTO,
    attachments: List<Attachment>,
    messageSenderName: String? = null,
    chat: ChatItem
) {
//    if (getPlatform() == Platform.Android) {
        val navigator = LocalNavigator.currentOrThrow

        val viewModel: ChatViewModel = koinInject()
        val isLoading = remember { mutableStateOf(false) }
        val isLoadingSuccess = remember { mutableStateOf(false) }
        val progress = remember { mutableStateOf(0f) }
        val downloadJob = remember { mutableStateOf<Job?>(null) }
        var filePath = remember { mutableStateOf("") }
        var photoFilePath = remember { mutableStateOf("") }
        val isBlurred = remember { mutableStateOf(true) }
        val isStartCipherLoading = remember { mutableStateOf(false) }
        val colors = MaterialTheme.colorScheme


    val fileProvider by remember { mutableStateOf(FileProviderFactory.create()) }
        
        val animatedProgress = animateFloatAsState(
            targetValue = progress.value,
            animationSpec = tween(durationMillis = 30)
        )
        
        val scope = rememberCoroutineScope()
        
        val imagePainter = rememberAsyncImagePainter(filePath.value)
        
        
        LaunchedEffect(message) {
            isStartCipherLoading.value = false //как заглушка, пока не прогрузится
            if (message.upload !== null) {
                downloadJob.value?.cancel()
                progress.value = 0f
                isLoading.value = true
                
                
                downloadJob.value = scope.launch {
                    isLoading.value = true
                    isStartCipherLoading.value = true
                    message.attachments?.get(0)?.let { attachment ->
                        val originalFileDir = attachments[0].originalFileDir
                        
                        if (originalFileDir != null) {
                            filePath.value = originalFileDir
                        }
                        
                        println("adasdada ${attachment.name} ${attachment.type}")
                        val fileId = FileProviderFactory.create().uploadCipherFile(
                            "file/upload",
                            attachment.originalFileDir!!,
                            attachment.type,
                            attachment.name
                        ) {
                            isStartCipherLoading.value = false
                            
                            println("progress1 ${it / 100f}")
                            
                            progress.value = it / 100f
                        }
                        
                        
                        if (fileId !== null) {
                            println("fileId ${fileId}")
                            
                            viewModel.sendLargeFileAttachments(
                                message.content,
                                message.uploadId!!,
                                listOf(fileId),
                                fileType = message.attachments!![0].type,
                                chat,
                            )
                        }
                        
                        
                    }
                    
                    isLoading.value = false
                    progress.value = 1f
                    isLoading.value = false
                    isBlurred.value = false
                }
                
                return@LaunchedEffect
            }
            
            
            
            println("fileId ${message.attachments?.get(0)?.fileId}")
            val fileName = attachments[0].name
            println("fileName $fileName")
            
            val existingFile = fileProvider.existingFileInDir(fileName, attachments[0].type)
            
            if (!existingFile.isNullOrBlank()) {
                downloadJob.value?.cancel()
                isLoadingSuccess.value = true
                isLoading.value = false
                isStartCipherLoading.value = false
                progress.value = 1f
                filePath.value = existingFile
                isBlurred.value = false
                
            } else {
                
                val url =
                    "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"
                
                downloadJob.value?.cancel()
                progress.value = 0f
                isLoading.value = true
                
                val downloadFilePath = fileProvider.downloadCipherFile(
                    url,
                    "image",
                    attachments[0].name,
                    "image"
                ) { newProgress ->
                    println("newProgress $newProgress")
                    progress.value = newProgress / 100f
                }
                
                
                if (downloadFilePath != null) {
                    filePath.value = downloadFilePath
                    downloadJob.value?.cancel()
                    isLoadingSuccess.value = true
                    isLoading.value = false
                    isStartCipherLoading.value = false
                    progress.value = 1f
                    isBlurred.value = false
                }
            }
        }

        val imageState = remember { mutableStateOf(imagePainter) }
        Box(
            modifier = Modifier
                .size(250.dp, 350.dp)
                .padding(4.dp)
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
                    indication = null // Убирает эффект нажатия
                ) {


                    navigator.let {
                        navigateToScreen(
                            it,
                            PhotoViewerScreen(
                                imageState,
                                messageSenderName,
                            )
                        )
                    }
                }
        ) {
            
            Image(
                painter = imagePainter,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (isBlurred.value) 16.dp else 0.dp)
            )
            
            if (isLoading.value) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(45.dp).align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        progress = animatedProgress.value,
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
                                
                                downloadJob.value?.cancel()
                                isLoading.value = false
                                isLoadingSuccess.value = false
                                isBlurred.value = true
                                progress.value = 0f
                            },
                        tint = Color.White
                    )
                }
            }
//            else if (isLoadingSuccess.value) {
//
//            } else {
//                Icon(
//                    painter = painterResource(Res.drawable.chat_download),
//                    contentDescription = "Download",
//                    tint = Color.White,
//                    modifier = Modifier.align(Alignment.Center).size(30.dp)
//                )
//            }

            val isMainUser = message.fromUser == profile.id
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = if (!isMainUser) 6.dp else 0.dp,
                        bottom = if (!isMainUser) 4.dp else 0.dp,
                    )
                    .background(
                        color = Color(0x80000000),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (message.created.isNotEmpty()) {
                        Text(
                            text = formatTimeOnly(message.created),
                            style = TextStyle(
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = Color.White,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                        )
                    }

                    if (isMainUser) {
                        if (message.anotherRead) {
                            Image(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(width = 17.7.dp, height = 8.dp),
                                painter = painterResource(Res.drawable.message_double_check),
                                contentDescription = null,
//                    colorFilter = ColorFilter.tint(Color.White)
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(width = 12.7.dp, height = 8.dp),
                                painter = painterResource(Res.drawable.message_single_check),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                    }
                }
            }

        }
//    } else {
//        val imageFilePath = remember(attachments[0].fileId) { mutableStateOf("") }
//
//        val imagePainter = rememberAsyncImagePainter(imageFilePath.value)
//
//        val imageState = remember { mutableStateOf(imagePainter) }
//
//        val navigator = LocalNavigator.current
//        val url =
//            "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"
//        LaunchedEffect(attachments[0].fileId) {
//            val fileId = attachments[0].fileId
//            val fileType = attachments[0].type
//
//            val fileProvider = FileProviderFactory.create()
//            val existingFile =
//                fileProvider.existingFileInDir(fileId, fileType)
//
//            if (!existingFile.isNullOrBlank()) {
//                imageFilePath.value = existingFile
//                println("existingFile ${existingFile}")
//            } else {
//                val filePath = fileProvider.downloadCipherFile(
//                    url,
//                    "image",
//                    fileId,
//                    "image"
//                ) { newProgress ->
//                    println("newProgress $newProgress")
//                }
//                if (filePath != null) {
//                    imageFilePath.value = filePath
//                }
//                println("filePath $filePath")
//            }
//        }
//
//        Image(
//            painter = imagePainter,
//            contentDescription = "Image",
//            contentScale = ContentScale.Crop,
//            modifier = Modifier
//                .size(250.dp, 350.dp)
//                .padding(
//                    4.dp
//                )
//                .clip(
//                    RoundedCornerShape(
//                        16.dp
//                    )
//                ).clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null // Убирает эффект нажатия
//                ) {
//                    if (imageFilePath.value.isNotBlank())
//                        navigator?.let {
//                            navigateToScreen(
//                                it,
//                                PhotoViewerScreen(
//                                    imageState,
//                                    messageSenderName,
//                                )
//                            )
//                        }
//                }
//        )
//    }
}


