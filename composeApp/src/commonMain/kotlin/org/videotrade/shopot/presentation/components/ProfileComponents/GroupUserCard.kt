import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular


@Composable
fun GroupUserCard(
    groupUser: GroupUserDTO,
    viewModel: ChatViewModel
) {
    val colors = MaterialTheme.colorScheme
    val inContact = groupUser.phone.let {
      val findContact = viewModel.findContactByPhone(it)
      findContact != null
    }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Avatar(icon = "", size = 56.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier,
                ) {

                    Spacer(modifier = Modifier.height(8.dp))


                    if (inContact) {
                        Text(
                            "${groupUser.firstName} ${groupUser.lastName}",
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                            fontWeight = FontWeight(500),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }




                    Text(
                        text = "+${groupUser.phone}",
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = if (inContact) FontFamily(Font(Res.font.ArsonPro_Regular)) else FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(400),
                        color = if (inContact) colors.secondary else colors.primary,
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                    )
                }
            }
        }
}
