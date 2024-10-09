package org.videotrade.shopot.data

import androidx.compose.runtime.MutableState
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import org.videotrade.shopot.domain.model.ReloadRes
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.getHttpClientEngine

class origin {
    val client =
        HttpClient(getHttpClientEngine()) // Инициализация HTTP клиента, возможно вам стоит инициализировать его вне этой функции, чтобы использовать пул соединений
    
    
    suspend inline fun <reified T> get(url: String): T? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            
            println("url ${EnvironmentConfig.serverUrl}$url")
            
            val response: HttpResponse = client.get("${EnvironmentConfig.serverUrl}$url") {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            
            
            println("bodyAsTextbodyAsText ${response.bodyAsText()} ${response.status}")
            
            
            if (
                response.status == HttpStatusCode(401, "Unauthorized")
            ) {
                return null
                
            }
            
            
            if (response.status.isSuccess()) {
                
                
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
    
    
    suspend inline fun post(
        url: String,
        data: String
    ): String? {
        
        try {
            val token = getValueInStorage("accessToken")
            println("url ${EnvironmentConfig.serverUrl}$url")

            val response: HttpResponse =
                client.post("${EnvironmentConfig.serverUrl}$url") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    setBody(data)
                }

            println("Отправляем данные: $data")
            println("response.bodyAsText() ${response.bodyAsText()}")
            
            if (response.status.isSuccess()) {

                
                return response.bodyAsText()
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
            }
        } catch (e: Exception) {
            
            println("Error1111: $e")
            
            return null
            
        } finally {
            client.close()
        }
        
        return null
    }

    // Новый метод post с поддержкой заголовков
    suspend inline fun post(
        url: String,
        data: String,
        headers: Map<String, String> = emptyMap()
    ): String? {
        return try {
            val token = getValueInStorage("accessToken")
            println("url ${EnvironmentConfig.serverUrl}$url")

            val response: HttpResponse =
                client.post("${EnvironmentConfig.serverUrl}$url") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    headers.forEach { (key, value) ->
                        header(key, value)
                    }
                    setBody(data)
                }

            println("Отправляем данные: $data")
            println("response.bodyAsText() ${response.bodyAsText()}")

            if (response.status.isSuccess()) {
                return response.bodyAsText()
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
            }
            null
        } catch (e: Exception) {
            println("Error1111: $e")
            null
        } finally {
            client.close()
        }
    }
    
    
    suspend inline fun put(
        url: String,
        data: String
    ): HttpResponse? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            val response: HttpResponse =
                client.put("${EnvironmentConfig.serverUrl}$url") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    setBody(data)
                }
            
            
            println("response.bodyAsText() ${response.bodyAsText()}")
            
            if (response.status.isSuccess()) {
                
                return response
                
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
                
                return null
                
            }
        } catch (e: Exception) {
            println("Error: $e")
            return null
            
        } finally {
            client.close()
        }
    }
    
    
    @OptIn(InternalAPI::class)
    suspend inline fun reloadTokens(): String? {
        val client = HttpClient(getHttpClientEngine())
        
        try {
            
            
            val refreshToken = getValueInStorage("refreshToken")
            
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("refreshToken", refreshToken)
                    
                }
            )
            
            println("response11 ")
            
            val response: HttpResponse =
                client.post("${EnvironmentConfig.serverUrl}auth/refresh-token") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonContent)
                }
            
            println("response11 ${response.bodyAsText()}")
            
            
            
            if (response.status.isSuccess()) {
                val responseData: ReloadRes = Json.decodeFromString(response.bodyAsText())
                
                addValueInStorage(
                    "accessToken",
                    responseData.accessToken
                )
                
                addValueInStorage(
                    "refreshToken",
                    responseData.refreshToken
                )
                
                return responseData.userId
                
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
                return null
                
            }
        } catch (e: Exception) {
            println("Error222: $e")
        } finally {
            client.close()
        }
        
        return null
    }
    
    
    suspend fun sendFile(
        fileDir: String? = null,
        contentType: String,
        filename: String,
        isAuth: Boolean,
    ): String? {

        return if (fileDir != null) {
            FileProviderFactory.create().uploadCipherFile(
                if (isAuth) "file/upload/no-auth" else "file/upload",
                fileDir,
                contentType,
                filename
            ) {
                println("progress ${it}")
            }
        } else {
            null
        }
    }
    
    
    suspend fun sendLargeFile(
        url: String,
        fileDir: String? = null,
        contentType: String,
        filename: String,
        progress: MutableState<Float>
    ): FileDTO? {
        return if (fileDir != null) {
            FileProviderFactory.create().uploadFileToDirectory(
                "file/upload",
                fileDir,
                contentType,
                filename
            ) {
                
                progress.value = it
                println("progress ${it}")
            }
        } else {
            null
        }
    }
    
}