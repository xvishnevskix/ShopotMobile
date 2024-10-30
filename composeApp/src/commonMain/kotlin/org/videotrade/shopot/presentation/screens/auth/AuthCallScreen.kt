package org.videotrade.shopot.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.icerock.moko.resources.compose.stringResource
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.CountryPickerBottomSheet
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.components.Auth.getPhoneNumberLength
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.signUp.SignUpScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold
import shopot.composeapp.generated.resources.auth_logo

class AuthCallScreen(private val phone: String, private val authCase: String) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>("1111") }
        val otpFields = remember { mutableStateListOf("", "", "", "") }
        val isSuccessOtp = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()
        val toasterViewModel: CommonViewModel = koinInject()
        var time by remember { mutableStateOf(10) }
        var isRunning by remember { mutableStateOf(false) }
        var reloadSend by remember { mutableStateOf(false) }
        var isSmsMode by remember { mutableStateOf(false) }
        var isSms by remember { mutableStateOf(false) }

        val isLoading = remember { mutableStateOf(false) }

        val phoneNotRegistered = stringResource(MokoRes.strings.phone_number_is_not_registered)
        val invalidCode = stringResource(MokoRes.strings.invalid_code)
        val sentSMSCode = stringResource(MokoRes.strings.sms_with_code_sent)
        var hasError = remember { mutableStateOf(false) }
        val animationTrigger = remember { mutableStateOf(false) }

        LaunchedEffect(key1 = Unit) {
            viewModel.navigator.value = navigator

            when (authCase) {
                "SignIn" -> {
                    if (phone == "+79990000000") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                        )
                    }
                }

                "SignUp" -> {
                    if (phone == "+79990000000") {
                        sendSignUp(phone, navigator)
                    }
                }
            }
        }



        fun startTimer() {
            coroutineScope.launch {
                while (isRunning && time > 0) {
                    delay(1000) // Задержка в 1 секунду
                    time -= 1 // Уменьшаем время на 1 секунду
                }
                if (time == 0) {
                    isRunning = false // Останавливаем таймер, когда достигнет 0
                    isSmsMode = true // Переходим в режим SMS после завершения таймера
                    time = 10 // Сбрасываем таймер обратно
                }
            }
        }

        fun handleError(errorMessage: String) {
            hasError.value = true
            isLoading.value = false
            animationTrigger.value = !animationTrigger.value
            toasterViewModel.toaster.show(
                message = errorMessage,
                type = ToastType.Warning,
                duration = ToasterDefaults.DurationDefault,
            )
        }

        suspend fun handleAuthCase() {
            when (authCase) {
                "SignIn" -> sendLogin(
                    phone,
                    navigator,
                    viewModel,
                    сommonViewModel = сommonViewModel,
                    toasterViewModel = toasterViewModel,
                    phoneNotRegistered = phoneNotRegistered
                )

                "SignUp" -> sendSignUp(phone, navigator)
            }
        }

        fun sendSms(sentSMSCode: String) {
            isSms = true
            coroutineScope.launch {
                if (!isRunning) {
                    isRunning = true
                    startTimer()
                }
                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("phoneNumber", phone.drop(1))
                        put("is_sms", true)
                    }
                )
                val response = origin().post("2fa", jsonContent)
                if (response != null) {
                    val jsonElement = Json.parseToJsonElement(response)
                    val messageObject = jsonElement.jsonObject["message"]?.jsonObject
                    responseState.value = messageObject?.get("code")?.jsonPrimitive?.content

                    toasterViewModel.toaster.show(
                        message = sentSMSCode,
                        type = ToastType.Success,
                        duration = ToasterDefaults.DurationDefault,
                    )


                    val otpText = otpFields.joinToString("")
                    if (otpText.length == 4) {
                        if (responseState.value == otpText) {
                            handleAuthCase()
                        } else {
                            handleError(invalidCode)
                        }
                    }
                } else {
                    handleError(invalidCode)
                }
            }
        }

        fun sendCall() {
            coroutineScope.launch {
                if (!isRunning) {
                    isRunning = true
                    startTimer()
                }

                val response = sendRequestToBackend(phone,
                    null,
                    "2fa",
                    toasterViewModel,
                    hasError = hasError,
                    animationTrigger = animationTrigger)
                if (response != null) {
                    val jsonString = response.bodyAsText()
                    val jsonElement = Json.parseToJsonElement(jsonString)
                    val messageObject = jsonElement.jsonObject["message"]?.jsonObject
                    responseState.value = messageObject?.get("code")?.jsonPrimitive?.content

                    // Проверка, что все 4 цифры введены перед проверкой
                    val otpText = otpFields.joinToString("")
                    if (otpText.length == 4) {
                        if (responseState.value == otpText) {
                            handleAuthCase()
                        } else {
                            handleError(invalidCode)
                        }
                    }
                } else {
                    handleError(invalidCode)
                }
            }
        }

        LaunchedEffect(Unit) {

            if (!isRunning) {
                isRunning = true
                startTimer()
            }
//            val response = sendRequestToBackend(phone, null, "2fa", toasterViewModel, hasError = hasError,
//                animationTrigger = animationTrigger)
//            if (response != null) {
//                val jsonString = response.bodyAsText()
//                val jsonElement = Json.parseToJsonElement(jsonString)
//                val messageObject = jsonElement.jsonObject["message"]?.jsonObject
//                responseState.value = messageObject?.get("code")?.jsonPrimitive?.content
//            }

            sendCall()
        }


        val isError = remember { mutableStateOf(false) }









        SafeArea(padding = 0.dp) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(1F)
                    .background(
                        Color.White
                    ).imePadding(),
                contentAlignment = Alignment.TopCenter
            ) {


                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .imePadding()
                ) {
                    when (authCase) {
                        "SignIn" -> AuthHeader(stringResource(MokoRes.strings.login))
                        "SignUp" -> AuthHeader(stringResource(MokoRes.strings.create_account))
                    }
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
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                        ) {
                            Text(
                                stringResource(MokoRes.strings.confirm_the_number),
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    lineHeight = 24.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Medium)),
                                    fontWeight = FontWeight(500),
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF373533)
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (!isSms)
                                    stringResource(MokoRes.strings.enter_last_4_digits_of_the_incoming_call)
                                else
                                    stringResource(MokoRes.strings.enter_the_code_from_the_sms_sent_to_the_number),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(Res.font.ArsonPro_Regular)),
                                    fontWeight = FontWeight(400),
                                    textAlign = TextAlign.Center,
                                    color = Color(0x80373533)
                                )
                            )
                            Spacer(modifier = Modifier.height(50.dp))

                            Otp(otpFields, isLoading.value, hasError.value, animationTrigger.value,
                                onOtpComplete = { otpText ->
                                    coroutineScope.launch {
                                        isLoading.value = true
                                        if (responseState.value == otpText) {
                                            handleAuthCase()
                                        } else {
                                            handleError(invalidCode)
                                            isLoading.value = false
                                        }
                                    }
                                }
                                )

                            Spacer(modifier = Modifier.height(16.dp))

                            CustomButton(
                                text = if (isRunning && time > 0) {
                                    if (isSmsMode) "${stringResource(MokoRes.strings.get_a_new_code)} ${time}" else "${stringResource(MokoRes.strings.receive_code_via_sms)} ${time}"
                                } else {
                                    if (isSmsMode) stringResource(MokoRes.strings.get_a_new_code) else stringResource(MokoRes.strings.receive_code_via_sms)
                                },
                                {
                                    if (isSmsMode) {
                                        sendSms(sentSMSCode)
                                    } else {
                                        sendCall()
                                    }
                                },
                                style = ButtonStyle.Gradient,
                                disabled = isRunning
                            )
                        }

                    }
                }
            }
        }
    }
}
//SafeArea {
//
//            when (authCase) {
//                "SignIn" -> AuthHeader(stringResource(MokoRes.strings.login))
//                "SignUp" -> AuthHeader(stringResource(MokoRes.strings.create_account))
//            }
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.TopCenter
//            ) {
//
//
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    ) {
//
//
//                        Spacer(modifier = Modifier.height(150.dp))
//                        Text(
//                            stringResource(MokoRes.strings.enter_last_4_digits_of_the_incoming_call),
//                            modifier = Modifier.padding(bottom = 10.dp),
//                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
//                            fontSize = 20.sp,
//                            textAlign = TextAlign.Center,
//                            letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
//                            lineHeight = 24.sp,
//                            color = Color.Black
//
//
//                        )
//                        Text(
//                            stringResource(MokoRes.strings.you_will_receive_a_call_to_your_number_enter_the_last_4),
//                            textAlign = TextAlign.Center,
//                            fontSize = 14.sp,
//                            fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
//                            lineHeight = 24.sp,
//                            modifier = Modifier.padding(bottom = 5.dp),
//                            color = Color(151, 151, 151)
//                        )
//
//
//
//                        Otp(otpFields, isLoading.value)
//
//
//                        CustomButton(
//                            stringResource(MokoRes.strings.confirm),
//                            {
//                                val otpText = otpFields.joinToString("")
//
//
//                                coroutineScope.launch {
//                                    isLoading.value = true
////                                    if (
////                                        responseState.value != otpText && !isSuccessOtp.value
////
////                                    ) {
////                                        isLoading.value = false
////                                        toasterViewModel.toaster.show(
////                                            message = invalidCode,
////                                            type = ToastType.Warning,
////                                            duration = ToasterDefaults.DurationDefault,
////                                        )
////                                        return@launch
////                                    }
//
//                                    when (authCase) {
//
//                                        "SignIn" -> sendLogin(
//                                            phone,
//                                            navigator,
//                                            viewModel,
//                                            сommonViewModel,
//                                            toasterViewModel = toasterViewModel,
//                                            phoneNotRegistered
//                                        )
//
//                                        "SignUp" -> sendSignUp(phone, navigator)
//                                    }
//
//                                }
//
//
//                            })
//
//
//                        Text(
//                            stringResource(MokoRes.strings.send_code_via_sms),
//                            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
//                            textAlign = TextAlign.Center,
//                            fontSize = 15.sp,
//                            lineHeight = 15.sp,
//                            color = Color(0xFF000000),
//                            textDecoration = TextDecoration.Underline,
//                            modifier = Modifier.padding(top = 20.dp)
//                                .clickable { navigator.push(AuthSMSScreen(phone, authCase)) }
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))
//
//
//
//                            Text(
//                                if (!isRunning) stringResource(MokoRes.strings.send_code_again) else "${stringResource(MokoRes.strings.you_can_resend_the_code_after)} $time",
//                                fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
//                                textAlign = TextAlign.Center,
//                                fontSize = 15.sp,
//                                lineHeight = 15.sp,
//                                color = Color(0xFF000000),
//                                textDecoration = if (!isRunning) TextDecoration.Underline else TextDecoration.None,
//                                modifier = Modifier.padding(top = 10.dp)
//                                    .clickable {
//                                        if (!isRunning) {
//                                            isRunning = true
//                                            reloadSend = false
//                                            coroutineScope.launch {
//                                                startTimer()
//
//                                                val response =
//                                                    sendRequestToBackend(phone, null, "2fa", toasterViewModel, "")
//
//                                                if (response != null) {
//                                                    val jsonString = response.bodyAsText()
//                                                    val jsonElement = Json.parseToJsonElement(jsonString)
//                                                    val messageObject = jsonElement.jsonObject["message"]?.jsonObject
//                                                    responseState.value = messageObject?.get("code")?.jsonPrimitive?.content
//
//
//                                                }
//                                            }
//                                        }
//                                        else {
//
//                                        }
//
//
//                                    }
//                            )
//                        Spacer(modifier = Modifier.height(300.dp))
//                    }
//                }
//
//
//            }
//
//        }


suspend fun sendRequestToBackend(
    phone: String,
    notificationToken: String?,
    url: String,
    toasterViewModel: CommonViewModel,
    phoneNotRegistered: String = "Номер телефона не зарегистрирован",
    hasError: MutableState<Boolean>? = null,
    animationTrigger: MutableState<Boolean>? = null,
): HttpResponse? {
    val client = HttpClient(getHttpClientEngine()) { // или другой движок в зависимости от платформы

    }


    try {
        val jsonContent = Json.encodeToString(
            buildJsonObject {
                put("phoneNumber", phone.drop(1))

                notificationToken?.let { put("notificationToken", it) }
            }
        )


        println("url $url ${jsonContent}")


        val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
            contentType(ContentType.Application.Json)
            setBody(jsonContent)
        }

        println("url ${response.bodyAsText()} ${jsonContent}")


        if (response.status.isSuccess()) {

            return response


        } else {
            println("Failed to retrieve data: ${response.status.description}")

            if (response.bodyAsText() == "User not found") {
                if (hasError != null) {
                    hasError.value = true
                }
                toasterViewModel.toaster.show(

                    phoneNotRegistered,
                    type = ToastType.Error,
                    duration = ToasterDefaults.DurationDefault
                )

                if (animationTrigger != null) {
                    animationTrigger.value = !animationTrigger.value
                }
            }

        }
    } catch (e: Exception) {
        println("Error111: $e")
    } finally {
        client.close()
    }

    return null
}


suspend fun sendLogin(
    phone: String,
    navigator: Navigator,
    viewModel: IntroViewModel,
    сommonViewModel: CommonViewModel,
    toasterViewModel: CommonViewModel,
    phoneNotRegistered: String = ""
) {


    val response =
        sendRequestToBackend(
            phone,
            NotifierManager.getPushNotifier().getToken(),
            "auth/login",
            toasterViewModel,
            phoneNotRegistered,
        )


    println("sadada ${response?.bodyAsText()}")
    println("sadada ${response?.bodyAsText()}")

    if (response != null) {

        val jsonString = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonString).jsonObject


        val token = jsonElement["accessToken"]?.jsonPrimitive?.content
        val refreshToken =
            jsonElement["refreshToken"]?.jsonPrimitive?.content

        val userId =
            jsonElement["userId"]?.jsonPrimitive?.content



        token?.let {
            addValueInStorage(
                "accessToken",
                token
            )
        }
        refreshToken?.let {
            addValueInStorage(
                "refreshToken",
                refreshToken
            )
        }



        viewModel.updateNotificationToken()
//        navigator.push(MainScreen())

        viewModel.startObserving()

        сommonViewModel.cipherShared(userId, navigator)


    }
}


fun sendSignUp(phone: String, navigator: Navigator) {


    navigator.push(SignUpScreen(phone))


}




