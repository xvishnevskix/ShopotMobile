package org.videotrade.shopot.presentation.screens.intro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
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
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.AppInitializer
import org.videotrade.shopot.multiplatform.NetworkHelper
import org.videotrade.shopot.multiplatform.NetworkStatus
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.checkNetwork
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

        LaunchedEffect(key1 = Unit) {
            if (сommonViewModel.isRestartApp.value) {
                navigator.push(MainScreen())
            }
        }

        AppInitializer()

        LaunchedEffect(key1 = Unit) {
            try {


                if (checkNetwork()) {
                    val isCheckVersion = false
//                        updateAppViewModel.checkVersion()  // Предполагаем, что checkVersion() - suspend-функция

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


                        val response = origin().reloadTokens(navigator)

                        if (response != null) {

                            сommonViewModel.setMainNavigator(navigator)

                            сommonViewModel.cipherShared(response, navigator)


                            return@LaunchedEffect


                        }
                        println("dasdadasadsad")
//                        navigator.replace(WelcomeScreen())


                    }
                } else {
                    navigator.replace(NetworkErrorScreen())
                }

            } catch (e: Exception) {
                navigator.replace(NetworkErrorScreen())
            }

        }


        val offsetY = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            offsetY.animateTo(
                targetValue = 30f, // Максимальная высота перемещения
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000), // Длительность 1 секунда
                    repeatMode = RepeatMode.Reverse // Возврат вниз
                )
            )
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

                Box(
                    modifier = Modifier
                        .size(65.dp, 70.dp) // Жёстко заданный размер Box
                        .background(Color.Transparent) // Для проверки размеров Box
                        .align(Alignment.CenterHorizontally) // Центрирование по горизонтали
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize() // Canvas занимает всё пространство Box

                    ) {
                        // Масштабирование для корректного отображения внутри Canvas
                        val scaleX = size.width / 195f
                        val scaleY = size.height / 132f

                        scale(2.7f, 2.7f) {
                            // Путь 1
                            val path1 = Path().apply {
                                moveTo(136.5f, 0f)
                                cubicTo(138.98f, 0f, 141f, 1.97f, 141f, 4.4f)
                                lineTo(141f, 52.52f)
                                cubicTo(137.74f, 97.16f, 110.59f, 121.12f, 80.29f, 129.01f)
                                cubicTo(50.55f, 136.75f, 17.05f, 129.11f, 1f, 109.76f)
                                cubicTo(-0.57f, 107.87f, -0.27f, 105.1f, 1.66f, 103.57f)
                                cubicTo(3.59f, 102.04f, 6.43f, 102.33f, 7.99f, 104.21f)
                                cubicTo(21.17f, 120.11f, 50.57f, 127.64f, 77.97f, 120.51f)
                                cubicTo(104.78f, 113.52f, 128.99f, 92.59f, 132f, 52.21f)
                                lineTo(132f, 4.4f)
                                cubicTo(132f, 1.97f, 134.01f, 0f, 136.5f, 0f)
                                close()
                            }
                            drawPath(path1, Color(0xFF373533), style = Fill)

                            // Путь 2
                            val path2 = Path().apply {
                                moveTo(163.5f, 0f)
                                cubicTo(165.98f, 0f, 168f, 1.97f, 168f, 4.4f)
                                lineTo(168f, 52.52f)
                                cubicTo(164.74f, 97.16f, 137.59f, 121.12f, 107.29f, 129.01f)
                                cubicTo(77.55f, 136.75f, 44.05f, 129.11f, 28f, 109.76f)
                                cubicTo(26.43f, 107.87f, 26.73f, 105.1f, 28.66f, 103.57f)
                                cubicTo(30.59f, 102.04f, 33.43f, 102.33f, 34.99f, 104.21f)
                                cubicTo(48.17f, 120.11f, 77.57f, 127.64f, 104.97f, 120.51f)
                                cubicTo(131.78f, 113.52f, 155.99f, 92.59f, 159f, 52.21f)
                                lineTo(159f, 4.4f)
                                cubicTo(159f, 1.97f, 161.01f, 0f, 163.5f, 0f)
                                close()
                            }
                            drawPath(path2, Color(0xFF373533), style = Fill)

                            // Путь 3
                            val path3 = Path().apply {
                                moveTo(190.5f, 0f)
                                cubicTo(192.98f, 0f, 195f, 1.97f, 195f, 4.4f)
                                lineTo(195f, 52.52f)
                                cubicTo(191.74f, 97.16f, 164.59f, 121.12f, 134.29f, 129.01f)
                                cubicTo(104.55f, 136.75f, 71.05f, 129.11f, 55f, 109.76f)
                                cubicTo(53.43f, 107.87f, 53.73f, 105.1f, 55.66f, 103.57f)
                                cubicTo(57.59f, 102.04f, 60.43f, 102.33f, 61.99f, 104.21f)
                                cubicTo(75.17f, 120.11f, 104.57f, 127.64f, 131.97f, 120.51f)
                                cubicTo(158.78f, 113.52f, 182.99f, 92.59f, 186f, 52.21f)
                                lineTo(186f, 4.4f)
                                cubicTo(186f, 1.97f, 188.01f, 0f, 190.5f, 0f)
                                close()
                            }
                            drawPath(path3, Color(0xFF373533), style = Fill)

                            // Анимируемый путь 4
                            translate(top = offsetY.value) {
                                val path4 = Path().apply {
                                    addOval(
                                        androidx.compose.ui.geometry.Rect(
                                            left = 100f,
                                            top = 5.5f,
                                            right = 111f,
                                            bottom = 16.5f
                                        )
                                    )
                                }
                                drawPath(path4, Color(0xFF373533), style = Fill)
                            }

                            // Путь 5 (статичный круг)
                            val path5 = Path().apply {
                                addOval(
                                    androidx.compose.ui.geometry.Rect(
                                        left = 100f,
                                        top = 35.5f,
                                        right = 111f,
                                        bottom = 46.5f
                                    )
                                )
                            }
                            drawPath(path5, Color(0xFF373533), style = Fill)
                        }
                    }

                }

                Box(
                    modifier = Modifier.padding(bottom = 80.dp)
                ) {

                    Text(
//                        text = "${MokoRes.strings.app_version}: alpha~1.0.6",
                        text = "App Version: alpha~",
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