package org.videotrade.shopot.presentation.components.Chat

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import shopot.composeapp.generated.resources.double_message_check
import shopot.composeapp.generated.resources.voice_message_pause
import shopot.composeapp.generated.resources.voice_message_play_dark
import shopot.composeapp.generated.resources.voice_message_play_white
import kotlin.random.Random

@Composable
fun VoiceMessageBox(
//    isPlaying: Boolean,
    duration: String,
//    waveData: List<Float>,
//    onPlayPauseClick: () -> Unit,
    
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
    
    
    LaunchedEffect(Unit) {
        scope.launch {
            val audioFile = FileProviderFactory.create()
            val url =
                "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
            val fileName = "downloadedFile.mp4"
            
            val filePath = audioFile.getAudioFilePath(fileName)
            
            audioFile.downloadFileToDirectory(url, filePath)
            
            
            println("filePath $filePath")
            
            audioFilePath = filePath
            
        }
    }
    
    Row(
        modifier = Modifier
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
            modifier = Modifier
                .size(45.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(45.dp),
                painter =
                if (!isPlaying) {
                    if (message.fromUser == profile.id) painterResource(Res.drawable.voice_message_play_white)
                    else painterResource(Res.drawable.voice_message_play_dark)
                } else {
                    painterResource(Res.drawable.voice_message_pause)
                },
                contentDescription = null,
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Waveform(waveData = waveData, message, profile)
            Text(
                text = duration,
                color = if (message.fromUser == profile.id) Color.White else Color(0xFF2A293C),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                
                )
        }
    }
}

//    Row(
//        modifier = Modifier
//            .padding(8.dp)
//            .background(color = Color(0xFF2E2E48), shape = RoundedCornerShape(16.dp))
//            .padding(12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(
//            onClick = { isPlaying = !isPlaying },
//            modifier = Modifier
//                .size(40.dp)
//                .background(Color.White, shape = CircleShape)
//        ) {
//            Image(
//                modifier = Modifier
//                    .size(45.dp),
//                painter = if (isPlaying) painterResource(Res.drawable.voice_message_play_white) else painterResource(
//                    Res.drawable.voice_message_play_dark),
//                contentDescription = null,
//            )
//        }
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        Column {
//            Waveform(waveData = waveData)
//            Text(
//                text = duration,
//                color = Color.White,
//                fontSize = 12.sp,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
//    }


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

@Composable
fun VoiceMessageBlurBox(
    message: MessageItem,
    profile: ProfileDTO,
    viewModel: ChatViewModel,
    onClick: () -> Unit,
    visible: Boolean
) {
    val clipboardManager = LocalClipboardManager.current
    
    val transition = updateTransition(targetState = visible, label = "MessageBlurBoxTransition")
    val orientation: Dp = if (message.fromUser == profile.id) 100.dp else -75.dp
    val firstColumnOffsetX by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "FirstColumnOffsetX"
    ) { state ->
        if (state) 0.dp else orientation
    }
    
    val secondColumnOffsetY by transition.animateDp(
        transitionSpec = { tween(durationMillis = 300, easing = FastOutSlowInEasing) },
        label = "SecondColumnOffsetY"
    ) { state ->
        if (state) 0.dp else 200.dp
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .offset(x = firstColumnOffsetX)
                .fillMaxWidth(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = if (message.fromUser == profile.id) Alignment.CenterEnd else Alignment.CenterStart,
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(onClick = onClick)
            ) {
                if (message.fromUser == profile.id) {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomEnd = 0.dp,
                            bottomStart = 20.dp
                        ),
                        shadowElevation = 4.dp,
                        color = Color(0xFF2A293C)
                    ) {
                        message.content?.let {
//                            MessageFormat(message, profile)
                        }
                    }
                } else {
                    Surface(
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomEnd = 20.dp,
                            bottomStart = 0.dp
                        ),
                        shadowElevation = 4.dp,
                        color = Color(0xFFF3F4F6)
                    ) {
                        message.content?.let {
//                            MessageFormat(message, profile)
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = if (message.fromUser == profile.id) Arrangement.End else Arrangement.Start,
                modifier = Modifier
                    .padding(start = 2.dp, end = 2.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .padding(
                            start = if (message.fromUser == profile.id) 70.dp else 0.dp,
                            top = 2.dp,
                            end = 4.dp
                        )
                        .size(14.dp),
                    painter = painterResource(Res.drawable.double_message_check),
                    contentDescription = null,
                )
            }
        }
        
        Column(
            modifier = Modifier
                .offset(y = secondColumnOffsetY)
                .padding(top = 4.dp)
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
        ) {
            editOptions.forEachIndexed { index, editOption ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
                        .fillMaxWidth().clickable {
                        
                        
                        editOption.onClick(viewModel, message, clipboardManager)
                        }
                ) {
                    Text(
                        text = editOption.text,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 20.sp,
                        color = Color(0xFF000000)
                    )
                    Image(
                        painter = painterResource(editOption.imagePath),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (index < editOptions.size - 1) {
                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
