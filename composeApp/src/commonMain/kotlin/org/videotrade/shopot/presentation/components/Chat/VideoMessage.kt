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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.chat.VideoViewerScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_download
import shopot.composeapp.generated.resources.chat_play


@Composable
fun VideoMessage(
    message: MessageItem,
    attachments: List<Attachment>,
    messageSenderName: String? = null
) {
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.current
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    val downloadProgress = viewModel.downloadProgress.collectAsState().value

    val isLoading = remember { mutableStateOf(false) }
    val isLoadingSuccess = remember { mutableStateOf(false) }
    val progress = remember { mutableStateOf(0f) }
    val downloadJob = remember { mutableStateOf<Job?>(null) }
    var filePath = remember { mutableStateOf("") }
    var photoFilePath = remember { mutableStateOf("") }
    val isBlurred = remember { mutableStateOf(true) }
    val isStartCipherLoading = remember { mutableStateOf(false) }
    val fileProvider by remember { mutableStateOf(FileProviderFactory.create()) }

    val imagePainter = rememberAsyncImagePainter(photoFilePath.value)

    val animatedProgress = animateFloatAsState(
        targetValue = progress.value,
        animationSpec = tween(durationMillis = 30)
    )


    LaunchedEffect(message) {

        val photoFileName = attachments[0].photoName


        if (photoFileName !== null) {
            val existingPhotoFile = fileProvider.existingFileInDir(photoFileName, "image")

            val url =
                "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].photoId}"

            if (!existingPhotoFile.isNullOrBlank()) {
                photoFilePath.value = existingPhotoFile
            } else {

                val downloadFilePath = fileProvider.downloadCipherFile(
                    url,
                    "image",
                    photoFileName,
                    "image"
                ) { newProgress ->
                    println("newProgress $newProgress")
                }


                if (downloadFilePath != null) {
                    photoFilePath.value = downloadFilePath
                }

                println("filePath $filePath")
            }
        }




        if (message.upload !== null) {
            downloadJob.value?.cancel()
            progress.value = 0f
            isLoading.value = true


            downloadJob.value = scope.launch {
                isLoading.value = true
                isStartCipherLoading.value = true
                message.attachments?.get(0)?.let { attachment ->

                    println("adasdada ${attachment.name} ${attachment.photoPath}")

                    val fileIds = fileProvider.uploadVideoFile(
                        "file/upload/video",
                        attachment.originalFileDir!!,
                        attachment.photoPath!!,
                        attachment.type,
                        attachment.name,
                        attachment.photoName!!
                    ) {
                        isStartCipherLoading.value = false

                        println("progress1 ${it / 100f}")

                        progress.value = it / 100f
                    }


                    if (fileIds !== null) {
                        println("fileId ${fileIds}")
                        viewModel.sendLargeFileAttachments(
                            message.content,
                            message.fromUser,
                            message.chatId,
                            message.uploadId!!,
                            fileIds,
                            fileType = message.attachments!![0].type
                        )
                    }


                }

                isLoading.value = false
                progress.value = 1f
                isLoading.value = false
//                isLoadingSuccess.value = true
//                isBlurred.value = false

            }

            return@LaunchedEffect
        }


        val fileName = attachments[0].name
        println("fileName $fileName")

        val existingFile = fileName.let {
            fileProvider.existingFileInDir(it, "video")
        }

        if (!existingFile.isNullOrBlank()) {
            isLoading.value = false
            progress.value = 1f
            isLoading.value = false
            isLoadingSuccess.value = true
            isBlurred.value = false
            filePath.value = existingFile
        }
    }


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
                indication = null // Remove click effect
            ) {
                if (isLoadingSuccess.value) {
                    viewModel.clearMessages()
                    navigator?.push(
                        VideoViewerScreen(
                            messageSenderName = messageSenderName,
                            message = message,
                            filePath = filePath.value
                        )
                    )
                    return@clickable
                }

                message.attachments?.get(0)?.let { attachment ->
                    if (!isLoading.value && !isLoadingSuccess.value) {

                        downloadJob.value = scope.launch {
//                        for (i in 1..100) {
//                            delay(40)
//                            progress.value = i / 99f
//                        }

                            val url =
                                "${EnvironmentConfig.SERVER_URL}file/id/${attachments[0].fileId}"

                            isLoading.value = true
                            isBlurred.value = true

                            val videoFilePath = fileProvider.downloadCipherFile(
                                url,
                                attachment.type,
                                attachment.name,
                                "video"
                            ) { newProgress ->
                                isStartCipherLoading.value = false
                                progress.value = newProgress
                            }

                            if (videoFilePath != null) {
                                filePath.value = videoFilePath

                                isLoading.value = false
                                isLoadingSuccess.value = true
                                isBlurred.value = false
                            }


                        }
                    }


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
        } else if (isLoadingSuccess.value) {
            Icon(
                painter = painterResource(Res.drawable.chat_play),
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center).size(30.dp).clickable {
                    navigator?.push(
                        VideoViewerScreen(
                            messageSenderName = messageSenderName,
                            message = message,
                            filePath = filePath.value
                        )
                    )
                }
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
