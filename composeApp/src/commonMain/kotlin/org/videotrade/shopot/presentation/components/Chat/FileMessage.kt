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
import shopot.composeapp.generated.resources.file_message_dark
import shopot.composeapp.generated.resources.file_message_white
import shopot.composeapp.generated.resources.voice_message_pause_dark
import shopot.composeapp.generated.resources.voice_message_pause_white
import shopot.composeapp.generated.resources.voice_message_play_dark
import shopot.composeapp.generated.resources.voice_message_play_white
import kotlin.random.Random

@Composable
fun FileMessage(
    message: MessageItem,
//    attachments: List<Attachment>
) {
    val scope = rememberCoroutineScope()

    val viewModel: ChatViewModel = koinInject()
    val profile = viewModel.profile.collectAsState(initial = ProfileDTO()).value

    var isLoading by remember { mutableStateOf(false) }





    Row(
        modifier = Modifier
            .widthIn(max = 204.dp)
            .padding(start = 22.dp, end = 22.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {

            },
            modifier = Modifier.size(43.dp)
        ) {
            Image(
                modifier = Modifier.size(45.dp),
                painter = if (!isLoading) {
                    if (message.fromUser == profile.id) painterResource(Res.drawable.file_message_white)
                    else painterResource(Res.drawable.file_message_dark)
                } else {
                    if (message.fromUser == profile.id) painterResource(Res.drawable.voice_message_pause_white)
                    else painterResource(Res.drawable.voice_message_pause_dark)
                },
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "50MB.txt",
                color = if (message.fromUser == profile.id)  Color(0xFFFFFFFF) else Color(0xFF2A293C),
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
