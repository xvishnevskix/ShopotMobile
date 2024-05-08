package org.videotrade.shopot.presentation.screens.group

import Avatar
import GroupUserCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.Font
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.person
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import org.videotrade.shopot.presentation.components.ProfileComponents.GroupEditHeader
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.add_photo


class GroupEditScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val textState = remember { mutableStateOf("Работа над проектом") }
        val textStyle = TextStyle(
            color = Color.Black,
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
            lineHeight = 20.sp,
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {

            Column {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomEnd = 46.dp, bottomStart = 46.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(16.dp)
                ) {
                    GroupEditHeader("Изменить")
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Avatar(drawableRes = Res.drawable.person, size = 70.dp)
                        BasicTextField(
                            value = textState.value,
                            onValueChange = { newText -> textState.value = newText },
                            singleLine = true,
                            textStyle = textStyle,
                            cursorBrush = SolidColor(Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(16.dp),
                            decorationBox = { innerTextField ->

                                Column {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Transparent)
                                            .padding(8.dp)
                                    ) {

                                        if (textState.value.isEmpty()) {
                                            Text("Введите текст", style = textStyle.copy(color = Color.Gray))
                                        }
                                        innerTextField()
                                    }
                                    Divider(color = Color(0xFF8E8E93), thickness = 1.dp)
                                }
                            }

                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 25.dp, bottom = 10.dp).fillMaxWidth(0.95F),
                        horizontalArrangement = Arrangement.Start,

                    ) {
                        Image(
                            painter = painterResource(Res.drawable.add_photo),
                            contentDescription = "Avatar",
                            modifier = Modifier.size(width = 27.75.dp, height = 20.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Text(
                            "Загрузить фотографию",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 15.sp,
                            color = Color(0xFF2A293C),
                                    modifier = Modifier.padding(start = 10.dp)
                        )
                    }


                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 40.dp, bottom = 13.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        "Редактировать участников",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                        lineHeight = 15.sp,
                        color = Color(0xFF000000)
                    )
                }


                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)
                                GroupUserCard(isEdit = true)

                            }
                        }
                }
            }
        }
    }
}


