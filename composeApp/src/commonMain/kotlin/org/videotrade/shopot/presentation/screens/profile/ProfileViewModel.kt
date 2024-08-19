package org.videotrade.shopot.presentation.screens.profile

import androidx.compose.runtime.MutableState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ProfileUseCase

class ProfileViewModel : ViewModel(), KoinComponent {
    private val profileUseCase: ProfileUseCase by inject()
    
    
    val profile = profileUseCase.getProfile()


//    fun getProfile() {
//        viewModelScope.launch {
//            profile.value =
//        }
//    }
    
    suspend fun sendNewProfile(
        newProfile: ProfileDTO,
        imageArray: MutableState<ByteArray?>
    ): Boolean {
        
        val fileId = imageArray.value?.let {
            origin().sendFile(
                null,
                "image", "profileImage", false,
            )
        }
        
        
        val jsonContent = Json.encodeToString(
            buildJsonObject {
                put("firstName", newProfile.firstName)
                put("lastName", newProfile.lastName)
                put("icon", fileId)
                put("description", newProfile.description)
            }
        )
        
        
        println("jsonContent321323 $jsonContent")
        
        
        val profileUpdate = origin().put("user/profile/edit", jsonContent)
        
        
        
        
        return if (profileUpdate !== null) {
            
            val responseData: ProfileDTO = Json.decodeFromString(profileUpdate.bodyAsText())
            profileUseCase.setProfile(responseData)
            
            true
        } else {
            false
        }
        
    }
    
    
}