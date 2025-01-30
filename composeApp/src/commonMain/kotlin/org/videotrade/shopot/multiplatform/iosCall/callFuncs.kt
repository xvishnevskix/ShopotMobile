package org.videotrade.shopot.multiplatform.iosCall

import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.commonViewModules.CommonViewModel
import org.videotrade.shopot.presentation.screens.call.CallViewModel

object CallHandler : KoinComponent {
    // Метод для запуска логики WebRTC
    fun startWebRTCSession(callId: String) {
        println("Starting WebRTC session for call ID: $callId")
        // Ваша логика для запуска WebRTC
    }
    
    suspend fun getCallInfo(callId: String): GetCallInfoDto? {
        try {
            val client = HttpClient(getHttpClientEngine())
            val profileId = getValueInStorage("profileId")
            
            val response: HttpResponse =
                client.get("${SERVER_URL}/calls/callMessage/$callId")
            
            println("response.bodyAsText() ${response.bodyAsText()}")
            
            if (response.status.isSuccess()) {
                val responseData: GetCallInfoDto = Json.decodeFromString(response.bodyAsText())
                
                return responseData
            } else {
                println("Failed to retrieve data: ${response.status.description} ${response.request}")
                return null
            }
        } catch (e: Exception) {
            println("Error1111: $e")
            return null
            
        }
    }
    
    
    suspend fun newCallIos(callInfo: GetCallInfoDto) {
        try {
            val contactsUseCase: ContactsUseCase by inject()
            val callViewModel: CallViewModel by inject()
            val commonViewModel: CommonViewModel by inject()
            val callUseCase: CallUseCase by inject()


//            val cameraPer = PermissionsProviderFactory.create()
//                .getPermission("microphone")

//            if (cameraPer) {
            val profileId = getValueInStorage("profileId") ?: return
            
            callViewModel.setOtherUserId(callInfo.userId)
            
            callViewModel.connectionCallWs(profileId)
            
//            callUseCase.setOffer(
//                SessionDescription(
//                    sdp = callInfo.rtcMessage.sdp,
//                    type = SessionDescriptionType.Offer,
//                )
//            )
        
        
        } catch (e: Exception) {
        
        }
        
        
    }
    
}


@Serializable
data class GetCallInfoDto(
    val type: String,
    val userId: String,
    val calleeId: String,
    val rtcMessage: SessionDescriptionDTO,
)