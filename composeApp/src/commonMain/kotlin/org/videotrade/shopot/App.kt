package org.videotrade.shopot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.setScreenLockFlags
import org.videotrade.shopot.presentation.screens.auth.CallPasswordScreen
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.settings.AppTheme
import org.videotrade.shopot.presentation.screens.settings.SettingsViewModel
import org.videotrade.shopot.presentation.screens.test.TestScreen

@Composable
internal fun App() {
    val viewModel: MainViewModel = koinInject()
    val commonViewModel: CommonViewModel = koinInject()
    val callViewModel: CallViewModel = koinInject()
    val settingsViewModel: SettingsViewModel = koinInject()
    val isDarkTheme by settingsViewModel.isDarkTheme
    
    
    AppTheme(darkTheme = isDarkTheme) {
        KoinContext {
            if (getPlatform() === Platform.Ios) {
                    Navigator(
                        IntroScreen()
//                        TestScreen()
                    ) { navigator ->
                        val appIsActive by commonViewModel.appIsActive.collectAsState()
                        
                        if (appIsActive) {
                            SlideTransition(navigator)
                        }
                    }
                
                
            } else {
                if (callViewModel.isCallBackground.value) {
                    isActiveCall(callViewModel)
                } else {
                    setScreenLockFlags(false)
                    
                    Navigator(
                    IntroScreen()
//                            CallPasswordScreen("79899236226", "SignIn")
//                        TestScreen()
                    ) { navigator ->
                        SlideTransition(navigator)
                    }
                }
                
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
            
            if (!callViewModel.isIncomingCall.value) {
                callViewModel.initWebrtc()
            }
        }
    }
    
    
    
    if (!isScreenOn) {
    }
    
    Navigator(
        CallScreen(user.id, null, user.firstName, user.lastName, user.phone)
    ) { navigator ->
        SlideTransition(navigator)
    }
    
}


