package org.videotrade.shopot.presentation.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.jetbrains.compose.resources.Font
import org.koin.compose.koinInject
import org.videotrade.shopot.SharedRes
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Auth.AuthHeader
import org.videotrade.shopot.presentation.components.Auth.Otp
import org.videotrade.shopot.presentation.components.Common.CustomButton
import org.videotrade.shopot.presentation.components.Common.SafeArea
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.signUp.SignUpScreen
import shopot.composeapp.generated.resources.Res
import shopot.composeapp.generated.resources.SFCompactDisplay_Regular
import shopot.composeapp.generated.resources.SFProText_Semibold

class AuthCallScreen(private val phone: String, private val authCase: String) : Screen {
    
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val responseState = remember { mutableStateOf<String?>("1111") }
        val isSuccessOtp = remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()
        val сommonViewModel: CommonViewModel = koinInject()


        
        
        LaunchedEffect(key1 = Unit) {
            
            viewModel.navigator.value = navigator
            
        }


//        LaunchedEffect(key1 = viewModel) {
//
//
//            val response = sendRequestToBackend(phone, null, "2fa")
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
//
        
        
        val isError = remember { mutableStateOf(false) }
        
        
        val otpFields = remember { mutableStateListOf("", "", "", "") }
        
        
        
        
        
        
        SafeArea {
            
            when (authCase) {
                "SignIn" -> AuthHeader(stringResource(SharedRes.strings.login), 0.55F)
                "SignUp" -> AuthHeader(stringResource(SharedRes.strings.create_account), 0.75F)
            }
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                
                
                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                    
                    
                    item {
                        Text(
                            stringResource(SharedRes.strings.enter_last_4_digits_of_the_incoming_call),
                            modifier = Modifier.padding(bottom = 10.dp),
                            fontFamily = FontFamily(Font(Res.font.SFProText_Semibold)),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = TextUnit(0.1F, TextUnitType.Sp),
                            lineHeight = 24.sp,
                            color = Color.Black
                            
                            
                            )
                        Text(
                            stringResource(SharedRes.strings.you_will_receive_a_call_to_your_number_enter_the_last_4),
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
                                
                                
                                coroutineScope.launch {


//                                if (
//                                    responseState.value != otpText && !isSuccessOtp.value
//
//                                ) {
//
//                                    return@launch
//                                }
                                    
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

//                    Text(
//                        "Отправить код по SMS",
//                        fontFamily = FontFamily(Font(Res.font.Montserrat_Medium)),
//                        textAlign = TextAlign.Center,
//                        fontSize = 15.sp,
//                        lineHeight = 15.sp,
//                        color = Color(0xFF000000),
//                        textDecoration = TextDecoration.Underline,
//                        modifier = Modifier.padding(top = 20.dp)
//                            .clickable { navigator.push(AuthSMSScreen(phone)) }
//                    )
                    }
                }
                
                
            }
            
        }
        
    }
    
    
}


suspend fun sendRequestToBackend(
    phone: String,
    notificationToken: String?,
    url: String
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
    сommonViewModel: CommonViewModel
) {
    
    
    val response =
        sendRequestToBackend(phone, NotifierManager.getPushNotifier().getToken(), "auth/login")
    
    
    println("sadada")
    
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




