package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.InternalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class VerificationResponse(
    val clientNumber: String,
    val confirmationNumber: String,
    val qrCodeUri: String,
    val callId: String
)

@Serializable
data class CheckResponse(
    val flag: Boolean,
    val timeout: Int
)

class TestScreen : Screen {
    @OptIn(InternalAPI::class)
    @Composable
    override fun Content() {
        val backendUrl = "" // Укажите URL вашего бэкенда
        val client = HttpClient()
        val coroutineScope = rememberCoroutineScope()
        
        var phoneNumber by remember { mutableStateOf("") }
        var clientNumber by remember { mutableStateOf("") }
        var confirmationNumber by remember { mutableStateOf("") }
        var qrCodeUri by remember { mutableStateOf("") }
        var timer by remember { mutableStateOf(0) }
        var view by remember { mutableStateOf("start") }
        
        fun checkConfirmation(callId: String) {
            coroutineScope.launch {
                while (true) {
                    delay(1000)
                    try {
                        val response: HttpResponse = client.post("$backendUrl?action=check") {
                            body = listOf("callId" to callId)
                        }
                        val data = Json.decodeFromString<CheckResponse>(response.body())
                        if (data.timeout > 0) {
                            timer = data.timeout
                            if (data.flag) {
                                view = "confirm"
                                break
                            }
                        } else {
                            view = "expire"
                            break
                        }
                    } catch (e: Exception) {
                        view = "error"
                        break
                    }
                }
            }
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            when (view) {
                "start" -> {
                    Text("Введите номер телефона", fontSize = 20.sp)
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Номер телефона") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            try {
                                val response: HttpResponse = client.post("$backendUrl?action=start") {
                                    body = listOf("phoneNumber" to phoneNumber)
                                }
                                val data = Json.decodeFromString<VerificationResponse>(response.body())
                                clientNumber = data.clientNumber
                                confirmationNumber = data.confirmationNumber
                                qrCodeUri = data.qrCodeUri
                                view = "wait"
                                checkConfirmation(data.callId)
                            } catch (e: Exception) {
                                view = "error"
                            }
                        }
                    }) {
                        Text("Далее")
                    }
                }
                "wait" -> {
                    Text("Позвоните с номера $clientNumber на $confirmationNumber")
      
                    Text("Ожидаем ваш звонок: $timer сек.")
                }
                "confirm" -> {
                    Text("Номер $clientNumber успешно верифицирован.", color = Color.Green)
                }
                "expire" -> {
                    Text("Время ожидания истекло. Номер $clientNumber не верифицирован.", color = Color.Red)
                }
                "error" -> {
                    Text("Ошибка при обработке запроса.", color = Color.Red)
                }
            }
        }
    }
    

}


