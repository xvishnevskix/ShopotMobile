package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.domain.model.ContactDTO

actual class ContactsProvider(private val context: Context) {
    @SuppressLint("Range")
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactDTO>()
        
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        
        cursor?.use {
            val contactIdIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val displayNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val contactId = it.getLong(contactIdIndex)
                val displayName = it.getString(displayNameIndex)
                val phone = it.getString(numberIndex)
                
                // Теперь получим firstName и lastName
                val nameCursor = context.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
                    ),
                    "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE),
                    null
                )
                
                var firstName: String? = null
                var lastName: String? = null
                
                nameCursor?.use { nc ->
                    if (nc.moveToFirst()) {
                        firstName = nc.getString(nc.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                        lastName = nc.getString(nc.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                    }
                }
                
                contacts.add(ContactDTO(firstName = firstName ?: "", lastName = lastName ?: "", phone =  phone, icon = null))
            }
        }
        
        contacts
    }
}

actual object ContactsProviderFactory {
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context
    }
    
    actual fun create(): ContactsProvider {
        return ContactsProvider(applicationContext)
    }
}
