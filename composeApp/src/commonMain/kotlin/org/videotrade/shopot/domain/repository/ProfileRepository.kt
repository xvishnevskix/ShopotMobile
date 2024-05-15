package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.UserProfile


interface ProfileRepository {
    
    
    suspend fun getProfile() : UserProfile?
    
    
}