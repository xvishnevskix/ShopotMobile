package org.videotrade.shopot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.setScreenLockFlags
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val viewModel: MainViewModel = koinInject()
    val commonViewModel: CommonViewModel = koinInject()
    val callViewModel: CallViewModel = koinInject()

    KoinContext {
        if (callViewModel.isCallBackground.value) {
            isActiveCall(callViewModel)
        } else {
            setScreenLockFlags(false)
            
            Navigator(
                IntroScreen()
            ) { navigator ->
                SlideTransition(navigator)
            }
        }
        
    }
}


@Composable
fun isActiveCall(callViewModel: CallViewModel) {
    val isScreenOn by callViewModel.isScreenOn.collectAsState()

    val profileId = getValueInStorage("profileId")
    
    val answerData = callViewModel.answerData.value


    val userJson =
        answerData?.jsonObject?.get("user")?.jsonObject


    val user =
        Json.decodeFromString<ProfileDTO>(userJson.toString())

    LaunchedEffect(Unit) {
        if (profileId != null) {

            callViewModel.callScreenInfo.value =
                CallScreen(user.id, null, user.firstName, user.lastName, user.phone)

            if (isScreenOn) {
                callViewModel.initWebrtc()
            }

            callViewModel.connectionBackgroundWs(profileId)
        }
    }



    if (!isScreenOn) {
        callViewModel.setIsIncomingCall(true)
    }

    Navigator(
        CallScreen(user.id, null, user.firstName, user.lastName, user.phone)
    ) { navigator ->
        SlideTransition(navigator)
    }
    
}

