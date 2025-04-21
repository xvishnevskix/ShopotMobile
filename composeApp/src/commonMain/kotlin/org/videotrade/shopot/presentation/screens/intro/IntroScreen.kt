package org.videotrade.shopot.presentation.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.incrementAppLaunchCounter
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.api.setFirstLaunchDateIfNotSet
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.AppInitializer
import org.videotrade.shopot.multiplatform.NetworkHelper
import org.videotrade.shopot.multiplatform.NetworkStatus
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.checkNetwork
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.multiplatform.iosCall.isActiveCallIos
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.components.Common.LogoLoading
import org.videotrade.shopot.presentation.components.Common.markSessionToday
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.common.NetworkErrorScreen
import org.videotrade.shopot.presentation.screens.common.UpdateAppViewModel
import org.videotrade.shopot.presentation.screens.common.UpdateScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.permissions.PermissionsScreen
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo


class IntroScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val introViewModel: IntroViewModel = koinInject()
        val commonViewModel: CommonViewModel = koinInject()
        val callViewModel: CallViewModel = koinInject()
        val isCallBackground by callViewModel.isCallBackground.collectAsState()
        
        if (isCallBackground) {
            if (getPlatform() == Platform.Ios) {
                isActiveCallIos(callViewModel, navigator)
            }
            return
        }
        
        LaunchedEffect(Unit) {
            if (commonViewModel.isRestartApp.value) {
                navigateToScreen(navigator, MainScreen())
                return@LaunchedEffect
            }
            
            try {
                if (!checkNetwork()) {
                    navigateToScreen(navigator, NetworkErrorScreen())
                    return@LaunchedEffect
                }
                
                val isCheckVersion = false // updateAppViewModel.checkVersion()
                
                if (isCheckVersion) {
                    navigateToScreen(navigator, UpdateScreen())
                    return@LaunchedEffect
                }
                
                introViewModel.navigator.value = navigator
                
                val hasContactsPermission = PermissionsProviderFactory.create()
                    .checkPermission("contacts")
                PermissionsProviderFactory.create().getPermission("notifications")
                
                if (!hasContactsPermission) {
                    navigateToScreen(navigator, PermissionsScreen())
                    return@LaunchedEffect
                }
                
                val response = origin().reloadTokens(navigator)
                
                if (response != null) {
                    commonViewModel.setMainNavigator(navigator)
                    commonViewModel.cipherShared(response, navigator)
                    return@LaunchedEffect
                }
                
                navigateToScreen(navigator, WelcomeScreen())
                
            } catch (e: Exception) {
                navigateToScreen(navigator, NetworkErrorScreen())
            } finally {
                incrementAppLaunchCounter()
            }
        }
        
        AppInitializer()
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFBBA796),
                            Color(0xFFEDDCCC),
                            Color(0xFFCAB7A3),
                            Color(0xFFEDDCCC),
                            Color(0xFFBBA796)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                LogoLoading()
                
                Box(modifier = Modifier.padding(bottom = 80.dp)) {
                    Text(
                        text = "App Version: 1.1.5",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            color = Color(0x80373533)
                        )
                    )
                }
            }
        }
    }
}
