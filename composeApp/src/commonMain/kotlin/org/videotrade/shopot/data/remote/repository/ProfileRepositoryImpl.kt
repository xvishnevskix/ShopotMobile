package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserProfile
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    
    private val profile = mutableStateOf<ProfileDTO?>(null)
    
    
    override suspend fun getProfile(): UserProfile? {
        
        val init = origin()
        
        
        val profileRes = init.get<UserProfile>("user/profile") ?: return null
        
        
        
        profile.value = profileRes.message
        
        
        return profileRes
        
    }
    
    
}