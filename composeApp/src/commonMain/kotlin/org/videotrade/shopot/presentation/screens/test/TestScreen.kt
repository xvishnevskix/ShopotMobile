package org.videotrade.shopot.presentation.screens.test

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.compose.koinInject
import org.videotrade.shopot.multiplatform.PermissionsProviderFactory
import org.videotrade.shopot.multiplatform.SwiftFuncsClass
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Common.SafeArea

class TestScreen : Screen {
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        
        
        LaunchedEffect(Unit){
            val cameraPer =
                PermissionsProviderFactory.create()
                    .getPermission("microphone")
        }
        
        MaterialTheme {
            SafeArea {
                Column {
                    Button(onClick = {

                        scope.launch {
                            val client = sendCall()
                        }
                        
                    }) {
                        Text(
                            "Start Recording", color = Color.Black
                        )
                    }
                    
                }
            }
        }
    }
}


suspend fun sendCall() {
    try {
        
        val client = HttpClient(getHttpClientEngine())
        
        val jsonContent = Json.encodeToString(buildJsonObject {
            put(
                "deviceToken",
                "808f2ae7ade15325d8b35347c18f461393ce6725e3f677f9fa3070aa4a1f758f51a2ff6e3c721e8e32ce4cb5559466a94934b82d2da475efe4bdb0a8c04d7cdd570bbfc430711e620dc5ac75677b09d9"
            )
            put("callerName", "Test Call")
            put("callerId", "1234")
        })
        
        
        val response: HttpResponse = client.post("http://192.168.1.118:3000/send-voip-notification") {
            contentType(ContentType.Application.Json)
            setBody(jsonContent)
        }
        
        println("response.bodyAsText() ${response.bodyAsText()}")
        
        if (response.status.isSuccess()) {
            
            println("response.bodyAsText() ${response.bodyAsText()}")
            
        } else {
            println("Failed to retrieve data: ${response.status.description} ${response.request}")
        }
    } catch (e: Exception) {
        
        println("Error1111: $e")
        
    }
}