package org.videotrade.shopot.presentation.components.Contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.screens.contacts.CreateGroupFirstScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.group

@Composable
 fun MakeGroup(contacts: List<ContactDTO>) {
    val navigator = LocalNavigator.currentOrThrow


    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .height(58.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0x33373533),
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .clickable {
                    if (contacts.isNotEmpty())
                        navigator.push(CreateGroupFirstScreen())
                },

            ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween
                , verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth().padding(vertical =  20.dp, horizontal = 16.dp )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start
                    , verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(Res.drawable.group),
                        contentDescription = "create group arrow",
                        modifier = Modifier.size(width = 18.dp, height = 15.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(MokoRes.strings.create_group),
                        modifier = Modifier
                        ,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    )
                }
                Box() {
                    Image(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = "create group arrow",
                        modifier = Modifier.size(width = 7.dp, height = 14.dp).rotate(180f)
                    )
                }
            }
        }
    }
}