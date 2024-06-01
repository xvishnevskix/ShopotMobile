package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.domain.model.ContactDTO
import platform.Contacts.*
import platform.Foundation.*

actual class ContactsProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.Default) {
        val contactStore = CNContactStore()
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        ) as List<*>
        
        val fetchRequest = CNContactFetchRequest(keysToFetch = keysToFetch)
        val contacts = mutableListOf<ContactDTO>()
        
        contactStore.enumerateContactsWithFetchRequest(fetchRequest, error = null) { contact, _ ->
            if(contact !== null){
            val firstName = contact.givenName
            val lastName = contact.familyName
            val phoneNumbers = contact.phoneNumbers
            phoneNumbers.forEach { labeledValue ->
                val phoneNumber = (labeledValue.value() as? CNPhoneNumber)?.stringValue ?: ""
                contacts.add(ContactDTO(firstName = firstName, lastName = lastName, phone = phoneNumber))
            }
                }
        }
        
        contacts
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider {
        return ContactsProvider()
    }
}