import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person

@Composable
fun Avatar(
    drawableRes: DrawableResource? = null,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier.size(size),
    contentScale: ContentScale = ContentScale.Crop
) {
    val imagePainter = if (drawableRes == null) {
        painterResource(Res.drawable.person)  // Ресурс по умолчанию
    } else {
        painterResource(drawableRes)  // Переданный ресурс
    }
    
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "Avatar",
            contentScale = contentScale,  // Используем contentScale как есть
            modifier = modifier,
        )
    }
}
