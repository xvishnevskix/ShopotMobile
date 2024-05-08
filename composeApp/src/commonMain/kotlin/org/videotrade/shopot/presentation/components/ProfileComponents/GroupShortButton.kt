import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.carbon_media_library
import shopot.composeapp.generated.resources.video_icon


@Composable
fun GroupShortButton(
    drawableRes: DrawableResource,
    width: Dp,
    height: Dp,
    text: String,
) {


    Box(
        modifier = Modifier
            .padding(top = 20.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFFFFF))
            .padding( top = 11.dp, bottom = 11.dp)
            .width(78.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
            ) {
                Image(
                    modifier = Modifier
                        .size(width, height),
                    painter = painterResource(drawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds
                )
            }
            Text(
                text,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                lineHeight = 15.sp,
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                modifier = Modifier.padding(top = 9.dp),
                color = Color(0xFF29303C)
            )
        }
    }
}
