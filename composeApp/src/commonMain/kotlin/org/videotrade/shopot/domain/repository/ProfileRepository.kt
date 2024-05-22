package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserProfile


interface ProfileRepository {
    
    
    suspend fun downloadProfile() : ProfileDTO?
    fun getProfile() : ProfileDTO?
    
    
}