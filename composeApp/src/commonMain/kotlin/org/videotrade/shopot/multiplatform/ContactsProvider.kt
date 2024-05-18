
package org.videotrade.shopot.multiplatform

expect class ContactsProvider {
    suspend fun getContacts(): List<Contact>
}

expect object ContactsProviderFactory {
    fun create(): ContactsProvider
}
