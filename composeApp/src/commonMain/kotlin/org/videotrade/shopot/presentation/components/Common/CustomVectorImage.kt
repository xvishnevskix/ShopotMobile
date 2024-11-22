import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomVectorImage(
    drawableRes: DrawableResource,
    tint: Color = Color.Black
) {
   Image(
        painter = painterResource( drawableRes),
        contentDescription = "Описание изображения",
        colorFilter = ColorFilter.tint(tint)
    )
}
