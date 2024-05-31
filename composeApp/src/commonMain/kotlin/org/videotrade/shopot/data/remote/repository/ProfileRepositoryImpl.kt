package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    
    private val profile = mutableStateOf<ProfileDTO?>(null)
    
    
    override suspend fun downloadProfile(): ProfileDTO? {
        
        val init = origin()
        
        
        val profileRes = init.get<ProfileDTO>("user/profile") ?: return null
        
        println("profileRes $profileRes")
        
        profile.value = profileRes
        
        
        return profileRes
        
    }
    
    
    override fun getProfile(): ProfileDTO? {
        
        return profile.value
        
    }
    
    
}