package org.videotrade.shopot.multiplatform
import android.content.Context
import android.provider.ContactsContract
import android.provider.Settings
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.domain.model.ContactDTO

actual class ContactsProvider (private val context: Context) {
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactDTO>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            
            while (it.moveToNext()) {
                
                val name = it.getString(nameIndex)
                val phone = it.getString(numberIndex)
                contacts.add(ContactDTO(name, phone))
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

