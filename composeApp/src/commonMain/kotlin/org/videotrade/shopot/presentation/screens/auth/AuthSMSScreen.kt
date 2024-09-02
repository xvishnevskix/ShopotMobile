package org.videotrade.shopot.presentation.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.MokoRes
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import shopot.composeapp.generated.resources.Montserrat_Medium
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold

class AuthSMSScreen(private val phone: String, private val authCase: String) : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()
        var time by remember { mutableStateOf(60) }
        var isRunning by remember { mutableStateOf(false) }
        var reloadSend by remember { mutableStateOf(false) }
        
        LaunchedEffect(Unit) {
            if (!isRunning) {
                isRunning = true
                coroutineScope.launch {
                    while (isRunning && time > 0) {
                        delay(1000) // Задержка в 1 секунду
                        time -= 1 // Уменьшаем время на 1 секунду
                    }
                    if (time == 0) {
                        isRunning = false // Останавливаем таймер, когда достигнет 0
                        reloadSend = true
                        time = 60 // Сбрасываем таймер обратно на 60 секунд
                    }
                }
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

                println("jsonElement41414 $jsonElement")

                val messageObject = jsonElement.jsonObject["message"]?.jsonObject

                responseState.value = messageObject?.get("code")?.jsonPrimitive?.content

            }
            
        }
        
        
        val isError = remember { mutableStateOf(false) }
        
        
        val otpFields = remember { mutableStateListOf("", "", "", "") }
        
        
        
        
        SafeArea {
            AuthHeader("Вход", 0.55F)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    
                    Text(
                        "Введите код из СМС",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
                        lineHeight = 24.sp,
                        
                        )
                    Text(
                        "На ваш номер $phone поступит SMS с кодом. Введите код в поле ниже ",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(Res.font.SFCompactDisplay_Regular)),
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 5.dp),
                        color = Color(151, 151, 151)
                    )
                    
                    
                    
                    Otp(otpFields)
                    
                    
                    CustomButton(
                        stringResource(MokoRes.strings.confirm),
                        {
                            val otpText = otpFields.joinToString("")
                            
                            
                            coroutineScope.launch {
                                if (
                                    responseState.value != otpText && !isSuccessOtp.value
                                
                                ) {
                                    
                                    return@launch
                                }
                                
                                
                                when (authCase) {
                                    
                                    "SignIn" -> sendLogin(
                                        phone,
                                        navigator,
                                        viewModel,
                                        сommonViewModel
                                    )
                                    
                                    "SignUp" -> sendSignUp(phone, navigator)
                                }
                                
                            }
                            
                            
                        })
                    
                    if (reloadSend) {
                        Text(
                            "Отправить",
                            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF000000),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.padding(top = 25.dp)
                                .clickable {
                                    if (!isRunning) {
                                        isRunning = true
                                        reloadSend = false
                                        coroutineScope.launch {
                                            while (isRunning && time > 0) {
                                                delay(1000) // Задержка в 1 секунду
                                                time -= 1 // Уменьшаем время на 1 секунду
                                            }
                                            if (time == 0) {
                                                isRunning =
                                                    false // Останавливаем таймер, когда достигнет 0
                                                reloadSend = true
                                                time = 60 // Сбрасываем таймер обратно на 60 секунд
                                            }
                                            
                                            
                                        }
                                        
                                        
                                    }

                                    coroutineScope.launch {
                                        val jsonContent = Json.encodeToString(
                                            buildJsonObject {
                                                put("phoneNumber", phone.drop(1))
                                                put("is_sms", true)
                                            }
                                        )

                                        val response = origin().post("2fa", jsonContent)

                                        if (response != null) {
                                            val jsonElement = Json.parseToJsonElement(response)

                                            println("jsonElement41414 $jsonElement")

                                            val messageObject =
                                                jsonElement.jsonObject["message"]?.jsonObject

                                            responseState.value =
                                                messageObject?.get("code")?.jsonPrimitive?.content

                                        }
                                    }
                                }
                        )
                    } else {
                        if (isRunning) Text(
                            text = "Повторно отправить через: $time секунд",
                            fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            lineHeight = 15.sp,
                            color = Color(0xFF000000),
                            modifier = Modifier.padding(top = 25.dp)
                        )
                    }
                }
                
                
            }
            
        }
        
    }
    
    
}


//suspend fun sendRequestToBackend(
//    phone: String,
//    notificationToken: String?,
//    url: String
//): HttpResponse? {
//    val client = HttpClient()
//
//    try {
//        val jsonContent = Json.encodeToString(
//            buildJsonObject {
//                put("phone", phone)
//
//                notificationToken?.let { put("notificationToken", it) }
//            }
//        )
//
//        val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
//            contentType(ContentType.Application.Json)
//            setBody(jsonContent)
//        }
//
//
//
//        if (response.status.isSuccess()) {
//
//
//            return response
//
//
//        } else {
//            println("Failed to retrieve data: ${response.status.description}")
//        }
//    } catch (e: Exception) {
//        println("Error111: $e")
//    } finally {
//        client.close()
//    }
//
//    return null
//}





