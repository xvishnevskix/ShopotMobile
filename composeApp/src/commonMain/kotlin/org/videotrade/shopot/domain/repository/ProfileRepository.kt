package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ProfileDTO


interface ProfileRepository {
    
    
    suspend fun downloadProfile() : ProfileDTO?
    fun getProfile(): ProfileDTO
    fun setProfile(newProfile: ProfileDTO)
    
    fun getProfileState(): StateFlow<ProfileDTO>
    
    fun clearData()
}