package org.videotrade.shopot.multiplatform.iosCall

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.api.EnvironmentConfig.SERVER_URL
import org.videotrade.shopot.api.getValueInStorage
import org.videotrade.shopot.api.navigateToScreen
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.SessionDescriptionDTO
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.multiplatform.getHttpClientEngine
import org.videotrade.shopot.presentation.screens.call.CallScreen
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel

object CallHandler : KoinComponent {
    val commonViewModel: CommonViewModel = getKoin().get()
    val callViewModel: CallViewModel = getKoin().get()
    
    
    fun startWebRTCSession() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                println("✅ CommonViewModel найден: ${callViewModel}")
                println("commonViewModel.mainNavigator.value ${commonViewModel.mainNavigator.value}")
                
                callViewModel.initWebrtc()
                
                commonViewModel.mainNavigator.value?.push(
                    CallScreen(
                        calleeId = callViewModel.iosCallData.value?.userId ?: "",
                        userFirstName = "",
                        userLastName = "",
                        userPhone = ""
                    )
                )
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
                    client.get("${SERVER_URL}calls/callMessage/$callId")
                
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
            val callUseCase: CallUseCase = getKoin().get()
            callViewModel.rejectCall(
                callUseCase.getOtherUserId(),
                "00:00:00"
            )
            
        } catch (e: Exception) {
            
        }
        
        
    }
    
    
    fun setIsCallBackground(isCallBackground: Boolean) {
        val callUseCase: CallUseCase by inject()
        
        callUseCase.setIsCallBackground(isCallBackground)
        
    }
    
    fun setIsIncomingCall(isIncomingCall: Boolean) {
        val callUseCase: CallUseCase by inject()
        
        callUseCase.setIsIncomingCall(isIncomingCall)
    }
    
    fun setAppIsActive(appIsActive: Boolean) {
        val commonViewModel: CommonViewModel by inject()
        
        callViewModel.initWebrtc()
        
        commonViewModel.setAppIsActive(appIsActive)
        
    }
    
}


@Composable
fun isActiveCallIos(callViewModel: CallViewModel, navigator: Navigator) {
    val profileId = getValueInStorage("profileId")
    
    val user = ProfileDTO()
    
    LaunchedEffect(Unit) {
        if (profileId != null) {
            callViewModel.callScreenInfo.value =
                CallScreen(user.id, null, user.firstName, user.lastName, user.phone)


//            callViewModel.initWebrtc()
        }
    }
    
    
    navigateToScreen(navigator,
        CallScreen(user.id, null, user.firstName, user.lastName, user.phone)
    )
    
}


@Serializable
data class GetCallInfoDto(
    val type: String,
    val userId: String,
    val calleeId: String,
    val rtcMessage: SessionDescriptionDTO,
)