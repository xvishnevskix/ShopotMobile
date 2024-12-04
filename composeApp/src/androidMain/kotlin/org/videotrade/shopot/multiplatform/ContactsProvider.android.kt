package org.videotrade.shopot.multiplatform

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.domain.model.ContactDTO

actual class ContactsProvider(private val context: Context) {
    @SuppressLint("Range")
    actual suspend fun getContacts(): List<ContactDTO> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<ContactDTO>()
        
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        
        // Условие фильтрации для получения только видимых контактов
        val selection = "${ContactsContract.Contacts.IN_VISIBLE_GROUP} = 1"
        
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            selection,
            null,
            null
        )
        
        cursor?.use {
            val displayNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val displayName = it.getString(displayNameIndex)
                val phone = it.getString(numberIndex)
                
                contacts.add(
                    ContactDTO(
                        firstName = displayName ?: "",
                        lastName = "",
                        phone = phone,
                        icon = null
                    )
                )
            }
        }
        
        contacts
    }
    
    actual fun sendMessageInvite() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Приглашаем в шепот господа") // Текст сообщения
        }
        
        // Показываем выбор приложений для отправки
        val chooserIntent = Intent.createChooser(shareIntent, "Share via")
        getContextObj.getActivity()?.startActivity(chooserIntent)
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
