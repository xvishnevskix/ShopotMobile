package org.videotrade.shopot.presentation.screens.apps


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Apps.AppsHeader
import org.videotrade.shopot.parkingProj.presentation.components.SafeArea
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.parking_app

class AppsScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(colors.surface)
        ) {
            SafeArea(padding = if (getPlatform() == Platform.Android) 0.dp else 16.dp)

            {
                Column(
                    Modifier.background(colors.background)
                ) {
                    AppsHeader(
                        text = stringResource(MokoRes.strings.applications),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier.padding(24.dp).clickable {

                        }
                    ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(Res.drawable.parking_app),
                                    contentDescription = "Image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(82.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "Парковки\nКиргизии",
                                    style = TextStyle(
                                        color = colors.primary
                                        ,
                                        fontSize = 14.sp,
                                        lineHeight = 14.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                        textAlign = TextAlign.Center
                                    ),
                                )
                            }
                    }
                }
            }
//                BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }

    }
}


