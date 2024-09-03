package org.videotrade.shopot.presentation.screens.profile

import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.multiplatform.PlatformFilePick
import org.videotrade.shopot.presentation.screens.main.MainViewModel

class ProfileViewModel : ViewModel(), KoinComponent {
    private val profileUseCase: ProfileUseCase by inject()
    private val mainViewModel: MainViewModel by inject()
    
    val profile = MutableStateFlow(profileUseCase.getProfile())


//    fun getProfile() {
//        viewModelScope.launch {
//            profile.value =
//        }
//    }
init {
    viewModelScope.launch {
        profileUseCase.getProfileState().collect {
            println("PRofileeeeeee $it")
            profile.value = it
        }
    }
}
    
    
    
    
    
    
    suspend fun sendNewProfile(
        newProfile: ProfileDTO,
        image: PlatformFilePick?,
        navigator: Navigator,
    ): Boolean {
        
        
        val icon = image?.let {
            withContext(Dispatchers.IO) {
                origin().sendFile(
                    image.fileAbsolutePath,
                    "image", image.fileName,
                    false
                )
            }
        }
        println("icon $icon")
        
        
        val jsonContent = Json.encodeToString(
            buildJsonObject {
                put("firstName", newProfile.firstName)
                put("lastName", newProfile.lastName)
                put("icon", icon)
                put("description", newProfile.description)
            }
        )
        
        
        println("jsonContent321323 $jsonContent")
        
        
        val profileUpdate = origin().put("user/profile/edit", jsonContent)
        
        
        
        
        return if (profileUpdate !== null) {
            
            val responseData: ProfileDTO = Json.decodeFromString(profileUpdate.bodyAsText())
            
            println("responseData $responseData")
            
            profileUseCase.setProfile(responseData)
            
            navigator.push(ProfileScreen(anotherUser = false))
            
            true
        } else {
            false
        }
        
        
    }
    
    
}