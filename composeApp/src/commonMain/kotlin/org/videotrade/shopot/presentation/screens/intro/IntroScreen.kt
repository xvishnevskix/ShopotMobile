package org.videotrade.shopot.presentation.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.AppInitializer
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.clearAllNotifications
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.common.UpdateAppViewModel
import org.videotrade.shopot.presentation.screens.common.UpdateScreen
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.permissions.PermissionsScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo
import shopot.composeapp.generated.resources.logo


class IntroScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: IntroViewModel = koinInject()
        val updateAppViewModel: UpdateAppViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()

        LaunchedEffect(key1 = Unit) {
            if (сommonViewModel.isRestartApp.value) {
                navigator.push(MainScreen())
            }
        }

        AppInitializer()

        LaunchedEffect(key1 = Unit) {
            try {

                val isCheckVersion =
                    updateAppViewModel.checkVersion()  // Предполагаем, что checkVersion() - suspend-функция

                if (isCheckVersion) {
                    navigator.push(UpdateScreen())
                } else {

                    viewModel.navigator.value = navigator


                    val contactsNative =
                        PermissionsProviderFactory.create().checkPermission("contacts")
                    PermissionsProviderFactory.create().getPermission("notifications")




                    if (!contactsNative) {
                        navigator.replace(PermissionsScreen())
                        return@LaunchedEffect
                    }


                    val response = origin().reloadTokens()




                    if (response != null) {

                        сommonViewModel.setMainNavigator(navigator)

                        сommonViewModel.cipherShared(response, navigator)


                        return@LaunchedEffect


                    }


                    navigator.replace(WelcomeScreen())
                }

            } catch (e: Exception) {

                navigator.replace(WelcomeScreen())

            }

        }



        Box(modifier = Modifier.fillMaxSize()
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

                Image(
                    modifier = Modifier
                        .size(width = 195.dp, height = 132.dp),
                    painter = painterResource(Res.drawable.auth_logo),
                    contentDescription = null,

                    )

        }

    }


}