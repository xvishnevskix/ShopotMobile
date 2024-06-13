package org.videotrade.shopot.presentation.components.Main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.contacts.CreateChatScreen
import org.videotrade.shopot.presentation.screens.main.MainScreen
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.videotrade.shopot.presentation.screens.profile.ProfileScreen
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.chat
import shopot.composeapp.generated.resources.contacts
import shopot.composeapp.generated.resources.smart_encryption
import shopot.composeapp.generated.resources.user_profile

@Composable
fun MainContentComponent(drawerState: DrawerState, viewModel: MainViewModel) {
    val chatState = viewModel.chats.collectAsState(initial = listOf()).value
    val navigator = LocalNavigator.currentOrThrow




        Box(modifier = Modifier.fillMaxSize()) {
            SafeArea {
                Column(modifier = Modifier.fillMaxSize()) {
                    HeaderMain(drawerState)

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            "Чаты",
                            modifier = Modifier.padding(bottom = 15.dp, top = 5.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            color = Color(0xFF000000)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            items(chatState) { item ->
                                UserComponentItem(item)
                            }

                            if (chatState.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier.padding(top = 40.dp, bottom = 10.dp)
                                            .fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Image(
                                                modifier = Modifier.size(27.dp),
                                                painter = painterResource(Res.drawable.smart_encryption),
                                                contentDescription = null,
                                            )
                                        }

                                        Row {
                                            Text(
                                                "Все чаты зашифрованы  ",
                                                textAlign = TextAlign.Center,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF000000),
                                            )
                                            Text(
                                                "ассиметричным и постквантовым шифрованием",
                                                textAlign = TextAlign.Center,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF219653),
                                                textDecoration = TextDecoration.Underline,
                                            )
                                        }
                                    }
                                }
                            } else {
                                item {
                                    Column(
                                        modifier = Modifier.padding(bottom = 10.dp)
                                            .fillMaxSize()
                                            .clickable {
                                                navigator.push(CreateChatScreen())
                                            }
                                            .size(600.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Row {
                                            Text(
                                                "Создайте новый чат",
                                                modifier = Modifier,
                                                textAlign = TextAlign.Center,
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                                lineHeight = 20.sp,
                                                color = Color(0xFF979797)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BottomBar(modifier = Modifier.align(Alignment.BottomCenter))
        }
}

@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: MainViewModel = koinInject()
    val currentScreen by viewModel.currentScreen.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF1EEEE))
            .padding(start = 34.dp, end = 34.dp, top = 6.dp),

    ) {
        Row(
            modifier = modifier
                .fillMaxWidth().height(84.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier.clickable {
                    if (currentScreen !is ProfileScreen) {
                        viewModel.navigateTo(ProfileScreen())
                        navigator.push(ProfileScreen())
                    }
                }
            ) {
                Image(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(Res.drawable.user_profile),
                    contentDescription = null,
                )
                Text(
                    "Профиль",
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = if (currentScreen is ProfileScreen) Color(0xFF8dbfe5) else  Color(0xFF000000),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier.clickable {
                    if (currentScreen !is MainScreen) {
                        viewModel.navigateTo(MainScreen())
                        navigator.push(MainScreen())
                    }
                }
            ) {
                Image(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(Res.drawable.chat),
                    contentDescription = null,
                )
                Text(
                    "Чаты",
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
//                    color = Color(0xFF000000),
                    color = if (currentScreen is MainScreen) Color(0xFF8dbfe5) else  Color(0xFF000000),
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
                , modifier = Modifier.clickable {
                    if (currentScreen !is CreateChatScreen) {
                        viewModel.navigateTo(CreateChatScreen())
                        navigator.push(CreateChatScreen())
                    }
                }
            ) {
                Image(
                    modifier = Modifier.size(35.dp),
                    painter = painterResource(Res.drawable.contacts),
                    contentDescription = null,
                )
                Text(
                    "Контакты",
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                    letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                    lineHeight = 20.sp,
                    color = if (currentScreen is CreateChatScreen) Color(0xFF8dbfe5) else  Color(0xFF000000),
                )
            }
        }
    }
}