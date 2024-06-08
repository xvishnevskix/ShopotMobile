package org.videotrade.shopot.data.remote.repository

import cafe.adriel.voyager.navigator.Navigator
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.repository.ContactsRepository
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.presentation.screens.main.MainScreen

class ContactsRepositoryImpl : ContactsRepository, KoinComponent {
    private val _contacts = MutableStateFlow<List<ContactDTO>>(
        listOf(
        
        )
    )
    override val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    
    
    override suspend fun fetchContacts(): List<ContactDTO>? {
        try {
            
            
            val newContacts = mutableListOf<ContactDTO>()
            
            val contactsNative = ContactsProviderFactory.create().getContacts()
            val contactsGet = origin().get<List<ContactDTO>>("user/getAll") ?: return null
            
            
            // Функция для нормализации номера телефона
            fun normalizePhoneNumber(phone: String): String {
                return phone.replace(Regex("[^0-9]"), "")
            }
            
            // Преобразование контактов из backend в словарь по нормализованному номеру телефона для быстрого поиска
            val backendContactsMap = contactsGet.associateBy {
                normalizePhoneNumber(it.phone)
            }
            
            
            println("contactst11231 $backendContactsMap ")
            
            // Сравнение контактов по нормализованному номеру телефона
            for (contact in contactsNative) {
                val normalizedPhone = normalizePhoneNumber(contact.phone)


                
                val backendContact = backendContactsMap[normalizedPhone]
                
                
                println("normalizedPhone $normalizedPhone $backendContactsMap")
                
                if (backendContact != null) {
                    newContacts.add(
                        ContactDTO(
                            backendContact.id,
                            backendContact.login,
                            backendContact.email,
                            contact.firstName,
                            contact.lastName,
                            backendContact.description,
                            normalizedPhone,
                            backendContact.status,
                            icon = backendContact.icon,
                        )
                    )
                }
            }
            
            
            println("contactst $contactsGet $contactsNative")
            println("newContacts $newContacts")
            
            _contacts.value = newContacts
            
            return newContacts
        } catch (e: Exception) {
            
            println("ERROR111: $e")
            
            return null
            
        }
        
        
    }
    
    override  fun getContacts(): List<ContactDTO> {
        
        return contacts.value
    }
    
    
    
    override suspend fun createChat(profileId: String, contact: ContactDTO, navigator: Navigator) {
        val wsUseCase: WsUseCase by inject()


//        val jsonContent = Json.encodeToString(
//            buildJsonObject {
//                put("firstUserId", profileId)
//                put("secondUserId", contact.id)
//
//            }
//        )
//
//        println("jsonContent $jsonContent")
//
//        @Serializable
//        data class PersonalChat(
//            val id: String,
//            val createdAt: String,
//            val firstUserId: String,
//            val secondUserId: String
//        )
//
//        val contactsGet = origin().post<PersonalChat>("personal-chat", jsonContent)
        
        
        try {
            val jsonContentSocket = Json.encodeToString(
                buildJsonObject {
                    put("action", "createChat")
                    put("firstUserId", profileId)
                    put("secondUserId", contact.id)
                }
            )
            
            wsUseCase.wsSession.value?.send(Frame.Text(jsonContentSocket))
            
            
            navigator.push(MainScreen())
        } catch (e: Exception) {
            
            println("error createChat")
        }
        
        
    }
    
    override fun clearData() {
        _contacts.value = emptyList()
    }
}
