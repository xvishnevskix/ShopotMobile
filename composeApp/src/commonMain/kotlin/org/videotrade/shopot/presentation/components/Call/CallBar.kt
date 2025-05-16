package org.videotrade.shopot.presentation.components.Call

import Avatar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res


@Composable
fun CallBar() {
    val callViewModel: CallViewModel = koinInject()
    val isTimerRunning = callViewModel.isTimerRunning.collectAsState()
    val timerValue = callViewModel.timer.collectAsState()
    val userIcon = callViewModel.userIcon.collectAsState()
    val callScreenInfo = callViewModel.callScreenInfo.collectAsState()
    val commonViewModel: CommonViewModel = koinInject()
    val colors = MaterialTheme.colorScheme


    if (isTimerRunning.value) {
        Row(
            modifier = Modifier
                .clickable {

                    if (commonViewModel.mainNavigator.value?.lastItem !is CallScreen) {
                        // Выполняем навигацию только если мы не находимся на этом экране
                        callScreenInfo.value?.let { commonViewModel.mainNavigator.value?.push(it) }
                    }

                }
                .shadow(16.dp)
                .padding(top = 4.dp)
//                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .height(40.dp)
                .background(colors.background)
                .padding(horizontal = 24.dp, vertical = 2.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(modifier = Modifier) {
                Avatar(userIcon.value, 30.dp, roundedCornerShape = 6.dp)
            }

            Text(
                text = stringResource(MokoRes.strings.back_to_conversation),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )

            Text(
                text = timerValue.value,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )
        }
    }
}