package org.videotrade.shopot.data

import androidx.compose.runtime.MutableState
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.util.InternalAPI
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import org.videotrade.shopot.api.EnvironmentConfig
import org.videotrade.shopot.api.addValueInStorage
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.FileDTO
import org.videotrade.shopot.multiplatform.FileProviderFactory
import org.videotrade.shopot.multiplatform.getHttpClientEngine

class origin {
    val client =
        HttpClient(getHttpClientEngine()) // Инициализация HTTP клиента, возможно вам стоит инициализировать его вне этой функции, чтобы использовать пул соединений
    
    
    suspend inline fun <reified T> get(url: String): T? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            
            println("url $url")
            
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
    
    
    suspend inline fun <reified T> post(
        url: String,
        data: String
    ): T? {
        
        try {
            val token = getValueInStorage("accessToken")
            
            val response: HttpResponse =
                client.post("${EnvironmentConfig.serverUrl}$url") {
                    contentType(ContentType.Application.Json)
                    header(HttpHeaders.Authorization, "Bearer $token")
                    setBody(data)
                }
            
            
            println("response.bodyAsText() ${response.bodyAsText()}")
            
            if (response.status.isSuccess()) {
                
                
                val responseData: T = Json.decodeFromString(response.bodyAsText())
                
                
                return responseData
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
            }
        } catch (e: Exception) {
            
            println("Error: $e")
            
            return null
            
        } finally {
            client.close()
        }
        
        return null
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
    suspend fun reloadTokens(
    
    ): HttpResponse? {
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
            
            println("response11 ${response.content}")
            
            
            
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
    
    
    suspend fun sendFile(
        url: String,
        fileDir: String? = null,
        contentType: String,
        filename: String,
        fileBytes: ByteArray? = null,
        
        ): FileDTO? {
        
        if (fileBytes == null) {
            return if (fileDir != null) {
                FileProviderFactory.create().uploadFileToDirectory(
                    "file/upload",
                    fileDir,
                    contentType,
                    filename
                ) {
                    println("progress ${it}")
                }
            } else {
                null
            }
            
        } else {
            val client =
                HttpClient(getHttpClientEngine())
            try {
                val token = getValueInStorage("accessToken")
                
                println("contentType $contentType")
                val response: HttpResponse = client.post("${EnvironmentConfig.serverUrl}$url") {
                    setBody(MultiPartFormDataContent(
                        formData {
                            append("file", fileBytes, Headers.build {
                                append(HttpHeaders.ContentType, contentType)
                                append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
                            })
                        }
                    ))
                    header(HttpHeaders.Authorization, "Bearer $token")
                }
                
                
                
                println("response.Send ${response.status} ${response.bodyAsText()}")
                
                if (response.status.isSuccess()) {
                    val responseData: FileDTO = Json.decodeFromString(response.bodyAsText())
                    
                    
                    return responseData
                    
                } else {
                    println("Failed to retrieve data: ${response.status.description} ${response.request}")
                    return null
                    
                }
            } catch (e: Exception) {
                
                println("Error111: $e")
                
                return null
                
            } finally {
                client.close()
            }
            
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