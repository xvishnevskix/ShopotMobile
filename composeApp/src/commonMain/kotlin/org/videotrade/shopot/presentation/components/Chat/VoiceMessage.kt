package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.ChatItem
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.AudioPlayer
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.chat_pause
import shopot.composeapp.generated.resources.chat_play
import kotlin.random.Random

@Composable
fun VoiceMessage(
    message: MessageItem,
    attachments: List<Attachment>,
    chat: ChatItem
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    
    // Состояния, уникальные для каждого сообщения
    var isPlaying = remember(message.id) { mutableStateOf(false) }
    val waveData = remember(message.id) { generateRandomWaveData(29) }
    var audioFilePath by remember(message.id) { mutableStateOf("") }
    val audioPlayer = remember { AudioFactory.createAudioPlayer() }
    var currentTime by remember(message.id) { mutableStateOf(0) }
    var duration by remember(message.id) { mutableStateOf("00:00") }
    var isLoading by remember(message.id) { mutableStateOf(false) }
    var isStartCipherLoading by remember(message.id) { mutableStateOf(false) }
    var progress by remember(message.id) { mutableStateOf(0f) }
    var downloadJob by remember(message.id) { mutableStateOf<Job?>(null) }
    var isUpload by remember(message.id) { mutableStateOf(false) }

    // Получаем текущее воспроизводимое сообщение
    val currentPlayingMessage by viewModel.currentPlayingMessage.collectAsState()

    // Проверяем, активное ли это сообщение
    LaunchedEffect(currentPlayingMessage) {
        isPlaying.value = currentPlayingMessage == message.id
        if (!isPlaying.value) {
            stopVoice(audioPlayer)  // Останавливаем воспроизведение, если это не активное сообщение
        }
    }
    
    // Эффект для обновления времени воспроизведения
    LaunchedEffect(isPlaying.value) {
        if (isPlaying.value) {
            while (currentTime > 0 && isPlaying.value) {
                delay(1000L)
                currentTime--
            }
        } else {
            if (duration.isNotBlank()) {
                durationToSeconds(duration)?.let {
                    currentTime = it
                }
            }
        }
    }
    
    // Эффект для загрузки или скачивания файла
    LaunchedEffect(message.id) {
        println("message.idSSSSS ${message.id}")
        if (!isUpload && message.upload != null) {
            downloadJob = scope.launch {
                isLoading = true
                isStartCipherLoading = true
                message.attachments?.firstOrNull()?.let { attachment ->


//                    delay(4000)
                    val originalFilePath = attachment.originalFileDir
                    if (originalFilePath != null) {
                        val fileId = FileProviderFactory.create().uploadCipherFile(
                            "file/upload",
                            originalFilePath,
                            attachment.type,
                            attachment.name
                        ) {
                            isStartCipherLoading = false
                            progress = it / 100f
                        }

                        fileId?.let {
                            viewModel.sendLargeFileAttachments(
                                message.content,
                                message.uploadId!!,
                                listOf(it),
                                fileType = message.attachments!![0].type,
                                chat,
                            )
                            audioFilePath = originalFilePath
                        }
                    }

                }
                isUpload = true
                isLoading = false
                progress = 1f
            }
        } else {
            println("AAAAAdsada")
            val existingFile = FileProviderFactory.create()
                .existingFileInDir(attachments.first().name, attachments.first().type)
            if (!existingFile.isNullOrBlank()) {
                downloadJob?.cancel()
                isLoading = false
                progress = 1f
                audioFilePath = existingFile
                audioPlayer.getAudioDuration(existingFile, attachments.first().name)?.let {
                    currentTime = durationToSeconds(it) ?: 0
                    duration = it
                }
            } else {
                val audioFile = FileProviderFactory.create()
                val url = "${EnvironmentConfig.SERVER_URL}file/id/${attachments.first().fileId}"
                val fileName = attachments.first().name
                val fileType = attachments.first().type

                scope.launch {
                    isLoading = true
                    isStartCipherLoading = true
                    println(" start AAAAA")
                    val pathResult =
                        audioFile.downloadCipherFile(url, fileType, fileName, "audio/mp4") {
                            isStartCipherLoading = false
                            progress = it / 100f
                        }

                    println(" end AAAAA")

                    pathResult?.let {
                        audioFilePath = it
                        audioPlayer.getAudioDuration(it, fileName)?.let { durationString ->
                            currentTime = durationToSeconds(durationString) ?: 0
                            duration = durationString
                        }
                    }
                    isUpload = true
                    isLoading = false
                    progress = 1f
                }
            }
        }
    }
    
    Row(
        modifier = Modifier
            .widthIn(max = 261.dp)
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp,
                top = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isStartCipherLoading) {
            LoadingBox(
                isLoading = isStartCipherLoading,
                color = if (message.fromUser == profile.id) Color.White else colors.primary,
                onCancel = {
                    isStartCipherLoading = false
                    viewModel.deleteMessage(message)
                }
            )
        } else if (isLoading) {
            LoadingBox(
                isLoading = isLoading,
                progress = progress,
                color = if (message.fromUser == profile.id) Color.White else colors.primary,
                onCancel = {
                    isLoading = false
                }
            )
        } else {
            PlayPauseButton(
                isPlaying = isPlaying.value,
                onClick = {
                    isPlaying.value = !isPlaying.value
//                    if (isPlaying.value) {
//                        playVoice(audioPlayer, audioFilePath, isPlaying)
//                    } else {
//                        stopVoice(audioPlayer)
//                    }

                    if (isPlaying.value) {
                        viewModel.setPlayingMessage(message.id)  // Устанавливаем новое активное сообщение
                        playVoice(audioPlayer, audioFilePath, isPlaying) // Передаём isPlaying

                    } else {
                        stopVoice(audioPlayer)
                        viewModel.setPlayingMessage(null)  // Останавливаем текущее воспроизведение
                        isPlaying.value = false
                    }
                },
                isFromUser = message.fromUser == profile.id
            )
        }
        Spacer(modifier = Modifier.width(13.dp))
        Box(modifier = Modifier.weight(1F)) {
            Waveform(waveData = waveData, message, profile)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.width(50.dp)) {
            Text(
                text = if (isPlaying.value) formatSecondsToDuration(currentTime) else duration,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                color = if (message.fromUser == profile.id) Color.White else colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
        }
    }
}

@Composable
fun Waveform(waveData: List<Float>, message: MessageItem, profile: ProfileDTO) {
    val minAmplitude = 0.2f // Минимальная амплитуда для каждой волны
    val colors = MaterialTheme.colorScheme
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val barWidth = size.width / (waveData.size * 2 - 1)
        val maxBarHeight = size.height
        
        waveData.forEachIndexed { index, amplitude ->
            val adjustedAmplitude =
                maxOf(amplitude, minAmplitude) // Применяем минимальную амплитуду
            val barHeight = maxBarHeight * adjustedAmplitude
            
            drawRoundRect(
                color = if (message.fromUser == profile.id) Color.White else colors.primary,
                topLeft = Offset(index * 2 * barWidth, maxBarHeight / 2 - barHeight / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2) // Закруглённые углы
            )
        }
    }
}

@Composable
fun LoadingBox(
    isLoading: Boolean,
    progress: Float? = null,
    color: Color,
    onCancel: () -> Unit
) {
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(24.dp)
    ) {
        if (progress !== null) {
            CircularProgressIndicator(
                progress =
                if (isLoading) progress else 1f,
                color = color,
                strokeWidth = 2.dp,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(
                color = color,
                strokeWidth = 2.dp,
                modifier = Modifier.fillMaxSize()
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            modifier = Modifier
                .padding(1.dp)
                .clickable(onClick = onCancel),
            tint = color
        )
    }
}

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    isFromUser: Boolean
) {
    val colors = MaterialTheme.colorScheme
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(24.dp)
    ) {
        val icon = if (isPlaying) {
            painterResource(Res.drawable.chat_pause)
        } else {
            painterResource(Res.drawable.chat_play)
        }
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            colorFilter = if (isFromUser) ColorFilter.tint(Color.White) else ColorFilter.tint(colors.primary)
        )
    }
}


fun playVoice(audioPlayer: AudioPlayer, audioFilePath: String, isPlaying: MutableState<Boolean>) {
//    audioPlayer.startPlaying(audioFilePath, isPlaying)
    isPlaying.value = true // Устанавливаем состояние "играет"

    val isStarted = audioPlayer.startPlaying(audioFilePath, isPlaying) // Передаём isPlaying

    if (!isStarted) {
        isPlaying.value = false // Если аудио не запустилось, сбрасываем состояние
    }
}

fun stopVoice(audioPlayer: AudioPlayer) {
    audioPlayer.stopPlaying()
}


fun generateRandomWaveData(size: Int): List<Float> {
    return List(size) { Random.nextFloat() }
}

fun durationToSeconds(duration: String): Int? {
    
    println("duration ${duration}")
    
    try {
        val parts = duration.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    } catch (e: Exception) {
        println("error durationToSeconds: ${e}")
        return null
    }
    
}

fun formatSecondsToDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
    val secondsStr =
        if (remainingSeconds < 10) "0$remainingSeconds" else remainingSeconds.toString()
    return "$minutesStr:$secondsStr"
}