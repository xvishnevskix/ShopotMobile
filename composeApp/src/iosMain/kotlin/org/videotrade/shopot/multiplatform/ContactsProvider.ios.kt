package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Contacts.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
                    package org.videotrade.shopot.multiplatform
                    
                    import kotlinx.cinterop.ExperimentalForeignApi
                            import kotlinx.coroutines.Dispatchers
                            import kotlinx.coroutines.withContext
                            import platform.Contacts.*
                            import kotlin.coroutines.resume
                            import kotlin.coroutines.suspendCoroutine
                            
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
                                        val phoneNumbers = contact.phoneNumbers?.mapNotNull { labeledValue ->
                                            (labeledValue as? CNLabeledValue)?.value as? CNPhoneNumber
                                        }?.map { it.stringValue } ?: emptyList()
                                        
                                        phoneNumbers.forEach { phone ->
                                            contacts.add(ContactDTO(givenName, familyName, phone))
                                        }
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

                    val phoneNumbers = contact?.phoneNumbers?.mapNotNull { labeledValue ->
                        val labeledValueObj = labeledValue as? CNLabeledValue
                        val phoneNumber = labeledValueObj?.value as? CNPhoneNumber
                        phoneNumber?.stringValue
                    } ?: emptyList()
                    
                    phoneNumbers?.forEach { phone ->
                        contacts.add(ContactDTO(firstName = contact?.givenName, lastName = contact?.familyName,phone = phone))
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
