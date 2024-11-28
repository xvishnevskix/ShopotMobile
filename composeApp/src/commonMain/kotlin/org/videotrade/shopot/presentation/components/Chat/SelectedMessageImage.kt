import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.domain.model.Attachment
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.menu_gallery

@Composable
fun SelectedMessageImage(
    attachments: List<Attachment>,
    selectedMessageSenderName: String,
    colorTitle: Color = Color.Black,
    isFromUser: Boolean,
) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme
    val imagePainter = remember { mutableStateOf<Painter?>(null) }
    
    
    if (getPlatform() == Platform.Android) {
        val fileId = attachments[0].fileId
        
        imagePainter.value = getImageStorage(fileId, fileId, false).value
        
    } else {
        val imageFilePath = remember(attachments[0].fileId) { mutableStateOf("") }
        
        imagePainter.value = rememberAsyncImagePainter(imageFilePath.value)
        
        val imageState = remember { mutableStateOf(imagePainter) }
        
        val url =
            "${EnvironmentConfig.serverUrl}file/id/${attachments[0].fileId}"
        LaunchedEffect(attachments[0].fileId) {
            val fileId = attachments[0].fileId
            val fileType = attachments[0].type
            
            val fileProvider = FileProviderFactory.create()
            val existingFile =
                fileProvider.existingFileInDir(fileId, fileType)
            
            if (!existingFile.isNullOrBlank()) {
                imageFilePath.value = existingFile
                println("existingFile ${existingFile}")
            } else {
                val filePath = fileProvider.downloadCipherFile(
                    url,
                    "image",
                    fileId,
                    "image"
                ) { newProgress ->
                    println("newProgress $newProgress")
                }
                if (filePath != null) {
                    imageFilePath.value = filePath
                }
                println("filePath $filePath")
            }
        }
        
    }
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            
            imagePainter.value?.let {
                Image(
                    painter = it,
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(36.dp, 36.dp)
                )
            }
            
            
            Column(
                modifier = Modifier
                    .padding(top = 0.dp, start = 8.dp, end = 8.dp)
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
                Row(verticalAlignment = Alignment.Top) {
//                    Image(
//                        painter = painterResource(Res.drawable.menu_gallery),
//                        contentDescription = "Image",
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier
//
//                            .size(20.dp),
//                        colorFilter = ColorFilter.tint(Color(0xFF979797))
//                    )
//                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = stringResource(MokoRes.strings.photo),
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
    }