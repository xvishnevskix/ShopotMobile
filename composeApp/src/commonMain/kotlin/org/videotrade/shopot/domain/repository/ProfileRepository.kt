package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.ProfileDTO


interface ProfileRepository {
    
    
    suspend fun downloadProfile() : ProfileDTO?
    fun getProfile() : ProfileDTO?
    
    
}