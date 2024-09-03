package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    
    private val profile = MutableStateFlow(ProfileDTO())
    
    override suspend fun downloadProfile(): ProfileDTO? {
        
        val init = origin()
        
        
        val profileRes = init.get<ProfileDTO>("user/profile") ?: return null
        
        println("profileRes $profileRes")
        
        profile.value = profileRes
        
        
        return profileRes
        
    }
    
    
    override fun getProfile(): ProfileDTO {
        
        return profile.value.copy()
        
    }
    
    override fun setProfile(newProfile: ProfileDTO) {
        profile.value = newProfile
    }
    
    override fun getProfileState(): StateFlow<ProfileDTO> = profile.asStateFlow()
    
    
    override fun clearData() {
        profile.value = ProfileDTO()
    }
}