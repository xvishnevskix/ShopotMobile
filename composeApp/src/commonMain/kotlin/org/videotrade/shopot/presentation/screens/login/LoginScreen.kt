package org.videotrade.shopot.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea

import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import shopot.composeapp.generated.resources.LoginLogo
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium


class LoginScreen : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow


        val textState = remember { mutableStateOf("+7") }

        SafeArea {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {


                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        modifier = Modifier
                            .size(220.dp),
                        painter = painterResource(Res.drawable.LoginLogo),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                        )

                    Text(
                        "Добро пожаловать!",
                        fontSize = 28.sp,
                        fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp) ,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                    Text(
                        "Для того, чтобы продолжить \nавторизуйтесь",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                        letterSpacing = TextUnit(-0.5F, TextUnitType.Sp) ,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontWeight = FontWeight.W400,
                        color = Color(151,151,151)
                    )




                    PhoneInput(textState)

                    CustomButton(
                        "Войти",
                        {
                        navigator.push(LoginCallScreen(textState.value))

                    })
                }


            }

        }

    }


}