package org.videotrade.shopot.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.ToastType
import com.dokar.sonner.ToasterDefaults
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
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.Platform
import org.videotrade.shopot.multiplatform.getFbToken
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.multiplatform.getPlatform
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.ButtonStyle
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.login.CountryName
import org.videotrade.shopot.presentation.screens.signUp.SignUpScreen
import shopot.composeapp.generated.resources.ArsonPro_Medium
import shopot.composeapp.generated.resources.ArsonPro_Regular
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.auth_logo

class AuthCallScreen(
    private val phone: String,
    private val authCase: String,
    private val selectCountryName: CountryName
) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val colors = MaterialTheme.colorScheme
        val responseState = remember { mutableStateOf<String?>("1111") }
        val otpFields = remember { mutableStateListOf("", "", "", "") }
        val isSuccessOtp = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()
        val toasterViewModel: CommonViewModel = koinInject()
        var time by remember { mutableStateOf(30) }
        var isRunning by remember { mutableStateOf(false) }
        var reloadSend by remember { mutableStateOf(false) }
        var isSmsMode by remember { mutableStateOf(false) }
        var isSms by remember { mutableStateOf(false) }

        val isLoading = remember { mutableStateOf(false) }

        val phoneNotRegistered = stringResource(MokoRes.strings.phone_number_is_not_registered)
        val invalidCode = stringResource(MokoRes.strings.invalid_code)
        val sentSMSCode = stringResource(MokoRes.strings.sms_with_code_sent)
        val serverUnavailable = stringResource(MokoRes.strings.the_server_is_temporarily_unavailable)
        var hasError = remember { mutableStateOf(false) }
        val animationTrigger = remember { mutableStateOf(false) }

        val scrollState = rememberScrollState()

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
                            serverUnavailable,
                        )
                    }
                    if (phone == "+79603412966") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                        )
                    }
                    if (phone == "+79063080529") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                        )
                    }
                    if (phone == "+79899236226") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                    if (phone == "+79388899885") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                    if (phone == "+375336483673") {
                        sendLogin(
                            phone,
                            navigator,
                            viewModel,
                            сommonViewModel,
                            toasterViewModel = toasterViewModel,
                            phoneNotRegistered,
                            serverUnavailable,
                        )
                    }
                }

                "SignUp" -> {
                    if (phone == "+79990000000") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+79899236226") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+79388899885") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+375336483673") {
                        sendSignUp(phone, navigator)
                    }
                    if (phone == "+375333805608") {
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
                    time = 30 // Сбрасываем таймер обратно
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
                    phoneNotRegistered = phoneNotRegistered,
                    serverUnavailable = serverUnavailable,
                )

                "SignUp" -> sendSignUp(phone, navigator)
            }
        }


        fun sendSms(sentSMSCode: String) {
            isSms = true
            coroutineScope.launch {
                try {
                    if (!isRunning && time == 30) {
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

                        val url = if (selectCountryName == CountryName.KZ) "2fa/kz" else "2fa"

                        val response = origin().post(url, jsonContent)

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
                } catch (e: Exception) {
                    println("${e} sendSms Exception")
                }
            }
        }

        fun sendCall()
        {

            println("sendCall")

            when (phone) {
                "+79990000000" -> return
                "+375336483673" -> return
                "+79899236226" -> return
                "+79388899885" -> return
            }

            coroutineScope.launch {
                if (!isRunning) {
                    isRunning = true
                    startTimer()
                }

                val url = if (selectCountryName == CountryName.KZ) "2fa/kz" else "2fa"

                val response = sendRequestToBackend(
                    phone,
                    null,
                    url,
                    toasterViewModel,
                    hasError = hasError,
                    animationTrigger = animationTrigger,
                    phoneNotRegistered = phoneNotRegistered,
                    serverUnavailable = serverUnavailable
                )

                println("responseSendCall $response")

                if (response != null) {
                    val jsonString = response.bodyAsText()
                    val jsonElement = Json.parseToJsonElement(jsonString)
                    val messageObject = jsonElement.jsonObject["message"]?.jsonObject
                    responseState.value = messageObject?.get("code")?.jsonPrimitive?.content

                    // Проверка, что все 4 цифры введены перед проверкой
//                    val otpText = otpFields.joinToString("")
//                    if (otpText.length == 4) {
//                        if (responseState.value == otpText) {
//                            handleAuthCase()
//                        } else {
//                            handleError(invalidCode)
//                        }
//                    }
                    handleAuthCase()
                } else {
                    if (authCase == "SignIn") {
                        handleError(invalidCode)
                    }
                }
            }
        }

        LaunchedEffect(Unit) {

            if (!isRunning) {
                isRunning = true
                startTimer()
            }
            sendCall()
        }






        SafeArea(padding = 4.dp, backgroundColor = colors.background) {
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(1F)
                    .background(
                        colors.background
                    )
                    .imePadding()
                ,
                contentAlignment = Alignment.TopCenter
            ) {


                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    when (authCase) {
                        "SignIn" -> AuthHeader(stringResource(MokoRes.strings.login))
                        "SignUp" -> AuthHeader(stringResource(MokoRes.strings.create_account))
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .fillMaxHeight().verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Image(
                            modifier = Modifier
                                .size(width = 195.dp, height = 132.dp),
                            painter = painterResource(Res.drawable.auth_logo),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            colorFilter = ColorFilter.tint(colors.primary)

                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(MokoRes.strings.confirm_the_number),
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
                                    color = colors.secondary,
                                    letterSpacing = TextUnit(0F, TextUnitType.Sp)
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
                            Box(modifier = Modifier.padding(bottom = 20.dp)) {
                                CustomButton(
                                    text = if (isRunning && time > 0) {
                                        if (isSmsMode) "${stringResource(MokoRes.strings.get_a_new_code)} ${time}" else "${
                                            stringResource(
                                                MokoRes.strings.receive_code_via_sms
                                            )
                                        } ${time}"
                                    } else {
                                        if (isSmsMode) stringResource(MokoRes.strings.get_a_new_code) else stringResource(
                                            MokoRes.strings.receive_code_via_sms
                                        )
                                    },
                                    {
                                        if (isSmsMode) {
                                            sendSms(sentSMSCode)
                                        } else {
//                                            sendCall()
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
}

suspend fun sendRequestToBackend(
    phone: String,
    notificationToken: String?,
    url: String,
    toasterViewModel: CommonViewModel,
    phoneNotRegistered: String = "Номер телефона не зарегистрирован",
    serverUnavailable: String = "Сервер временно недоступен",
    hasError: MutableState<Boolean>? = null,
    animationTrigger: MutableState<Boolean>? = null,
): HttpResponse? {
    val client = HttpClient(getHttpClientEngine()) { // или другой движок в зависимости от платформы

    }

    val voipToken = getValueInStorage("voipToken")

    try {
        val jsonContent = Json.encodeToString(
            buildJsonObject {
                put("phoneNumber", phone.drop(1))
                notificationToken?.let { put("notificationToken", it) }
                if (getPlatform() == Platform.Ios) put("voipToken", voipToken)
                put("deviceType", getPlatform().name)

            }
        )


        println("url $url ${jsonContent}")


        val response: HttpResponse = client.post("${EnvironmentConfig.SERVER_URL}$url") {
            contentType(ContentType.Application.Json)
            setBody(jsonContent)
        }

        println("url ${response.bodyAsText()} ${jsonContent}")


        if (response.status.isSuccess()) {

            return response


        } else {
            println("Failed to retrieve data: ${response.status.description}")

            if (response.bodyAsText() == "User not found") {
                hasError?.value = true
                toasterViewModel.toaster.show(
                    phoneNotRegistered,
                    type = ToastType.Error,
                    duration = ToasterDefaults.DurationDefault
                )
            }

            when (response.status.value) {


                500 -> {
                    toasterViewModel.toaster.show(
                        serverUnavailable,
                        type = ToastType.Error,
                        duration = ToasterDefaults.DurationDefault
                    )
                }
                502 -> {
                    toasterViewModel.toaster.show(
                        serverUnavailable,
                        type = ToastType.Error,
                        duration = ToasterDefaults.DurationDefault
                    )
                }
                503 -> {
                    toasterViewModel.toaster.show(
                        serverUnavailable,
                        type = ToastType.Error,
                        duration = ToasterDefaults.DurationDefault
                    )
                }
                504 -> {
                    toasterViewModel.toaster.show(
                        serverUnavailable,
                        type = ToastType.Error,
                        duration = ToasterDefaults.DurationDefault
                    )
                }
//                else -> {
//                    toasterViewModel.toaster.show(
//                        "Неизвестная ошибка (${response.status.value}). Попробуйте позже.",
//                        type = ToastType.Error,
//                        duration = ToasterDefaults.DurationDefault
//                    )
//                }
            }

// Триггер анимации, если нужно
            animationTrigger?.let {
                it.value = !it.value
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
    phoneNotRegistered: String = "Номер телефона не зарегистрирован",
    serverUnavailable: String = "Сервер временно недоступен",
) {
    println("sendLogin")

    val response =
        sendRequestToBackend(
            phone,
            getFbToken(),
            "auth/login",
            toasterViewModel,
            phoneNotRegistered = phoneNotRegistered,
            serverUnavailable = serverUnavailable
        )


    println("sadada ${response?.bodyAsText()}")
    println("sadada ${response?.bodyAsText()}")

    if (response != null) {

        val jsonString = response.bodyAsText()
        val jsonElement = Json.parseToJsonElement(jsonString).jsonObject
        println("jsonElement00000Login $jsonElement")

        val token = jsonElement["accessToken"]?.jsonPrimitive?.content
        val refreshToken =
            jsonElement["refreshToken"]?.jsonPrimitive?.content

        val deviceId =
            jsonElement["deviceId"]?.jsonPrimitive?.content

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

        deviceId?.let {
            addValueInStorage(
                "deviceId",
                it
            )
        }


        viewModel.updateNotificationToken()
//        navigateToScreen(navigator,MainScreen())

        viewModel.startObserving()

        сommonViewModel.cipherShared(userId, navigator)


    }
}


fun sendSignUp(phone: String, navigator: Navigator) {
    navigateToScreen(navigator,SignUpScreen(phone))

}




