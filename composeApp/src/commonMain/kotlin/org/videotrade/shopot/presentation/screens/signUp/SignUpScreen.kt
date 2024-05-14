package org.videotrade.shopot.presentation.screens.login

import Avatar
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_Regular
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold
import shopot.composeapp.generated.resources.pencil_in_circle
import shopot.composeapp.generated.resources.person

data class SignUpTextState(
    var firstName: String = "",
    var lastName: String = "",
    var nickname: String = ""
)

class SignUpScreen() : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()

        val textState = remember { mutableStateOf(SignUpTextState()) }

        SafeArea {

            AuthHeader("Создать аккаунт", 0.75F)

            Box(
                modifier = Modifier.padding(top = 70.dp).fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box() {
                        Avatar(Res.drawable.person, 140.dp)
                        Image(
                            painter = painterResource(Res.drawable.pencil_in_circle),
                            contentDescription = "Редактировать",
                            modifier = Modifier.size(28.dp).align(Alignment.BottomEnd)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(1F),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Column(
                            modifier = Modifier.padding(top = 35.dp),
                        ) {
                            Text(
                                "Имя",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    bottom = 8.dp,
                                    start = 4.dp
                                ),
                                color = Color(0xFF000000)
                            )
                            BasicTextField(
                                value = textState.value.firstName,
                                onValueChange = {
                                    textState.value = textState.value.copy(firstName = it)
                                },
                                decorationBox = { innerTextField ->
                                    if (textState.value.firstName.isEmpty()) {
                                        Text(
                                            text = "Имя", style = TextStyle(
                                                color = Color(0xFFC7C7C7),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                lineHeight = 15.sp,
                                            )
                                        )
                                    }
                                    innerTextField()
                                },
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Start,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF000000)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .shadow(1.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .fillMaxWidth(0.9f).background(Color(0xFFFFFFFF))
                                    .padding(start = 15.dp, top = 19.dp, bottom = 15.dp)
                            )
                        }




                        Column(
                            modifier = Modifier.padding(top = 7.dp),
                        ) {
                            Text(
                                "Фамилия",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    bottom = 8.dp,
                                    start = 4.dp
                                ),
                                color = Color(0xFF000000)
                            )
                            BasicTextField(
                                value = textState.value.lastName,
                                onValueChange = {
                                    textState.value = textState.value.copy(lastName = it)
                                },
                                decorationBox = { innerTextField ->
                                    if (textState.value.lastName.isEmpty()) {
                                        Text(
                                            text = "Фамилия", style = TextStyle(
                                                color = Color(0xFFC7C7C7),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                lineHeight = 15.sp,
                                            )
                                        )
                                    }
                                    innerTextField()
                                },
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Start,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF000000)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .shadow(1.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .fillMaxWidth(0.9f).background(Color(0xFFFFFFFF))
                                    .padding(start = 15.dp, top = 19.dp, bottom = 15.dp)
                            )
                        }




                        Column(
                            modifier = Modifier.padding(top = 7.dp, bottom = 10.dp),
                        ) {
                            Text(
                                "Придумайте ник",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                                letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(
                                    top = 5.dp,
                                    bottom = 8.dp,
                                    start = 4.dp
                                ),
                                color = Color(0xFF000000)
                            )
                            BasicTextField(
                                value = textState.value.nickname,
                                onValueChange = {
                                    textState.value = textState.value.copy(nickname = it)
                                },
                                decorationBox = { innerTextField ->
                                    if (textState.value.nickname.isEmpty()) {
                                        Text(
                                            text = "Придумайте ник", style = TextStyle(
                                                color = Color(0xFFC7C7C7),
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                                                lineHeight = 15.sp,
                                            )
                                        )
                                    }
                                    innerTextField()
                                },
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Start,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily(Font(Res.font.Montserrat_Regular)),
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp),
                                    lineHeight = 20.sp,
                                    color = Color(0xFF000000)
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .shadow(1.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .fillMaxWidth(0.9f).background(Color(0xFFFFFFFF))
                                    .padding(start = 15.dp, top = 19.dp, bottom = 15.dp)
                            )
                        }

                    }

                    Box(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        CustomButton(
                            "Создать аккаунт",
                            {


                            })
                    }
                }
            }
        }
    }
}



