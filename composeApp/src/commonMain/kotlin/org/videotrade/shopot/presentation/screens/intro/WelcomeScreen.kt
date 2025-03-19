package org.videotrade.shopot.presentation.screens.intro

import FAQ
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.multiplatform.LanguageSelector
import org.videotrade.shopot.multiplatform.openUrl
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Auth.getPhoneNumberLength
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.auth.AuthCallScreen
import org.videotrade.shopot.presentation.screens.auth.sendRequestToBackend
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.signUp.SignUpPhoneScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.LoginLogo
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.auth_logo
import shopot.composeapp.generated.resources.logo
import shopot.composeapp.generated.resources.support


class WelcomeScreen : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()


        var logoVisible by remember { mutableStateOf(false) }
        var contentVisible by remember { mutableStateOf(false) }
        val logoOffsetY = remember { Animatable(0f) }
        val contentAlpha = animateFloatAsState(
            targetValue = if (contentVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 800)
        )

        // Launch animations when the screen is first composed
        LaunchedEffect(Unit) {
            logoOffsetY.animateTo(
                targetValue = -450f,
                animationSpec = tween(durationMillis = 1000)
            )
            logoVisible = true
            contentVisible = true
        }


            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(1F)
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

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = logoOffsetY.value
                            }
                            .size(width = 195.dp, height = 132.dp),
                        painter = painterResource(Res.drawable.auth_logo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Column(verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                ) {
                    Column(
                        modifier = Modifier.safeContentPadding().fillMaxWidth().fillMaxHeight(0.85f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    )    {

                            Box(modifier = Modifier.size(width = 195.dp, height = 132.dp))

//                        Spacer(modifier = Modifier.height(50.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.graphicsLayer {
                                    alpha = contentAlpha.value
                                }
                            ) {
                                Text(
                                    stringResource(MokoRes.strings.greeting),
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        lineHeight = 24.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                        fontWeight = FontWeight(500),
                                        textAlign = TextAlign.Center,
                                        color = Color(0xFF373533),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(
                                        MokoRes.strings.choose_to_continue
                                    ),
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
                                Spacer(modifier = Modifier.height(50.dp))
                                CustomButton(stringResource(MokoRes.strings.entrance), {
                                    navigateToScreen(navigator,SignInScreen())
                                }, style = ButtonStyle.Primary)
                                Spacer(modifier = Modifier.height(16.dp))
                                CustomButton(stringResource(MokoRes.strings.registration), {
                                    navigateToScreen(navigator,SignUpPhoneScreen())
                                }, style = ButtonStyle.Outline)
                            }

                    }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 20.dp).graphicsLayer {
                                alpha = contentAlpha.value
                            }.pointerInput(Unit) {
                                navigateToScreen(navigator,FAQ())
                            }
                        ) {
                            Image(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(Res.drawable.support),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                stringResource(MokoRes.strings.support),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF373533),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                )
                            )
                        }
                    }
                }

        }
    }
