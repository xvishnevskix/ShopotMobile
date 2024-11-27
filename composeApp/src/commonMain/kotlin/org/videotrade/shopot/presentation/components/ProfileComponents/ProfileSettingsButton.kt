import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.screens.profile.ProfileEditScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.arrow_left

@Composable
fun ProfileSettingsButton(
    drawableRes: DrawableResource,
    width: Dp = 25.dp,
    height: Dp = 25.dp,
    mainText: String,
    onClick: () -> Unit
) {

    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .border(width = 1.dp, color = colors.secondaryContainer, shape = RoundedCornerShape(size = 16.dp))
            .clickable{
                onClick()
            }
            .background(Color.Transparent)
            .fillMaxWidth()
            .height(56.dp)
            .padding(16.dp)
        ,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Box(
                    modifier = Modifier.width(35.dp).padding(end = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        modifier = Modifier

                            .size(width = width, height = height),
                        painter = painterResource(drawableRes),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        colorFilter =  ColorFilter.tint(colors.primary)
                    )
                }
                Text(
                    "${mainText}",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                    fontWeight = FontWeight(400),
                    color = colors.primary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
                , modifier = Modifier
            ) {

                Image(
                    modifier = Modifier
                        .size(width = 7.dp, height = 14.dp).rotate(180f),
                    painter = painterResource(Res.drawable.arrow_left),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    colorFilter =  ColorFilter.tint(colors.primary)
                )
            }
        }
    }
}
