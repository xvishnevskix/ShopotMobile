import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
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
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.carbon_media_library


@Composable
fun ProfileSettingsButton(
    drawableRes: DrawableResource,
    width: Dp = 25.dp,
    height: Dp = 25.dp,
    mainText: String,
//    boxText: String,
    onClick: () -> Unit
) {


    Box(
        modifier = Modifier
            .padding(top = 15.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable{
                onClick()
            }
            .background(Color(0xFFF3F4F6))
            .fillMaxWidth(0.9F)
            .padding(start = 15.dp, top = 14.dp, end = 10.dp, bottom = 14.dp)
            ,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(0.dp)
            ) {
                Image(
                    modifier = Modifier
                        .padding(end = 18.dp)
                        .size(width = width, height = height),
                    painter = painterResource(drawableRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
//                    colorFilter = ColorFilter.tint(Color.Gray)
                )
                Text(
                    "${mainText}",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 15.sp,
                    color = Color(0xFF29303C)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Box(
//                    modifier = Modifier
//                        .clip(RoundedCornerShape(6.dp))
//                        .background(if (boxText.isEmpty()) Color.Transparent else Color(0xFF2A293C))
//                ) {
//                    Text(
//                        "${boxText}",
//                        textAlign = TextAlign.Center,
//                        fontSize = 12.sp,
//                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                        lineHeight = 20.sp,
//                        color = Color(0xFFFFFFFF),
//                        modifier = Modifier
//                            .padding(start = 6.dp, end = 6.dp, top = 1.dp, bottom = 1.dp)
//                    )
//
//                }
                Image(
                    modifier = Modifier
                        .size(width = 7.dp, height = 14.dp).padding(top = 5.dp),
                    painter = painterResource(Res.drawable.arrowleft),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
