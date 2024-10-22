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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.preat.peekaboo.image.picker.toImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.multiplatform.imageAsync
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

val avatarCache = LruCache<String, ByteArray>(100) // Кэш для 100 аватарок

@Composable
fun Avatar(
    icon: String? = null,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.size(size).clickable {
        onClick?.invoke()
    },
    contentScale: ContentScale = ContentScale.Crop,
    bitmap: ImageBitmap? = null,
) {
//    val imageBitmap = remember(icon) {
//        mutableStateOf<ImageBitmap?>(null)
//    }
//
//    // Если icon не пустой и изображение еще не загружено
//    if (icon != null && imageBitmap.value == null) {
//        LaunchedEffect(icon) {
//            // Проверка кэша
//            val cachedImage = avatarCache[icon]
//            if (cachedImage != null) {
//                println("cachedImage31313131")
//                imageBitmap.value = cachedImage.toImageBitmap()
//            } else {
//                println("cachedIma1121")
//                val newByteArray = imageAsync(icon)
//                if (newByteArray != null) {
//                    avatarCache.put(icon, newByteArray)
//                    imageBitmap.value = newByteArray.toImageBitmap()
//                }
//            }
//        }
//    }
    
    val imageBitmap = getImageStorage(icon, icon, false)
    
    
    
    Surface(
        modifier = modifier,
        shape = CircleShape,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Avatar",
                contentScale = contentScale,
                modifier = Modifier.size(size)
            )
        } else if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
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


@Composable
fun getImageStorage(imageId: String?, imageName: String?, isCipher: Boolean): ImageBitmap? {
    val imageBitmap = remember(imageId) {
        mutableStateOf<ImageBitmap?>(null)
    }
    
    // Если icon не пустой и изображение еще не загружено
    if (imageId != null && imageBitmap.value == null) {
        LaunchedEffect(imageId) {
            // Проверка кэша
            val cachedImage = avatarCache[imageId]
            if (cachedImage != null) {
                println("cachedImage31313131")
                imageBitmap.value = cachedImage.toImageBitmap()
            } else {
                println("cachedIma1121")
                
                val newByteArray = imageName?.let { imageAsync(imageId, it, isCipher) }
                if (newByteArray != null) {
                    avatarCache.put(imageId, newByteArray)
                    imageBitmap.value = newByteArray.toImageBitmap()
                }
            }
        }
    }
    return imageBitmap.value
}

