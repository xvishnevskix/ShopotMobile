import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

@Composable
fun Avatar(
    icon: String? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier.size(size),
    contentScale: ContentScale = ContentScale.Crop,
    bitmap: ImageBitmap? = null
) {
    val imagePainter = if (icon == null) {
        painterResource(Res.drawable.person)
    } else {
        rememberImagePainter("${serverUrl}file/id/$icon")
    }
    
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
        
        Image(
            painter = imagePainter,
            contentDescription = "Avatar",
            contentScale = contentScale,  // Используем contentScale как есть
            modifier = modifier,
        )
        
        
    }
}
