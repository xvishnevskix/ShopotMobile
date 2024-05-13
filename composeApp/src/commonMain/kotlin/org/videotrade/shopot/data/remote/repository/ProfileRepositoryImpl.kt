package org.videotrade.shopot.data.remote.repository

import androidx.compose.runtime.mutableStateOf
import co.touchlab.kermit.Logger
import kotlinx.serialization.Serializable
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileRepositoryImpl : ProfileRepository {
    
    private val profile = mutableStateOf<ProfileDTO?>(null)
    
    
    override suspend fun getProfile() {
        
        val init = origin()
        
        
        @Serializable
        data class UserProfile(
            val id: String,
            val name: String
        )
        
        
        
        Logger.d("profile414141")
        
        val profile = init.get<UserProfile>("user/profile")
        
        
        Logger.d("profile3131 $profile")
        
    }
    
    
}