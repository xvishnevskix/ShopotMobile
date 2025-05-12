package videotrade.parkingProj.presentation.components.Common.Common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun CustomImage(
    image: DrawableResource,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    size: Dp = Dp.Unspecified,            // общий размер
    width: Dp = Dp.Unspecified,           // конкретная ширина
    height: Dp = Dp.Unspecified,          // конкретная высота
    contentDescription: String? = null
) {
    val finalModifier = modifier.then(
        when {
            width != Dp.Unspecified && height != Dp.Unspecified -> Modifier.size(width, height)
            width != Dp.Unspecified -> Modifier.width(width)
            height != Dp.Unspecified -> Modifier.height(height)
            size != Dp.Unspecified -> Modifier.size(size)
            else -> Modifier
        }
    )

    Image(
        painter = painterResource(image),
        contentDescription = contentDescription,
        modifier = finalModifier,
        colorFilter = if (tint != Color.Unspecified) ColorFilter.tint(tint) else null
    )
}