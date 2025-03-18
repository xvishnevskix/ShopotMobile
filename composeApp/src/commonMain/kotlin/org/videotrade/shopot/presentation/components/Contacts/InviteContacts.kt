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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.presentation.screens.contacts.CreateGroupFirstScreen
import org.videotrade.shopot.presentation.screens.contacts.InviteContactsScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.group
import shopot.composeapp.generated.resources.invite_contact

@Composable
fun InviteContacts(contacts: List<ContactDTO>) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .height(58.dp)
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = colors.secondaryContainer,
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .clickable {
                    if (contacts.isNotEmpty())
                        navigateToScreen(navigator,InviteContactsScreen())
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
                        painter = painterResource(Res.drawable.invite_contact),
                        contentDescription = "invite contacts arrow",
                        modifier = Modifier.size(width = 18.dp, height = 15.dp),
                        colorFilter =  ColorFilter.tint(colors.primary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(MokoRes.strings.invite_to_shopot),
                        modifier = Modifier
                        ,
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            color = colors.primary,
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    )
                }
                Box() {
                    Image(
                        painter = painterResource(Res.drawable.arrow_left),
                        contentDescription = "create group arrow",
                        modifier = Modifier.size(width = 7.dp, height = 14.dp).rotate(180f),
                        colorFilter =  ColorFilter.tint(colors.primary)
                    )
                }
            }
        }
    }
}