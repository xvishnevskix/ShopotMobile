package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.screens.contacts.CreateChatScreen
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.add_main
import shopot.composeapp.generated.resources.logo_main
import shopot.composeapp.generated.resources.search_main
import shopot.composeapp.generated.resources.settings_main

@Composable
fun HeaderMain() {
    val interactionSource =
        remember { MutableInteractionSource() }  // Создаем источник взаимодействия

    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,

        ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {


            Image(
                modifier = Modifier.size(50.dp).pointerInput(Unit) {
                
                
                },
                painter = painterResource(Res.drawable.logo_main),
                contentDescription = null,

                )

            Text(
                stringResource(MokoRes.strings.whisper),
                modifier = Modifier.padding(start = 7.dp),

                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                color = Color(0xFF000000)

            )
        }

//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//
//            Image(
//                modifier = Modifier.padding(end = 15.dp).size(22.dp).clickable(
//                    interactionSource = interactionSource, // Используем источник взаимодействия
//                    indication = null, // Указываем null, чтобы убрать анимацию при клике
//                    onClick = { }
//                ),
//
//                painter = painterResource(Res.drawable.search_main),
//                contentDescription = null,
//
//                )
//            Image(
//                modifier = Modifier.padding(end = 15.dp).size(30.dp).clickable {
//                    navigator.push(CreateChatScreen())
//                },
//                painter = painterResource(Res.drawable.add_main),
//                contentDescription = null,
//
//                )
//            Image(
//                modifier = Modifier.size(25.dp),
//                painter = painterResource(Res.drawable.settings_main),
//                contentDescription = null,
//
//                )
//        }

    }


}