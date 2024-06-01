package org.videotrade.shopot.multiplatform

import org.videotrade.shopot.domain.model.ContactDTO

actual class ContactsProvider {
    actual suspend fun getContacts(): List<ContactDTO> {
        TODO("Not yet implemented")
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider {
        TODO("Not yet implemented")
    }
}