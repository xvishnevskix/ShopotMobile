package org.videotrade.shopot.domain.repository

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.StateFlow
import org.videotrade.shopot.domain.model.ContactDTO

interface ContactsRepository {
    
    val contacts: StateFlow<List<ContactDTO>>
    
    suspend fun fetchContacts(): List<ContactDTO>?
    
    suspend fun createChat(profileId: String , contact: ContactDTO, navigator: Navigator)
    
    fun clearData()
}