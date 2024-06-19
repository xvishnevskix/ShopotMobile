package org.videotrade.shopot.presentation.screens.profile

import androidx.compose.runtime.MutableState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
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
    
    fun sendNewProfile(newProfile: ProfileDTO, imageArray: MutableState<ByteArray?>) {
        viewModelScope.launch {

//            val icon = imageArray.value?.let {
//                origin().sendFile(
//                    "file/upload",
//                    it, "image/jpeg"
//                )
//
//            }
            
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("firstName", newProfile.firstName)
                    put("lastName", newProfile.lastName)
//                    put("icon", icon?.id)
                    put("status", newProfile.status)
                }
            )
            
            
            println("jsonContent321323 $jsonContent")


//            origin().put("user/profile/edit", jsonContent)
        
        
        }
    }
    
    
}