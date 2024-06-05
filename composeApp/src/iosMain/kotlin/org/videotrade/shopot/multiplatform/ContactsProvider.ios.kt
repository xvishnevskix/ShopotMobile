package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.domain.model.ContactDTO
import platform.Contacts.*
import platform.Foundation.NSError
import platform.Foundation.NSString
import platform.Foundation.NSLog

actual class ContactsProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.Default) {
        val contactStore = CNContactStore()
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        )
        
        val fetchRequest = CNContactFetchRequest(keysToFetch = keysToFetch)
        val contacts = mutableListOf<ContactDTO>()
        val fetchError: CPointer<ObjCObjectVar<NSError?>>? = null
        
        contactStore.enumerateContactsWithFetchRequest(fetchRequest, error = fetchError) { contact, stop ->
            val firstName = contact?.givenName ?: ""
            val lastName = contact?.familyName ?: ""
            val phoneNumbers = contact?.phoneNumbers
            
            phoneNumbers?.forEach { labeledValue ->
                val labeledValueObj = labeledValue as? CNLabeledValue
                
                val phoneNumber = (labeledValueObj?.value as? CNPhoneNumber)?.stringValue ?: ""
                contacts.add(ContactDTO(firstName = firstName, lastName = lastName, phone = phoneNumber, icon = null))
            }
        }
        
        if (fetchError != null) {
            NSLog("Failed to fetch contacts: %@", fetchError)
        }
        
        contacts
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider {
        return ContactsProvider()
    }
}



