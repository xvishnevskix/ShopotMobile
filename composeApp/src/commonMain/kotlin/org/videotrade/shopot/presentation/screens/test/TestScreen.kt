package org.videotrade.shopot.presentation.screens.test


import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

import cafe.adriel.voyager.core.screen.Screen
import com.mmk.kmpnotifier.notification.NotifierManager
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.cio.Request
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.compose.koinInject
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.EnvironmentConfig.serverUrl
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.multiplatform.getHttpClientEngine

import org.videotrade.shopot.presentation.components.Common.ZoomableImage
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val coroutineScope = rememberCoroutineScope()
        val viewModel: IntroViewModel = koinInject()


        Button({

            coroutineScope.launch {
                var token = NotifierManager.getPushNotifier().getToken()
                println("onNewToken: $token ") // При необходимости обновить пользовательский токен на сервере


                val jsonContent = Json.encodeToString(
                    buildJsonObject {
                        put("title", "Test Notification")
                        put("body", "This is a test message from Postman.")
                        put("token", token)

                    }
                )

                val op = origin().post<Any>("notification/notify", jsonContent)


                println("op3131 $op")

            }


        }, content = {

            Text("ASDDSAD")
        })

//        ZoomableImage()
    }
}




