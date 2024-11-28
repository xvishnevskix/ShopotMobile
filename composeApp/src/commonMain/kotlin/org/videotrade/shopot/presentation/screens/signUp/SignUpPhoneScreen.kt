package org.videotrade.shopot.presentation.screens.signUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.CountryPickerBottomSheet
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Auth.getPhoneNumberLength
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.auth.AuthCallScreen
import org.videotrade.shopot.presentation.screens.auth.sendRequestToBackend
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFProText_Semibold
import shopot.composeapp.generated.resources.auth_logo

class SignUpPhoneScreen : Screen {


    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()
        val toasterViewModel: CommonViewModel = koinInject()
        val sendCode = stringResource(MokoRes.strings.code_sent)
        val phoneRegistered = stringResource(MokoRes.strings.phone_number_is_already_registered)
        var countryCode by remember { mutableStateOf("+7") }
        var hasError = remember { mutableStateOf(false) }
        val animationTrigger = remember { mutableStateOf(false) }
        val bottomSheetState =
            rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
        val keyboardController = LocalSoftwareKeyboardController.current
        var showPhoneMenu = remember { mutableStateOf(false) }
        val phone = remember {
            mutableStateOf(
                TextFieldValue(
                    text = "", selection = TextRange(1) //для правильной позиции курсора
                )
            )
        }


        val countries = listOf(
            "+7" to "\uD83C\uDDF7\uD83C\uDDFA   ${stringResource(MokoRes.strings.ru)}",
            "+375" to "\uD83C\uDDE7\uD83C\uDDFE   ${stringResource(MokoRes.strings.by)}",
            "+374" to "\uD83C\uDDE6\uD83C\uDDF2   ${stringResource(MokoRes.strings.am)}",
            "+996" to "\uD83C\uDDF0\uD83C\uDDEC   ${stringResource(MokoRes.strings.kg)}",
            "+992" to "\uD83C\uDDF9\uD83C\uDDEF   ${stringResource(MokoRes.strings.tj)}",
            "+995" to "\uD83C\uDDEC\uD83C\uDDEA   ${stringResource(MokoRes.strings.ge)}",
            "+998" to "\uD83C\uDDFA\uD83C\uDDFF   ${stringResource(MokoRes.strings.uz)}",
            "+371" to "\uD83C\uDDF1\uD83C\uDDFB   ${stringResource(MokoRes.strings.lv)}",
            "+63" to "\uD83C\uDDF5\uD83C\uDDED   ${stringResource(MokoRes.strings.ph)}"
        )

        LaunchedEffect(showPhoneMenu) {
            if (showPhoneMenu.value) {
                keyboardController?.hide()
            }
        }
        DisposableEffect(showPhoneMenu.value) {
            if (showPhoneMenu.value) {
                keyboardController?.hide()
            }
            onDispose { }
        }



        SafeArea(padding = 4.dp, backgroundColor = colors.background)
        {
            ModalBottomSheetLayout(
                sheetState = bottomSheetState,
                sheetContent = {

                    CountryPickerBottomSheet(
                        countries = countries,
                        showPhoneMenu = showPhoneMenu,
                        selectedCountryCode = countryCode,
                        onCountrySelected = { selectedCode ->
                            countryCode = selectedCode
                            val currentNumber = phone.value.text
                            phone.value = TextFieldValue(
                                text = currentNumber,
                                selection = TextRange(currentNumber.length)
                            )
                            coroutineScope.launch {
                                bottomSheetState.hide()
                            }
                        }

                    ) {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }

                },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(1F)
                        .background(
                            colors.background
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {


                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .imePadding()
                    ) {
                        AuthHeader(stringResource(MokoRes.strings.create_account))
                        Column(
                            modifier = Modifier.safeContentPadding().fillMaxWidth()
                                .fillMaxHeight(0.85f).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                modifier = Modifier
                                    .size(width = 195.dp, height = 132.dp),
                                painter = painterResource(Res.drawable.auth_logo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter =  ColorFilter.tint(colors.primary)
                            )

                            Spacer(modifier = Modifier.height(50.dp))

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                            ) {
                                Text(
                                    stringResource(MokoRes.strings.registration),
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        lineHeight = 24.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                        fontWeight = FontWeight(500),
                                        textAlign = TextAlign.Center,
                                        color = colors.primary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp)
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(MokoRes.strings.enter_your_phone_number),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                        fontWeight = FontWeight(400),
                                        textAlign = TextAlign.Center,
                                        color = colors.secondary,
                                        letterSpacing = TextUnit(0F, TextUnitType.Sp)
                                    )
                                )
                                Spacer(modifier = Modifier.height(50.dp))

                                PhoneInput(
                                    textState = phone,
                                    countryCode = countryCode,
                                    hasError = hasError.value,
                                    animationTrigger = animationTrigger.value,
                                    onCountrySelected = {
                                        coroutineScope.launch {
                                            bottomSheetState.show()
                                        }
                                    },
                                    showPhoneMenu = showPhoneMenu,
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                val requiredPhoneLength =
                                    stringResource(MokoRes.strings.required_phone_number_length)


                                Box( modifier = Modifier.padding(bottom = 20.dp)) {
                                    CustomButton(stringResource(MokoRes.strings.send_code), {
                                        coroutineScope.launch {
                                            val fullPhoneNumber = countryCode + phone.value.text



                                            val phoneNumberLength =
                                                getPhoneNumberLength(countryCode)
                                            hasError.value = false

                                            if (fullPhoneNumber.length < phoneNumberLength) {
                                                hasError.value = true
                                                toasterViewModel.toaster.show(
                                                    "$requiredPhoneLength $phoneNumberLength",
                                                    type = ToastType.Error,
                                                    duration = ToasterDefaults.DurationDefault
                                                )
                                                animationTrigger.value = !animationTrigger.value

                                            } else {
                                                val response =
                                                    sendRequestToBackend(
                                                        fullPhoneNumber,
                                                        NotifierManager.getPushNotifier()
                                                            .getToken(),
                                                        "auth/login",
                                                        toasterViewModel,
                                                        sendCode,
                                                        hasError = hasError,
                                                        animationTrigger = animationTrigger
                                                    )
                                                if (response == null) {
                                                    navigator.push(
                                                        AuthCallScreen(
                                                            fullPhoneNumber,
                                                            "SignUp"
                                                        )
                                                    )
                                                }
                                                hasError.value = false
                                                if (response != null) {
                                                    hasError.value = true
                                                    toasterViewModel.toaster.show(
                                                        phoneRegistered,
                                                        type = ToastType.Error,
                                                        duration = ToasterDefaults.DurationDefault
                                                    )
                                                    animationTrigger.value = !animationTrigger.value
                                                }

                                            }
                                        }
                                    }, style = ButtonStyle.Gradient)
                                }
                            }

                        }
                    }
                }
            }


        }
    }
}


//SafeArea {
//            
//            AuthHeader(stringResource(MokoRes.strings.create_account), 0.75F)
//            
//            Box(
//                modifier = Modifier.fillMaxSize().padding(top = 220.dp),
//                contentAlignment = Alignment.TopCenter
//            ) {
//                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
//                    item {
//                        Text(
//                            stringResource(MokoRes.strings.enter_your_phone_number),
//                            modifier = Modifier.padding(bottom = 5.dp),
//                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
//                            fontSize = 20.sp,
//                            textAlign = TextAlign.Center,
//                            letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
//                            lineHeight = 24.sp,
//                            color = Color(0xFF000000)
//                        )
//
//                        Spacer(modifier = Modifier.height(80.dp))
//
////                        PhoneInput(phone)
//
//                        Box(
//                            modifier = Modifier.padding(top = 20.dp)
//                        ) {
//                            val requiredPhoneLength = stringResource(MokoRes.strings.required_phone_number_length)
//                            CustomButton(
//                                stringResource(MokoRes.strings.send_code),
//                                {
//                                    coroutineScope.launch {
//                                        val response =
//                                            sendRequestToBackend(phone.value.text, NotifierManager.getPushNotifier().getToken(), "auth/login", toasterViewModel, sendCode)
//
//                                        val countryCode = phone.value.text.takeWhile { it.isDigit() || it == '+' }
//                                        val phoneNumberLength = getPhoneNumberLength(countryCode)
//                                        if (phone.value.text.length < phoneNumberLength) {
//                                            coroutineScope.launch {
//                                                toasterViewModel.toaster.show(
//                                                    "${requiredPhoneLength} $phoneNumberLength",
//                                                    type = ToastType.Error,
//                                                    duration = ToasterDefaults.DurationDefault
//                                                )
//                                            }
//                                        }
//                                        else if (response == null) {
//                                            navigator.push(
//                                                AuthCallScreen(
//                                                    phone.value.text,
//                                                    "SignUp"
//                                                )
//                                            )
//                                        }
//                                        else if (response != null) {
//                                            toasterViewModel.toaster.show(
//                                                phoneRegistered,
//                                                type = ToastType.Error,
//                                                duration = ToasterDefaults.DurationDefault
//                                            )
//                                        }
//
//
//                                    }
//                                }
//
//                            )
//                        }
//                    }
//                }
//            }
//        }