package org.videotrade.shopot.domain.usecase

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.repository.ContactsRepository

class ContactsUseCase : KoinComponent {
    private val repository: ContactsRepository by inject()
    
    
    val contacts: StateFlow<List<ContactDTO>> get() = repository.contacts
    
    
    suspend fun fetchContacts(): List<ContactDTO>? {
        return repository.fetchContacts()
    }
    
    suspend fun createChat(profileId: String, contact: ContactDTO, navigator: Navigator) {
        return repository.createChat(profileId, contact, navigator)
    }
    
    fun clearData() {
        repository.clearData()
    }
}