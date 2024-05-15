package org.videotrade.shopot.domain.usecase

import org.videotrade.shopot.domain.model.MessageItem
import org.videotrade.shopot.domain.repository.ChatRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserProfile
import org.videotrade.shopot.domain.repository.ProfileRepository

class ProfileUseCase : KoinComponent {
    private val repository: ProfileRepository by inject()

    suspend fun downloadProfile(): UserProfile? {
        return repository.downloadProfile()
    }
    
    
    
     fun getProfile(): ProfileDTO? {
        return repository.getProfile()
    }
}