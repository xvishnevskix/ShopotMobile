

package org.videotrade.shopot.presentation.screens.common

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
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
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.components.Common.SafeArea

class TestScreen : Screen {
    @Composable
    override fun Content() {
        MediaPickerSample()
    }
}

@Composable
fun MediaPickerSample() {
    val scope = rememberCoroutineScope()
    
    
    SafeArea {
        Row {
            
            Button(
                onClick = {
                    
                    scope.launch {
                        reloadTokens()
                    }
                }
            ) {
                Text("Pick Single")
            }
        }
        
    }
    
}


suspend fun reloadTokens(

): HttpResponse? {
    val client = HttpClient(getHttpClientEngine())
    
    try {
        
        
        val refreshToken = getValueInStorage("refreshToken")
        
        println("refreshToken $refreshToken")
        
        val jsonContent = Json.encodeToString(
            buildJsonObject {
                put("refreshToken", refreshToken)
                
            }
        )
        
        val response: HttpResponse =
            client.post("${EnvironmentConfig.serverUrl}auth/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(jsonContent)
            }
        
        
        
        
        if (response.status.isSuccess()) {
            
            val jsonString = response.bodyAsText()
            val jsonElement = Json.parseToJsonElement(jsonString)
            val messageObject = jsonElement.jsonObject["message"]?.jsonObject
            
            
            val token = messageObject?.get("accessToken")?.jsonPrimitive?.content
            
            token?.let {
                addValueInStorage(
                    "accessToken",
                    token
                )
            }
            
            
            return response
            
            
        } else {
            println("Failed to retrieve data: ${response.status.description} ${response.request}")
        }
    } catch (e: Exception) {
        println("Error222: $e")
    } finally {
        client.close()
    }
    
    return null
}





