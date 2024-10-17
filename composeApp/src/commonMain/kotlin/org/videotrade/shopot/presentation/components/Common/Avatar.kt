
import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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

val avatarCache = LruCache<String, ByteArray>(100) // Кэш для 50 аватарок

@Composable
fun Avatar(
    icon: String? = null,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier.size(size).clickable {
        if (onClick != null) {
            onClick()
        }
    },
    contentScale: ContentScale = ContentScale.Crop,
    bitmap: ImageBitmap? = null,
) {

    val imagePainter = icon?.let { getImagePainter(it) }


    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
    ) {

        if (bitmap !== null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Avatar",
                contentScale = contentScale,  // Используем contentScale как есть
                modifier = modifier,
            )
            return@Surface
        }



        if (imagePainter !== null) {
            Image(
                bitmap = imagePainter.toImageBitmap(),
                contentDescription = "Avatar",
                contentScale = contentScale,  // Используем contentScale как есть
                modifier = modifier,

                )
        } else {
            Image(
                painter = painterResource(Res.drawable.person),
                contentDescription = "Avatar",
                contentScale = contentScale,  // Используем contentScale как есть
                modifier = modifier,
            )
        }


    }
}

@Composable
fun getImagePainter(icon: String): ByteArray? {
    // Проверяем, есть ли изображение в кэше памяти
    val cachedImage = avatarCache[icon]

    if (cachedImage != null) {
        return cachedImage
    } else {
        val newPainter = imageAsync(icon)

        if (newPainter != null) {
            avatarCache.put(icon, newPainter)
            return newPainter

        }
    }
    return null
}

