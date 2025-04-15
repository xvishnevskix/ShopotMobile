package org.videotrade.shopot.domain.repository

import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ContactDTO

interface ContactsRepository {
    
    val contacts: StateFlow<List<ContactDTO>>
    
    val unregisteredContacts: StateFlow<List<ContactDTO>>
    
    suspend fun fetchContacts(): List<ContactDTO>?
     fun getContacts(): List<ContactDTO>
    
    
    suspend fun createChat(profileId: String, contact: ContactDTO)
    suspend fun createGroupChat(
        users: List<String?>,
        groupName: String,
        ownerId: String
    )
    
    fun clearData()
}