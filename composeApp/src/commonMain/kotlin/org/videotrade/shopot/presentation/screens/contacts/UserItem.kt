package org.videotrade.shopot.presentation.screens.contacts

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.domain.model.ContactDTO
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.arrowleft
import shopot.composeapp.generated.resources.person
import shopot.composeapp.generated.resources.randomUser


@Composable
fun UserItem(item: ContactDTO) {

    Box(
        modifier = Modifier
            .padding(top = 22.dp)
            .background(Color(255, 255, 255))
            .fillMaxWidth()
            .clickable {  }

    ) {
        Column(
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding().fillMaxWidth()
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding()
                ) {
                    Avatar(Res.drawable.randomUser, 80.dp)
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = "${item.name}",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )
                        Text(
                            text = "${item.phone}",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF979797),
                            modifier = Modifier.padding(top = 13.dp)
                        )

                    }


                }
                Image(
                    painter = painterResource(Res.drawable.arrowleft),
                    contentDescription = "create group arrow",
                    modifier = Modifier.size(18.dp)
                )
            }
            Divider(
                color = Color(0xFFD9D9D9).copy(alpha = 0.43f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 22.dp)
            )
        }
    }
}