package org.videotrade.shopot.presentation.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.icerock.moko.resources.compose.stringResource
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.SharedRes
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold

class AuthSMSScreen(private val phone: String) : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()


//        coroutineScope.launch {

//            val response = sendRequestToBackend(phone.drop(1), null, "auth/2fa")
//
//            if (response != null) {
//
//                val jsonString = response.bodyAsText()
//                val jsonElement = Json.parseToJsonElement(jsonString)
//                val messageObject = jsonElement.jsonObject["message"]?.jsonObject
//
//
//
//
//                responseState.value = messageObject?.get("code")?.jsonPrimitive?.content
//
//            }
//
//        }


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
                        stringResource(SharedRes.strings.confirm),
                        {
                            val otpText = otpFields.joinToString("")


//                            coroutineScope.launch {
//
//
//                                if (
//                                    responseState.value != otpText && !isSuccessOtp.value
//
//                                ) {
//
//                                    return@launch
//                                }
//
//                                val response = sendRequestToBackend(phone, null, "auth/login")
//
//
//                                if (response != null) {
//
//                                    val jsonString = response.bodyAsText()
//                                    val jsonElement = Json.parseToJsonElement(jsonString)
//                                    val messageObject =
//                                        jsonElement.jsonObject["message"]?.jsonObject
//
//
//                                    val token = messageObject?.get("accessToken")?.jsonPrimitive?.content
//                                    val refreshToken =
//                                        messageObject?.get("refreshToken")?.jsonPrimitive?.content
//
//
//
//
//                                    token?.let {
//                                        addValueInStorage(
//                                            "accessToken",
//                                            token
//                                        )
//                                    }
//                                    refreshToken?.let {
//                                        addValueInStorage(
//                                            "refreshToken",
//                                            refreshToken
//                                        )
//                                    }
//
//
//                                    navigator.push(MainScreen())
//
//
//                                }
//
//
//                            }


                        })
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





