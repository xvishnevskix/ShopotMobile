package org.videotrade.shopot.presentation.screens.common

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
import com.preat.peekaboo.image.picker.toImageBitmap
import dev.icerock.moko.resources.compose.stringResource
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.VideoPlayer
import org.videotrade.shopot.multiplatform.appUpdate
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.multiplatform.getBuildVersion
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.login.SignInScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.signUp.SignUpPhoneScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res


class UpdateScreen : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val updateAppViewModel: UpdateAppViewModel = koinInject()

        val description = updateAppViewModel.description.collectAsState().value
        val appVersion = updateAppViewModel.appVersion.collectAsState().value

        // Состояние для вертикальной прокрутки
        val scrollState = rememberScrollState()

        MaterialTheme {
            SafeArea {
                Box(
                    modifier = Modifier.fillMaxSize() // Заполняет весь экран
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState) // Прокрутка
                            .padding(bottom = 20.dp), // Чтобы не было перекрытия с кнопкой
                        horizontalAlignment = Alignment.CenterHorizontally, // Центрирование по горизонтали
                        verticalArrangement = Arrangement.Center // Центрирование по вертикали
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp), // Padding добавлен для отступов от краев
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically, // Выровнять элементы по вертикали
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically, // Выровнять элементы по вертикали
                            ) {
                                Text(
                                    text = "Обновление приложения",
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    modifier = Modifier.padding(bottom = 5.dp, end = 10.dp),
                                    color = Color.Black
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp), // Padding добавлен для отступов от краев
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically, // Выровнять элементы по вертикали
                        ) {
                            Text(
                                text = "Версия: $appVersion",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(bottom = 5.dp),
                                color = Color.Black
                            )
                        }


                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "Что нового:\n" +
                                    "\n" +
                                    "\uD83D\uDE80 Улучшенная производительность: Ваши сообщения теперь доставляются ещё быстрее!\n" +
                                    "\uD83D\uDEE0\uFE0F Исправлены ошибки: Мы устранили несколько багов, чтобы сделать ваше общение ещё приятнее и стабильнее.\n" +
                                    "\uD83C\uDFA8 Обновленный дизайн: Новые иконки и улучшенный интерфейс для более комфортного использования.\n" +
                                    "\uD83D\uDD14 Уведомления: Теперь уведомления приходят мгновенно и настроены ещё удобнее.\n" +
                                    "\uD83D\uDCAC Новые функции чатов: Возможность закреплять важные сообщения и быстро отправлять GIF прямо из чата!\n" +
                                    "\uD83C\uDF10 Оптимизация данных: Меньшее потребление интернета без потери качества связи.\n" + "\n" +
                                    "Обновляйте приложение и наслаждайтесь новыми функциями! Спасибо, что остаетесь с нами \uD83D\uDCAC\n" +
                                    "\n",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                            color = Color.Black
                        )

                        // Кнопка по центру
                        CustomButton(
                            "Обновить",
                            {
                                it.launch {
                                    appUpdate()
                                }
                            },
                            style = ButtonStyle.Primary
                        )
                    }
                }
            }
        }
    }
}




