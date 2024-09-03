package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileUseCase : KoinComponent {
    private val repository: ProfileRepository by inject()

    suspend fun downloadProfile(): ProfileDTO? {
        return repository.downloadProfile()
    }
    
    
    fun getProfile(): ProfileDTO {
        return repository.getProfile()
    }
    
    
    
    fun getProfileState(): StateFlow<ProfileDTO> {
        return repository.getProfileState()
    }
    
    fun setProfile(newProfile: ProfileDTO) {
        repository.setProfile(newProfile)
    }
    
    fun clearData() {
        repository.clearData()
    }
}