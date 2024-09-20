import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular

@Composable
fun SelectedMessageText(selectedMessage: MessageItem, selectedMessageSenderName: String) {
    Column(
        modifier = Modifier
            .padding(top = 5.dp, start = 22.dp, end = 22.dp, bottom = 2.dp)
    ) {
        Text(
            text = selectedMessageSenderName,
            style = TextStyle(
                color = Color(0xff000000),
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
            ),

            )
        selectedMessage.content?.let {
            Text(
                text = it,
                style = TextStyle(
                    color = Color(0xff979797),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                ),

                )
        }
    }
}