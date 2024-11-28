import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat_micro
import shopot.composeapp.generated.resources.menu_gallery

@Composable
fun SelectedVoiceMessage(
    selectedMessage: MessageItem,
    selectedMessageSenderName: String,
    colorTitle: Color = Color.Black,
    isFromUser: Boolean,
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
    ) {
        Text(
            text = if (selectedMessageSenderName == "") stringResource(MokoRes.strings.you) else selectedMessageSenderName,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                fontWeight = FontWeight(500),
                color = colorTitle,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            ),

            )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
//            Image(
//                painter = painterResource(Res.drawable.chat_micro),
//                contentDescription = "Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//
//                    .size(14.dp),
//                colorFilter = ColorFilter.tint(Color(0xFF979797))
//            )
//            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(MokoRes.strings.voice_message),
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = if (isFromUser)
                        colors.onTertiary

                    else
                        colors.secondary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                ),
            )

        }
    }
}