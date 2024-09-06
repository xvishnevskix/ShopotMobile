package org.videotrade.shopot.presentation.screens.signUp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Auth.getPhoneNumberLength
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.auth.AuthCallScreen
import org.videotrade.shopot.presentation.screens.auth.sendRequestToBackend
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFProText_Semibold

class SignUpPhoneScreen : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()
        val toasterViewModel: CommonViewModel = koinInject()

        val phone =  remember {
            mutableStateOf(
                TextFieldValue(
                    text = "+7", selection = TextRange(3) //для правильной позиции курсора
                )
            )
        }

        val sendCode = stringResource(MokoRes.strings.code_sent)
        val phoneRegistered = stringResource(MokoRes.strings.phone_number_is_already_registered)
        
        SafeArea {
            
            AuthHeader(stringResource(MokoRes.strings.create_account), 0.75F)
            
            Box(
                modifier = Modifier.fillMaxSize().padding(top = 220.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    item {
                        Text(
                            stringResource(MokoRes.strings.enter_your_phone_number),
                            modifier = Modifier.padding(bottom = 5.dp),
                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
                            lineHeight = 24.sp,
                            color = Color(0xFF000000)
                        )

                        Spacer(modifier = Modifier.height(80.dp))

                        PhoneInput(phone)

                        Box(
                            modifier = Modifier.padding(top = 20.dp)
                        ) {
                            val requiredPhoneLength = stringResource(MokoRes.strings.required_phone_number_length)
                            CustomButton(
                                stringResource(MokoRes.strings.send_code),
                                {
                                    coroutineScope.launch {
                                        val response =
                                            sendRequestToBackend(phone.value.text, NotifierManager.getPushNotifier().getToken(), "auth/login", toasterViewModel, sendCode)

                                        val countryCode = phone.value.text.takeWhile { it.isDigit() || it == '+' }
                                        val phoneNumberLength = getPhoneNumberLength(countryCode)
                                        if (phone.value.text.length < phoneNumberLength) {
                                            coroutineScope.launch {
                                                toasterViewModel.toaster.show(
                                                    "${requiredPhoneLength} $phoneNumberLength",
                                                    type = ToastType.Error,
                                                    duration = ToasterDefaults.DurationDefault
                                                )
                                            }
                                        }
                                        else if (response == null) {
                                            navigator.push(
                                                AuthCallScreen(
                                                    phone.value.text,
                                                    "SignUp"
                                                )
                                            )
                                        }
                                        else if (response != null) {
                                            toasterViewModel.toaster.show(
                                                phoneRegistered,
                                                type = ToastType.Error,
                                                duration = ToasterDefaults.DurationDefault
                                            )
                                        }


                                    }
                                }

                            )
                        }
                    }
                }
            }
        }
    }
}



