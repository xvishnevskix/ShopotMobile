package org.videotrade.shopot.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.preat.peekaboo.image.picker.toImageBitmap
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.VideoPlayer
import org.videotrade.shopot.multiplatform.getAndSaveFirstFrame
import org.videotrade.shopot.multiplatform.getBuildVersion
import org.videotrade.shopot.presentation.components.Common.SafeArea
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res


class UpdateScreen : Screen {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {


            scope.launch {

            }

//            println("op $op")
        }

        MaterialTheme {
            SafeArea {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 20.dp), // Padding добавлен для отступов от краев
//                            verticalArrangement = Arrangement.Top, // Можно задать выравнивание по вертикали, если нужно
//                            horizontalAlignment = Alignment.End // Выравнивание по правому краю для всех элементов в колонке
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
//                                Image(
//                                    modifier = Modifier
//                                        .size(60.dp),
//                                    painter = painterResource(Res.drawable.LoginLogo),
//                                    contentDescription = null,
//                                    contentScale = ContentScale.Crop,
//                                    alignment = Alignment.Center,
//                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
//
//                        Text(
//                            text = "Обновление",
//                            fontSize = 16.sp,
//                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
//                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
//                            lineHeight = 20.sp,
//                            modifier = Modifier.padding(bottom = 5.dp),
//                            color = Color.Black
//                        )

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
                    }
                }

            }
        }
    }
}




