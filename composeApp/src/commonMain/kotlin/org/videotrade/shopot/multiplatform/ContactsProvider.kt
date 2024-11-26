package org.videotrade.shopot.multiplatform

import org.videotrade.shopot.domain.model.ContactDTO

expect class ContactsProvider {
    suspend fun getContacts(): List<ContactDTO>
    
    fun sendMessageInvite()
}

expect object ContactsProviderFactory {
    fun create(): ContactsProvider
}
