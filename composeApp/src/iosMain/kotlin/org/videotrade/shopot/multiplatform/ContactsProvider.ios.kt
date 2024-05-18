package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Contacts.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class ContactsProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getContacts(): List<Contact> = withContext(Dispatchers.Default) {
        
        val store = CNContactStore()
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        )
        
        val request = CNContactFetchRequest(keysToFetch = keysToFetch)
        
        val contacts = mutableListOf<Contact>()
        suspendCoroutine<Unit> { continuation ->
            try {
                store.enumerateContactsWithFetchRequest(request, error = null) { contact, _ ->
                    val name = "${contact?.givenName} ${contact?.familyName}"

                    val phoneNumbers = contact?.phoneNumbers?.mapNotNull { labeledValue ->
                        val labeledValueObj = labeledValue as? CNLabeledValue
                        val phoneNumber = labeledValueObj?.value as? CNPhoneNumber
                        phoneNumber?.stringValue
                    } ?: emptyList()
                    
                    phoneNumbers?.forEach { phoneNumber ->
                        contacts.add(Contact(name, phoneNumber))
                    }
                }
                continuation.resume(Unit)
            } catch (e: Throwable) {
                // handle error
                continuation.resume(Unit)
            }
        }
        
        contacts
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider = ContactsProvider()
}
