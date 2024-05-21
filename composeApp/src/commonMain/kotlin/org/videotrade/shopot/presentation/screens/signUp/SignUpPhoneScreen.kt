package org.videotrade.shopot.presentation.screens.signUp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.jetbrains.compose.resources.Font
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.presentation.components.Auth.PhoneInput
import org.videotrade.shopot.presentation.screens.auth.AuthCallScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFProText_Semibold

class SignUpPhoneScreen : Screen {


    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>(null) }
        val isSuccessOtp = remember { mutableStateOf<Boolean>(false) }
        val coroutineScope = rememberCoroutineScope()

        val phone = remember { mutableStateOf(value = TextFieldValue()) }


        SafeArea {

            AuthHeader("Создать аккаунт", 0.75F)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Введите номер телефона",
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
                        lineHeight = 24.sp,
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                    PhoneInput(phone)

                    Box(
                        modifier = Modifier.padding(top = 20.dp)
                    ) {
                        CustomButton(
                            "Отправить код",
                            {

                                it.launch {
                                    try {
                                        val client = HttpClient()

                                        val jsonContent = Json.encodeToString(
                                            buildJsonObject {
                                                put("phoneNumber", phone.value.text.drop(1))
                                            }
                                        )
                                        val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}auth/sign-up") {
                                            contentType(ContentType.Application.Json)
                                            setBody(jsonContent)
                                        }
                                        println("responseresponse ${response}")

                                        if (response.status.isSuccess()) {
                                            val jsonString = response.bodyAsText()
                                            val jsonElement = Json.parseToJsonElement(jsonString).jsonObject

                                            println("accessToken ${jsonElement}")

                                            val accessToken = jsonElement["accessToken"]?.jsonPrimitive?.content
                                            val refreshToken = jsonElement["refreshToken"]?.jsonPrimitive?.content

                                            accessToken?.let {
                                                addValueInStorage("accessToken", accessToken)
                                            }
                                            refreshToken?.let {
                                                addValueInStorage("refreshToken", refreshToken)
                                            }

                                            navigator.push(
                                                AuthCallScreen(
                                                    phone.value.text,
                                                    "SignUp"
                                                )
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace() // It is a good practice to print the stack trace of the exception for debugging purposes
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



