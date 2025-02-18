import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.imageAsync
import shopot.composeapp.generated.resources.Avatar
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

val avatarCache = LruCache<String, ImageBitmap>(5000) // Кэш для 100 аватарок

@Composable
fun Avatar(
    icon: String? = null,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.size(size).clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        onClick?.invoke()
    },
    contentScale: ContentScale = ContentScale.Crop,
    roundedCornerShape: Dp = 16.dp,
) {
    val placeholderPainter = painterResource(Res.drawable.Avatar)
    
    val imagePainter = if (icon.isNullOrBlank()) {
        remember { mutableStateOf(placeholderPainter) }
    } else {
        getImageStorage(icon, icon, false)
    }
    
    
    Surface(
        modifier = modifier.clip(RoundedCornerShape(roundedCornerShape)),
//        shape = CircleShape,
    ) {
        Image(
            painter = imagePainter.value ?: painterResource(Res.drawable.Avatar),
            contentDescription = "Avatar",
            contentScale = contentScale,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun getImageStorage(imageId: String?, imageName: String?, isCipher: Boolean): State<Painter?> {
    val imagePainter = remember(imageId) { mutableStateOf<Painter?>(null) }
    
    if (getPlatform() == Platform.Android) {
        LaunchedEffect(imageId) {
            try {
                if (imageId != null) {
                    val cachedImage = avatarCache[imageId]
                    if (cachedImage != null) {
                        println("cachedImage31313131 ${cachedImage}")
                        imagePainter.value = BitmapPainter(cachedImage)
                    } else {
                        println("cachedIma1121")
                        val imageData = imageName?.let { imageAsync(imageId, it, isCipher) }
                        if (imageData != null) {
                            println("imageData $imageData")
                            avatarCache.put(imageId, imageData)
                            imagePainter.value = BitmapPainter(imageData)
                        }
                    }
                }
            } catch (e: Exception) {
                println("error getImageStorage $e")
            }
        }
    } else {
        imagePainter.value = rememberImagePainter(url = "${SERVER_URL}file/plain/$imageId")
    }
  
    return imagePainter
}