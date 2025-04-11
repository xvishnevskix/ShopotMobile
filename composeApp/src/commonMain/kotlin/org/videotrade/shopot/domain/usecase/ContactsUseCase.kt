package org.videotrade.shopot.domain.usecase

import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.repository.ContactsRepository

class ContactsUseCase : KoinComponent {
    private val repository: ContactsRepository by inject()
    
    
    val contacts: StateFlow<List<ContactDTO>> get() = repository.contacts
    
    val unregisteredContacts: StateFlow<List<ContactDTO>> get() = repository.unregisteredContacts
    
    
    suspend fun fetchContacts(): List<ContactDTO>? {
        return repository.fetchContacts()
    }
    
    fun getContacts(): List<ContactDTO> {
        return repository.getContacts()
    }
    
    suspend fun createChat(profileId: String, contact: ContactDTO) {
        return repository.createChat(profileId, contact)
    }
    
    suspend fun createGroupChat(users: List<String?>, groupName: String,
//    ownerId: String,
    ) {
        return repository.createGroupChat(users, groupName,
//            ownerId

        )
    }
    
    fun clearData() {
        repository.clearData()
    }
}