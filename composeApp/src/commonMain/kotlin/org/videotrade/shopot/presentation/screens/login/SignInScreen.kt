package org.videotrade.shopot.presentation.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
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
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.multiplatform.LanguageSelector
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.auth.AuthCallScreen
import org.videotrade.shopot.presentation.screens.signUp.SignUpPhoneScreen
import shopot.composeapp.generated.resources.LoginLogo
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Montserrat_SemiBold
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Medium


class SignInScreen : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        
        
        val textState =
            remember {
                mutableStateOf(
                    TextFieldValue(
                        text = "+7", selection = TextRange(3) //для правильной позиции курсора
                    )
                )
            }
        
        
        SafeArea {
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                
                
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        Image(
                            modifier = Modifier
                                .size(220.dp),
                            painter = painterResource(Res.drawable.LoginLogo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        
                        Text(
                            stringResource(MokoRes.strings.greeting),
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(Res.font.Montserrat_SemiBold)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                            color = Color.Black
                        
                        )
                        Text(
                            stringResource(MokoRes.strings.to_continue_please_log_in),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Medium)),
                            letterSpacing = TextUnit(-0.5F, TextUnitType.Sp),
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontWeight = FontWeight.W400,
                            color = Color(151, 151, 151)
                        )
                        
                        
                        
                        
                        Box(modifier = Modifier.padding(top = 25.dp, bottom = 25.dp)) {
                            PhoneInput(textState)
                        }
                        
                        
                        CustomButton(
                            stringResource(MokoRes.strings.login),
                            {
                                navigator.push(
                                    AuthCallScreen(
                                        textState.value.text,
                                        
                                        "SignIn"
                                    )
                                )
                                
                            })
//                        LanguageSelector()
                        Spacer(modifier = Modifier.height(154.dp))
                        
                        Row(
                            modifier = Modifier.padding(10.dp).fillMaxWidth()
                                .clickable { navigator.push(SignUpPhoneScreen()) },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                stringResource(MokoRes.strings.do_not_have_an_account_yet),
                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFF979797),
                            )
                            Text(
                                " " + stringResource(MokoRes.strings.sign_up),
                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                color = Color(0xFF000000),
                                textDecoration = TextDecoration.Underline
                            )
                            
                        }
                    }
                    
                }
                
                
            }
            
        }
        
    }
    
    
}


