import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil3.compose.rememberAsyncImagePainter
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.multiplatform.FileProviderFactory
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.menu_video

@Composable
fun SelectedVideoMessage(attachments: List<Attachment>, selectedMessageSenderName: String, colorTitle: Color = Color.Black) {
    var photoFilePath = remember { mutableStateOf("") }
    val fileProvider by remember { mutableStateOf(FileProviderFactory.create()) }
    
    val imagePainter = rememberAsyncImagePainter(photoFilePath.value)
    
    LaunchedEffect(Unit) {
        
        val photoFileName = attachments[0].photoName
        
        val existingPhotoFile = photoFileName?.let {
            fileProvider.existingFileInDir(it, "imageCache")
        }
        
        if (!existingPhotoFile.isNullOrBlank()) {
            photoFilePath.value = existingPhotoFile
        }
    }
    
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        
        Image(
            painter = imagePainter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .size(40.dp, 40.dp)
        )
        
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
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0x80373533),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    ),
                )
                
            }
        }
    }
}