package org.videotrade.shopot.presentation.components.Main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Call.CallBar
import org.videotrade.shopot.presentation.screens.contacts.CreateChatScreen
import org.videotrade.shopot.presentation.tabs.ChatsTab
import org.videotrade.shopot.presentation.tabs.ContactsTab
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.add_main
import shopot.composeapp.generated.resources.logo_main
import shopot.composeapp.generated.resources.search_icon

@Composable
fun HeaderMain(isSearching: MutableState<Boolean>,) {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия
    val colors = MaterialTheme.colorScheme
    val tabNavigator = LocalTabNavigator.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,

            ) {


                Text(
                    stringResource(MokoRes.strings.chats),
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                    fontWeight = FontWeight(500),
                    textAlign = TextAlign.Center,
                     color = colors.primary,
                    letterSpacing = TextUnit(0F, TextUnitType.Sp)

                )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Crossfade(targetState = isSearching.value) { searching ->
                if (!searching) {
                    Box(modifier = Modifier.padding(horizontal = 5.dp).pointerInput(Unit) {
                        isSearching.value = true
                    }) {
                        Image(
                            painter = painterResource(Res.drawable.search_icon),
                            contentDescription = "Search",
                            modifier = Modifier

                                .size(18.dp)
                                ,
                            colorFilter =  ColorFilter.tint(colors.primary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(11.dp))

            Box(modifier = Modifier.padding(horizontal = 5.dp).pointerInput(Unit) {
                tabNavigator.current = ContactsTab
            }) {
                Image(
                    painter = painterResource(Res.drawable.add_main),
                    contentDescription = "Search",
                    modifier = Modifier
                        .size(18.dp)
                    ,
                    colorFilter =  ColorFilter.tint(colors.primary)
                )
            }
        }

        }
        Box(modifier = Modifier.padding(top = 5.dp)) {
            CallBar()
        }
    }

}