import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.imageAsync
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

val avatarCache = LruCache<String, ImageBitmap>(100) // Кэш для 100 аватарок

@Composable
fun Avatar(
    icon: String? = null,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.size(size).clickable {
        onClick?.invoke()
    },
    contentScale: ContentScale = ContentScale.Crop,
//    bitmap: ImageBitmap? = null,
) {
    val imagePainter = remember { mutableStateOf<Painter?>(null) }
    
    if (icon.isNullOrBlank()) {
        imagePainter.value = painterResource(Res.drawable.person)
    } else {
        if (getPlatform() == Platform.Android) {
            LaunchedEffect(icon) {
                val newImageBitmap = getImageStorage(icon, icon, false)
                imagePainter.value = newImageBitmap?.let { BitmapPainter(it) }
            }
        } else {
            imagePainter.value = rememberImagePainter(url = "${serverUrl}file/plain/$icon")
        }
    }

    
    Surface(
        modifier = modifier,
        shape = CircleShape,
    ) {
        if (imagePainter.value != null) {
            Image(
                painter = imagePainter.value!!,
                contentDescription = "Avatar",
                contentScale = contentScale,
                modifier = Modifier.size(size)
            )
        } else {
            Image(
                painter = painterResource(Res.drawable.person),
                contentDescription = "Avatar",
                contentScale = contentScale,
                modifier = Modifier.size(size)
            )
        }
    }
}


suspend fun getImageStorage(imageId: String?, imageName: String?, isCipher: Boolean): ImageBitmap? {
    // Если icon не пустой и изображение еще не загружено
    try {
        if (imageId != null) {
            // Проверка кэша
            val cachedImage = avatarCache[imageId]
            if (cachedImage != null) {
                println("cachedImage31313131")
                return cachedImage
            } else {
                println("cachedIma1121")
                
                val imageData = if (getPlatform() == Platform.Android) {
                    imageName?.let { imageAsync(imageId, it, isCipher) }
                } else {
                    null
                }
                
                
                if (imageData != null) {
                    println("imageData $imageData")
                    
                    // Попробуем декодировать массив байтов безопасно
                    avatarCache.put(imageId, imageData)
                    return imageData
                }
            }
        }
    } catch (e: Exception) {
        println("error getImageStorage $e")
    }
    return null
}

