package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
    
    var isPlaying by remember { mutableStateOf(false) }
    val waveData = remember { generateRandomWaveData(50) }
    var audioFilePath by remember { mutableStateOf("") }
    val audioPlayer = remember { AudioFactory.createAudioPlayer() }
    var currentTime by remember { mutableStateOf(0) }
    val duration = remember { mutableStateOf("00:00") }
    
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
            if (duration.value.isNotBlank())
                currentTime = durationToSeconds(duration.value)
        }
    }
    
    LaunchedEffect(message) {
        
        println("fileId ${message.attachments?.get(0)?.fileId}")
        val audioFile = FileProviderFactory.create()
        val url = "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
        val fileName = "${attachments[0].name}.m4a"
        
        println("fileName $fileName")
        val filePath = audioFile.getFilePath(fileName, "audio/mp4")
        scope.launch {
            
            if (filePath == null) return@launch
            
            audioFile.downloadFileToDirectory(url, filePath) {
            
            }
            
            println("filePath $filePath")
            val audioDuration = audioPlayer.getAudioDuration(filePath)
            
            println("audioDuration $audioDuration")
            
            currentTime = durationToSeconds(audioDuration)
            
            duration.value = audioDuration
            
            audioFilePath = filePath
        }
    }
    
    Row(
        modifier = Modifier
            .widthIn(max = 204.dp)
            .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                isPlaying = !isPlaying
                println("isPlaying $isPlaying")
                
                if (isPlaying) {
                    playVoice(audioPlayer, audioFilePath)
                } else {
                    stopVoice(audioPlayer)
                }
            },
            modifier = Modifier.size(45.dp)
        ) {
            Image(
                modifier = Modifier.size(45.dp),
                painter = if (!isPlaying) {
                    if (message.fromUser == profile.id) painterResource(Res.drawable.voice_message_play_white)
                    else painterResource(Res.drawable.voice_message_play_dark)
                } else {
                    if (message.fromUser == profile.id) painterResource(Res.drawable.voice_message_pause_white)
                    else painterResource(Res.drawable.voice_message_pause_dark)
                },
                contentDescription = null
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Waveform(waveData = waveData, message, profile)
            Text(
                text = if (isPlaying) formatSecondsToDuration(currentTime) else duration.value,
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

fun durationToSeconds(duration: String): Int {
    val parts = duration.split(":")
    return parts[0].toInt() * 60 + parts[1].toInt()
}

fun formatSecondsToDuration(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    val minutesStr = if (minutes < 10) "0$minutes" else minutes.toString()
    val secondsStr =
        if (remainingSeconds < 10) "0$remainingSeconds" else remainingSeconds.toString()
    return "$minutesStr:$secondsStr"
}