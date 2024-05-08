import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun Avatar(
    drawableRes: DrawableResource,
    size: Dp = 40.dp
) {


    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,

    ) {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = "Avatar",
            modifier = Modifier.size(size),
        )
    }
}
