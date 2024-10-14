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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.test.TestScreen
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
    val navigator = LocalNavigator.currentOrThrow


    if (isTimerRunning.value) {
        Row(
            modifier = Modifier
                .padding(top = 1.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFF2A293C))
                .padding(horizontal = 8.dp, vertical = 2.dp).clickable {
                    
                    if (navigator.lastItem !is CallScreen) {
                        // Выполняем навигацию только если мы не находимся на этом экране
                        callScreenInfo.value?.let { navigator.push(it) }
                    }
               
                }
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Box(modifier = Modifier.padding(start = 5.dp, end = 10.dp)) {
                Avatar(userIcon.value, 30.dp)
            }

            Text(
                text = "Вернуться к разговору",
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 16.dp),
            )

            Text(
                text = timerValue.value,
                color = Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                lineHeight = 20.sp,
                modifier = Modifier.padding(start = 16.dp),
            )
        }
    }
}