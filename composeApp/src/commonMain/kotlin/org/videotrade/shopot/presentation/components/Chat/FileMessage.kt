import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.file_message_dark
import shopot.composeapp.generated.resources.file_message_download_dark
import shopot.composeapp.generated.resources.file_message_download_white
import shopot.composeapp.generated.resources.file_message_white
import shopot.composeapp.generated.resources.voice_message_pause_dark
import shopot.composeapp.generated.resources.voice_message_pause_white


@Composable
fun FileMessage(
    message: MessageItem,
) {
    val scope = rememberCoroutineScope()

    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value

    var isLoading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }

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
                    if (!isLoading) {
                        downloadJob?.cancel()
                        progress = 0f
                        isLoading = true
                        downloadJob = scope.launch {

                            for (i in 1..100) {
                                delay(50)
                                progress = i / 100f
                            }
                            isLoading = false
                            progress = 1f
                        }
                    } else {

                        downloadJob?.cancel()
                        isLoading = false
                        progress = 0f
                    }
                },
                modifier = Modifier.size(43.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        progress = progress,
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
                        modifier = Modifier.size(45.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "50MB.txt",
                color = if (message.fromUser == profile.id) Color(0xFFFFFFFF) else Color(0xFF2A293C),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp
            )
            Text(
                text = "50MB",
                color = if (message.fromUser == profile.id) Color(0xFFD7D4D4) else Color(0xFF37363F),
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp
            )
        }
    }
}
