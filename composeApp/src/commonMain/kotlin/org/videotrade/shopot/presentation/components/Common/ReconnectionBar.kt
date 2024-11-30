package org.videotrade.shopot.presentation.components.Common

import androidx.compose.animation.core.AnimationConstants
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.multiplatform.NetworkListener
import org.videotrade.shopot.multiplatform.NetworkStatus
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res

@Composable
fun ReconnectionBar() {
    val commonViewModel: CommonViewModel = koinInject()
    
    val isReconnectionWs = commonViewModel.isReconnectionWs.collectAsState()
    val networkListener: NetworkListener = koinInject()
    
    val networkStatus by networkListener.networkStatus.collectAsState(NetworkStatus.Connected)
    

    val colors = MaterialTheme.colorScheme
    val rotationAngle by animateFloatAsState(
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing,
                delayMillis = 0
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    if (networkStatus == NetworkStatus.Disconnected) {
        Row(
            modifier = Modifier
                .shadow(16.dp)
                .padding(top = 1.dp)
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .height(40.dp)
                .background(colors.background)
                .padding(horizontal = 24.dp, vertical = 2.dp)
                .clickable {

                }
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            

            Text(
                text = stringResource(MokoRes.strings.restoring_the_connection),
                fontSize = 16.sp,
                lineHeight = 16.sp,
                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                fontWeight = FontWeight(400),
                color = colors.primary,
                letterSpacing = TextUnit(0F, TextUnitType.Sp),
            )

            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colors.primary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp).graphicsLayer(
                        rotationZ = rotationAngle
                    )
                )
            }

        }
    }
}