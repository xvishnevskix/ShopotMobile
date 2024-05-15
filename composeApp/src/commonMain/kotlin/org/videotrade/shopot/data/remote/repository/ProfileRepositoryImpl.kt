package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserProfile
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    
    private val profile = mutableStateOf<ProfileDTO?>(ProfileDTO("1"))
    
    
    override suspend fun downloadProfile(): UserProfile? {
        
        val init = origin()
        
        
        val profileRes = init.get<UserProfile>("user/profile") ?: return null
        
        
        
        profile.value = profileRes.message
        
        
        return profileRes
        
    }
    
    
    override fun getProfile(): ProfileDTO? {
        
        return profile.value
        
    }
    
    
}