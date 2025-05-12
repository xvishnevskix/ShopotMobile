package org.videotrade.shopot.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.multiplatform.checkNetwork
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.parkingProj.presentation.components.SafeArea
import org.videotrade.shopot.presentation.screens.intro.IntroScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo


class NetworkErrorScreen : Screen {
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme
        val commonViewModel : CommonViewModel = koinInject()
        
        SafeArea(padding = 4.dp, backgroundColor = colors.background) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(1F)
                    .background(
                        colors.background
                    ).imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .imePadding()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .fillMaxHeight(0.85f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        
                        Image(
                            modifier = Modifier
                                .size(width = 195.dp, height = 132.dp),
                            painter = painterResource(Res.drawable.auth_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter =  ColorFilter.tint(colors.primary)
                        
                        )
                        
                        Spacer(modifier = Modifier.height(50.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(MokoRes.strings.network_title),
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    lineHeight = 24.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = colors.primary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp)
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                stringResource(MokoRes.strings.network_simple_title),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.secondary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp),
                            )
                            Spacer(modifier = Modifier.height(55.dp))
                            
                            CustomButton(stringResource(MokoRes.strings.network_button_title), {
                                if (checkNetwork()) {
                                    navigateToScreen(navigator,IntroScreen())
                                } else {
//                                    commonViewModel.toaster.show(
//                                        "Не удалось подключиться к сети",
//                                        type = ToastType.Error,
//                                        duration = ToasterDefaults.DurationDefault
//                                    )
                                }
                            
                            }, style = ButtonStyle.Gradient)
                        }
                    }
                }
            }
        }
        
        
        
    }
    
}