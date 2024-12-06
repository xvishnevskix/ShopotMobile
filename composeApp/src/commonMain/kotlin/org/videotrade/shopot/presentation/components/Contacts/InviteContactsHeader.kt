package org.videotrade.shopot.presentation.components.Contacts


import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.components.Common.BackIcon
import org.videotrade.shopot.presentation.screens.contacts.InviteContactsScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.invite_contact
import shopot.composeapp.generated.resources.search_icon

@Composable
fun InviteContactsHeader(
//    text: String,
    isSearching: MutableState<Boolean>,
) {
    val navigator = LocalNavigator.currentOrThrow
    val colors = MaterialTheme.colorScheme


    Column(
        modifier = Modifier.background(colors.background).padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(35.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.clickable {
                    navigator.pop()
                }.padding(start = 8.dp, end = 8.dp)) {
                    BackIcon()
                }
                Text(
                    text = stringResource(MokoRes.strings.invite),
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    color = colors.primary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp),

                    )
                Box(Modifier.size(20.dp)) {
                    Crossfade(targetState = isSearching.value) { searching ->
                        if (!searching) {
                            Row {
                                Image(
                                    painter = painterResource(Res.drawable.search_icon),
                                    contentDescription = "Search",
                                    modifier = Modifier
                                        .padding(end = 2.dp)
                                        .size(18.dp)
                                        .pointerInput(Unit) {
                                            isSearching.value = true
                                        },
                                    colorFilter = ColorFilter.tint(colors.primary)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    Box(Modifier.background(colors.background)) {
        CallBar()
    }
}