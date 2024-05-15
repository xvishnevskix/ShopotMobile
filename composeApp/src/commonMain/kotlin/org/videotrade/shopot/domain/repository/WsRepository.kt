package org.videotrade.shopot.domain.repository

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession


interface WsRepository {


    suspend fun connectionWs(userId: String)
    
    suspend fun getWsSession(): DefaultClientWebSocketSession?
    
    

}