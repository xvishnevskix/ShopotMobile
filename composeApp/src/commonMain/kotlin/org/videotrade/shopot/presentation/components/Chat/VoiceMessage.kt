package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
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
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.AudioFactory
import org.videotrade.shopot.multiplatform.AudioPlayer
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.voice_message_pause_dark
import shopot.composeapp.generated.resources.voice_message_pause_white
import shopot.composeapp.generated.resources.voice_message_play_dark
import shopot.composeapp.generated.resources.voice_message_play_white
import kotlin.random.Random

@Composable
fun VoiceMessage(
    message: MessageItem,
    attachments: List<Attachment>
) {
    val scope = rememberCoroutineScope()
    
    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value
    
    // Состояния, уникальные для каждого сообщения
    var isPlaying by remember(message.id) { mutableStateOf(false) }
    val waveData = remember(message.id) { generateRandomWaveData(50) }
    var audioFilePath by remember(message.id) { mutableStateOf("") }
    val audioPlayer = remember { AudioFactory.createAudioPlayer() }
    var currentTime by remember(message.id) { mutableStateOf(0) }
    var duration by remember(message.id) { mutableStateOf("00:00") }
    var isLoading by remember(message.id) { mutableStateOf(false) }
    var isStartCipherLoading by remember(message.id) { mutableStateOf(false) }
    var progress by remember(message.id) { mutableStateOf(0f) }
    var downloadJob by remember(message.id) { mutableStateOf<Job?>(null) }
    var isUpload by remember(message.id) { mutableStateOf(false) }
    
    // Эффект для обновления времени воспроизведения
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (currentTime > 0 && isPlaying) {
                delay(1000L)
                currentTime--
            }
            if (currentTime <= 0) {
                stopVoice(audioPlayer)
                isPlaying = false
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
    LaunchedEffect(message) {
        if (!isUpload && message.upload != null) {
            downloadJob = scope.launch {
                isLoading = true
                isStartCipherLoading = true
                message.attachments?.firstOrNull()?.let { attachment ->


//                    delay(4000)
                    val fileId = FileProviderFactory.create().uploadCipherFile(
                        "file/upload",
                        attachment.originalFileDir!!,
                        attachment.type,
                        attachment.name
                    ) {
                        isStartCipherLoading = false
                        progress = it / 100f
                    }
                    
                    fileId?.let {
                        viewModel.sendLargeFileAttachments(
                            message.content,
                            message.fromUser,
                            message.chatId,
                            message.uploadId!!,
                            listOf(it),
                            fileType = message.attachments!![0].type
                        )
                        audioFilePath = attachment.originalFileDir!!
                    }
                }
                isUpload = true
                isLoading = false
                progress = 1f
            }
        } else {
            val existingFile = FileProviderFactory.create()
                .existingFile(attachments.first().name, attachments.first().type)
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
                val url = "${EnvironmentConfig.serverUrl}file/id/${attachments.first().fileId}"
                val fileName = attachments.first().name
                val fileType = attachments.first().type
                
                scope.launch {
                    isLoading = true
                    isStartCipherLoading = true
                    val pathResult =
                        audioFile.downloadCipherFile(url, fileType, fileName, "audio/mp4") {
                            isStartCipherLoading = false
                            progress = it / 100f
                        }
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
            .widthIn(max = 204.dp)
            .padding(
                start = 22.dp,
                end = 22.dp,
                top = if (message.fromUser == profile.id) 12.dp else 7.dp,
                bottom = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isStartCipherLoading) {
            LoadingBox(
                isLoading = isStartCipherLoading,
                color = if (message.fromUser == profile.id) Color.White else Color.DarkGray,
                onCancel = {
                    isStartCipherLoading = false
                    viewModel.deleteMessage(message)
                }
            )
        } else if (isLoading) {
            LoadingBox(
                isLoading = isLoading,
                progress = progress,
                color = if (message.fromUser == profile.id) Color.White else Color.DarkGray,
                onCancel = {
                    isLoading = false
                }
            )
        } else {
            PlayPauseButton(
                isPlaying = isPlaying,
                onClick = {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        playVoice(audioPlayer, audioFilePath)
                    } else {
                        stopVoice(audioPlayer)
                    }
                },
                isFromUser = message.fromUser == profile.id
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Waveform(waveData = waveData, message, profile)
            Text(
                text = if (isPlaying) formatSecondsToDuration(currentTime) else duration,
                color = if (message.fromUser == profile.id) Color.White else Color(0xFF2A293C),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp
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
        modifier = Modifier.size(45.dp)
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
                .padding()
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
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(45.dp)
    ) {
        val icon = if (isPlaying) {
            if (isFromUser) painterResource(Res.drawable.voice_message_pause_white)
            else painterResource(Res.drawable.voice_message_pause_dark)
        } else {
            if (isFromUser) painterResource(Res.drawable.voice_message_play_white)
            else painterResource(Res.drawable.voice_message_play_dark)
        }
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(45.dp)
        )
    }
}


fun playVoice(audioPlayer: AudioPlayer, audioFilePath: String) {
    audioPlayer.startPlaying(audioFilePath)
}

fun stopVoice(audioPlayer: AudioPlayer) {
    audioPlayer.stopPlaying()
}

@Composable
fun Waveform(waveData: List<Float>, message: MessageItem, profile: ProfileDTO) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(19.dp)
    ) {
        val barWidth = size.width / (waveData.size * 2 - 1)
        val maxBarHeight = size.height
        
        waveData.forEachIndexed { index, amplitude ->
            val barHeight = maxBarHeight * amplitude
            drawRect(
                color = if (message.fromUser == profile.id) Color.White else Color(0xFF2A293C),
                topLeft = Offset(index * 2 * barWidth, maxBarHeight / 2 - barHeight / 2),
                size = Size(barWidth, barHeight)
            )
        }
    }
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