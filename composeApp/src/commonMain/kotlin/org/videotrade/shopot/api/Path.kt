package org.videotrade.shopot.api

import kotlinx.serialization.Serializable

// В androidMain
object EnvironmentConfig {
    const val serverUrl: String = "http://192.168.31.223:3000/api/"
const val webSocketsUrl: String = "192.168.31.223"
    
//    const val serverUrl: String = "https://videotradedev.ru/api/"
//
//    const val webSocketsUrl: String = "videotradedev.ru"
    
    @Serializable
    data class Customer(
        
        val content: String = "Привет!!",
        val fromUser: String = "10f609c6-df91-4cbc-afc7-30c175cc1111",
        val forwardMessage: Int = 0,
        val answerMessage: Int = 0,
        val replaces: Int = 0,
        val created_at: String = "",
        val isDeleted: Boolean = false,
        val chatId: String = "89da6ae1-cec8-4a48-89de-46c5b7c174a2"
        
    )
}

