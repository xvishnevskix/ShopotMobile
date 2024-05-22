package org.videotrade.shopot.data

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage

class origin {
    val client =
        HttpClient() // Инициализация HTTP клиента, возможно вам стоит инициализировать его вне этой функции, чтобы использовать пул соединений
    
    suspend inline fun <reified T> get(url: String): T? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            
            val response: HttpResponse = client.get("${EnvironmentConfig.serverUrl}$url") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            
            
            
            if (response.status.isSuccess()) {
                
                println("response3131 ${response.bodyAsText()}")
                
                
                val responseData: T = Json.decodeFromString(response.bodyAsText())
                
                
                return responseData
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
            }
        } catch (e: Exception) {
            println("Error22231: $e")
        } finally {
            client.close()
        }
        return null
    }
    
    
    suspend  inline fun <reified T> post(
        url: String,
        data: String
    ): T? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            val response: HttpResponse =
                client.get("${EnvironmentConfig.serverUrl}$url") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    setBody(data)
                }
            
            
            if (response.status.isSuccess()) {
                
                
                
                val responseData: T = Json.decodeFromString(response.bodyAsText())
                
                
                return responseData
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
            }
        } catch (e: Exception) {
            println("Error: $e")
        } finally {
            client.close()
        }
        
        return null
    }
    
    
    suspend fun reloadTokens(
    
    ): HttpResponse? {
        val client = HttpClient()
        
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
}