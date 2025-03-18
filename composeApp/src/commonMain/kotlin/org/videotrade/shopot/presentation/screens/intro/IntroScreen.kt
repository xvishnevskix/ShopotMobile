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
import org.videotrade.shopot.api.navigateToScreen
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
        val viewModel: IntroViewModel = koinInject()
        val updateAppViewModel: UpdateAppViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()
        val callViewModel: CallViewModel = koinInject()
        val isCallBackground by callViewModel.isCallBackground.collectAsState()
        
        if (getPlatform() == Platform.Ios) {
            if (isCallBackground) {
                isActiveCallIos(callViewModel, navigator)
            }
        }
        if (isCallBackground) {
            return
        }
        
        LaunchedEffect(key1 = Unit) {
            if (сommonViewModel.isRestartApp.value) {
                navigateToScreen(navigator,MainScreen())
            }
        }

        AppInitializer()

        LaunchedEffect(key1 = Unit) {
            try {


                if (checkNetwork()) {
                    val isCheckVersion = false
//                        updateAppViewModel.checkVersion()  // Предполагаем, что checkVersion() - suspend-функция

                    if (isCheckVersion) {
                        navigateToScreen(navigator,UpdateScreen())
                    } else {

                        viewModel.navigator.value = navigator


                        val contactsNative =
                            PermissionsProviderFactory.create().checkPermission("contacts")
                        PermissionsProviderFactory.create().getPermission("notifications")




                        if (!contactsNative) {
                            navigator.replace(PermissionsScreen())
                            return@LaunchedEffect
                        }


                        val response = origin().reloadTokens(navigator)

                        if (response != null) {

                            сommonViewModel.setMainNavigator(navigator)

                            сommonViewModel.cipherShared(response, navigator)


                            return@LaunchedEffect


                        }
                        println("dasdadasadsad")
                        navigator.replace(WelcomeScreen())


                    }
                } else {
                    navigator.replace(NetworkErrorScreen())
                }

            } catch (e: Exception) {
                navigator.replace(NetworkErrorScreen())
            }

        }





        Box(
            modifier = Modifier.fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFBBA796), // rgb(187, 167, 150)
                            Color(0xFFEDDCCC), // rgb(237, 220, 204)
                            Color(0xFFCAB7A3), // rgb(202, 183, 163)
                            Color(0xFFEDDCCC), // rgb(237, 220, 204)
                            Color(0xFFBBA796)  // rgb(187, 167, 150)
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

//                Image(
//                    modifier = Modifier
//                        .size(width = 195.dp, height = 132.dp),
//                    painter = painterResource(Res.drawable.auth_logo),
//                    contentDescription = null,
//
//                    )
                Spacer(modifier = Modifier.height(16.dp))

                LogoLoading()

                Box(
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {

                    Text(
//                        text = "${MokoRes.strings.app_version}: alpha~1.0.6",
                        text = "App Version: alpha~1.1.0",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                            fontWeight = FontWeight(400),
                            textAlign = TextAlign.Center,
                            color = Color(0x80373533),
                            letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        )
                    )
                }
            }

        }

    }


}