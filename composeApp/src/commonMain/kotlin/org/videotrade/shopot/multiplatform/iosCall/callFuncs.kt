package org.videotrade.shopot.multiplatform.iosCall

import com.shepeliev.webrtckmp.SessionDescription
import com.shepeliev.webrtckmp.SessionDescriptionType
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.isSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.screens.call.CallIosScreen
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

object CallHandler : KoinComponent {
    val commonViewModel: CommonViewModel = getKoin().get()
    val callViewModel: CallViewModel = getKoin().get()
    
    
    fun startWebRTCSession(callId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("✅ CommonViewModel найден: ${callViewModel}")
                println("commonViewModel.mainNavigator.value ${commonViewModel.mainNavigator.value}")
                
                callViewModel.initWebrtc()
                
//                commonViewModel.mainNavigator.value?.push(
//                    CallIosScreen(
//                        calleeId = callViewModel.iosCallData.value?.userId ?: "",
//                        userFirstName = "",
//                        userLastName = "",
//                        userPhone = ""
//                    )
//                )
            } catch (e: Exception) {
                println("❌ Ошибка получения CommonViewModel: $e")
            }
        }
    }
    
    
    suspend fun getCallInfo(callId: String): GetCallInfoDto? {
        return withContext(Dispatchers.IO) {
            try {
                val client = HttpClient(getHttpClientEngine())
                val profileId = getValueInStorage("profileId")
                
                val response: HttpResponse =
                    client.get("${SERVER_URL}/calls/callMessage/$callId")
                
                println("response.bodyAsText() ${response.bodyAsText()}")
                
                if (response.status.isSuccess()) {
                    val responseData: GetCallInfoDto = Json.decodeFromString(response.bodyAsText())
                    callViewModel.setOtherUserId(responseData.userId)
                    callViewModel.setIosCallData(responseData)
                    newCallIos(responseData)
                    responseData
                } else {
                    println("Failed to retrieve data: ${response.status.description} ${response.request}")
                    null
                }
            } catch (e: Exception) {
                println("Error1111: $e")
                null
            }
        }
    }
    
    
    suspend fun newCallIos(callInfo: GetCallInfoDto) {
        try {
            val commonViewModel: CommonViewModel = KoinPlatform.getKoin().get()
            val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
            val contactsUseCase: ContactsUseCase by inject()
            val callUseCase: CallUseCase by inject()


//            val cameraPer = PermissionsProviderFactory.create()
//                .getPermission("microphone")

//            if (cameraPer) {
            
//            callViewModel.setIsIncomingCall(true)
//            callViewModel.setIsCallBackground(true)
            
            val profileId = getValueInStorage("profileId") ?: return
            
            callViewModel.setOtherUserId(callInfo.userId)
            
            callViewModel.connectionCallWs(profileId)
            
            callUseCase.setOffer(
                SessionDescription(
                    sdp = callInfo.rtcMessage.sdp,
                    type = SessionDescriptionType.Offer,
                )
            )
            
            
        } catch (e: Exception) {
        
        }
        
        
    }
    
     fun rejectCallIos() {
        try {
            callViewModel.iosCallData.value?.userId?.let { callViewModel.rejectCall(it, "00:00:00") }
        } catch (e: Exception) {
        
        }
        
        
    }
    
    suspend fun waitForCommonViewModel(): CommonViewModel {
        var attempt = 0
        while (attempt < 5) {
            try {
                val viewModel: CommonViewModel = getKoin().get()
                if (viewModel.mainNavigator.value != null) return viewModel
            } catch (e: Exception) {
                println("🔄 Ожидание CommonViewModel... Попытка: $attempt")
            }
            delay(500) // Подождем 500 мс и попробуем снова
            attempt++
        }
        throw IllegalStateException("❌ CommonViewModel не стал доступен после 5 попыток!")
    }
    
}




@Serializable
data class GetCallInfoDto(
    val type: String,
    val userId: String,
    val calleeId: String,
    val rtcMessage: SessionDescriptionDTO,
)