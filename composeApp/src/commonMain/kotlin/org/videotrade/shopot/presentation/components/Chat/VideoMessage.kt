import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
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
import org.videotrade.shopot.api.formatSize
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_download
import shopot.composeapp.generated.resources.chat_play
import shopot.composeapp.generated.resources.file_message_dark
import shopot.composeapp.generated.resources.file_message_download_dark
import shopot.composeapp.generated.resources.file_message_download_white
import shopot.composeapp.generated.resources.file_message_white
import shopot.composeapp.generated.resources.pepe


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

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 30)
    )



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
        Image(
            painter = painterResource(Res.drawable.pepe),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(if (isBlurred) 16.dp else 0.dp)
        )

        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(45.dp).align(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    progress = animatedProgress,
                    color = Color.White ,
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
                    tint =  Color.White
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
