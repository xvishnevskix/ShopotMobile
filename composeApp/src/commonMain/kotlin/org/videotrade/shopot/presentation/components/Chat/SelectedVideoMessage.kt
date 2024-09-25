import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import coil3.compose.rememberAsyncImagePainter
import com.preat.peekaboo.image.picker.toImageBitmap
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.presentation.screens.chat.PhotoViewerScreen
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.menu_gallery
import shopot.composeapp.generated.resources.menu_video

@Composable
fun SelectedVideoMessage(attachments: List<Attachment>, selectedMessageSenderName: String) {



    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        attachments[0].photoByteArray?.toImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(40.dp, 40.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 2.dp)
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
                    painter = painterResource(Res.drawable.menu_video),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier

                        .size(20.dp),
                    colorFilter = ColorFilter.tint(Color(0xFF979797))
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = stringResource(MokoRes.strings.video),
                    style = TextStyle(
                        color = Color(0xff979797),
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    ),
                )

            }
        }
    }
}