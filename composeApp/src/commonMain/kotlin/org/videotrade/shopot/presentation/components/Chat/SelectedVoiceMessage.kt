import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.call_microphone_on
import shopot.composeapp.generated.resources.menu_gallery

@Composable
fun SelectedVoiceMessage(selectedMessage: MessageItem, selectedMessageSenderName: String) {


    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 16.dp, end = 16.dp, bottom = 2.dp)
    ) {
        Text(
            text = if (selectedMessageSenderName == "") stringResource(MokoRes.strings.you) else selectedMessageSenderName,
            style = TextStyle(
                color = Color(0xff000000),
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
            ),

            )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(Res.drawable.call_microphone_on),
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier

                    .size(14.dp),
                colorFilter = ColorFilter.tint(Color(0xFF979797))
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = stringResource(MokoRes.strings.voice_message),
                style = TextStyle(
                    color = Color(0xff979797),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                ),
            )

        }
    }
}