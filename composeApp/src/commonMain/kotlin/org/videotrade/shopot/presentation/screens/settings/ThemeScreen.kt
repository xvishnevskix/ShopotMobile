package org.videotrade.shopot.presentation.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
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
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.presentation.components.Auth.BaseHeader
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.arrow_left
import shopot.composeapp.generated.resources.half_moon


enum class ThemeMode {
    LIGHT, DARK
}

data class ThemeState(
    val currentTheme: ThemeMode = ThemeMode.LIGHT
)




class ThemeScreen : Screen {
    @Composable
    override fun Content() {

       val viewModel: SettingsViewModel = koinInject()
        val isDarkTheme by viewModel.isDarkTheme

        val colors = MaterialTheme.colorScheme

        SafeArea(backgroundColor = colors.background) {
            Column(
                modifier = Modifier.fillMaxWidth().background(colors.background)
            ) {

                BaseHeader(stringResource(MokoRes.strings.design), background = colors.background)



                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = stringResource(MokoRes.strings.mode),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                        fontWeight = FontWeight(500),
                        letterSpacing = TextUnit(0F, TextUnitType.Sp),
                        color = colors.primary
                    ),



                    )

                Spacer(modifier = Modifier.height(16.dp))


                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(size = 16.dp))
                        .border(width = 1.dp, color = colors.primary, shape = RoundedCornerShape(size = 16.dp))
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { viewModel.toggleTheme() } // Обработка касания
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(35.dp)
                                    .padding(end = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Image(
                                    modifier = Modifier.size(17.61.dp),
                                    painter = painterResource(Res.drawable.half_moon),
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds,
                                    colorFilter = ColorFilter.tint(colors.primary)
                                )
                            }
                            Text(
                                stringResource(MokoRes.strings.night_mode),
                                textAlign = TextAlign.Center,
                                fontSize = 16.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                fontWeight = FontWeight(400),
                                color = colors.primary,
                                letterSpacing = TextUnit(0F, TextUnitType.Sp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                        ) {
                            Switch(
                                checked = isDarkTheme,
                                onCheckedChange = { viewModel.toggleTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF32D74B),
                                    uncheckedThumbColor = Color.White
                                )
                            )
                        }
                    }
                }


            }
        }

    }
}