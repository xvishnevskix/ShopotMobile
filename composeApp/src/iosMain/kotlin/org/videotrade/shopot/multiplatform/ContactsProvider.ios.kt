// Файл: composeApp/src/iosMain/kotlin/org/videotrade/shopot/multiplatform/ContactsProvider.ios.kt
//package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Contacts.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import org.videotrade.shopot.domain.model.ContactDTO // Импортируйте ContactDTO из общего модуля

actual class ContactsProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.Default) {
        val store = CNContactStore()
        val keysToFetch = listOf(
            CNContactGivenNameKey,
            CNContactFamilyNameKey,
            CNContactPhoneNumbersKey
        )
        
        val request = CNContactFetchRequest(keysToFetch = keysToFetch)
        val contacts = mutableListOf<ContactDTO>()
        
        suspendCoroutine<Unit> { continuation ->
            try {
                store.enumerateContactsWithFetchRequest(request, error = null) { contact, _ ->
                    contact?.let {
                        val givenName = contact.givenName
                        val familyName = contact.familyName
                        val phoneNumbers = contact.phoneNumbers.mapNotNull { labeledValue ->
                            (labeledValue as? CNLabeledValue)?.value as? CNPhoneNumber
                        }?.map { it.stringValue } ?: emptyList()
                        
                        phoneNumbers.forEach { phone ->
                            contacts.add(ContactDTO(
                                firstName = givenName,
                                lastName = familyName,
                                phone = phone
                            ))
                        }
                    }
                }
                continuation.resume(Unit)
            } catch (e: Throwable) {
                continuation.resume(Unit)
            }
        }
        contacts
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider = ContactsProvider()
}
