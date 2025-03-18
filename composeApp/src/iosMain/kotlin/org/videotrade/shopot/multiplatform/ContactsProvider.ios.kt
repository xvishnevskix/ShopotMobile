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
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

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
    
    actual fun sendMessageInvite() {
        val message =
            "Привет!  \n" +
                    "\n" +
                    "Приглашаю тебя в Шёпот — безопасный мессенджер для общения и обмена идеями. Присоединяйся, чтобы быть на связи и делиться мыслями в защищённой среде.  \n" +
                    "\n" +
                    "\uD83D\uDC49 [shopot.videotrade.ru]\n" +
                    "\n" +
                    "Жду тебя там! \uD83D\uDD12"
        
        // Создаем массив для данных, которые будем шарить
        val itemsToShare = listOf(message)
        
        // Инициализируем UIActivityViewController
        val activityViewController = UIActivityViewController(itemsToShare, applicationActivities = null)
        
        // Получаем текущий viewController
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        
        // Отображаем UIActivityViewController
        rootViewController?.presentViewController(activityViewController, animated = true, completion = null)
    }
}

actual object ContactsProviderFactory {
    actual fun create(): ContactsProvider {
        return ContactsProvider()
    }
}



