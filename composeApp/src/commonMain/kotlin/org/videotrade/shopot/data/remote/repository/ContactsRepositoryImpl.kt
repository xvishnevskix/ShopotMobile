package org.videotrade.shopot.data.remote.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.repository.ContactsRepository
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.multiplatform.ContactsProviderFactory
import org.videotrade.shopot.presentation.screens.test.sendMessageOrReconnect

class ContactsRepositoryImpl : ContactsRepository, KoinComponent {
    private val _contacts = MutableStateFlow<List<ContactDTO>>(
        listOf(
        
        )
    )
    override val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    
    
    private val _unregisteredContacts = MutableStateFlow<List<ContactDTO>>(
        listOf(
        
        )
    )
    override val unregisteredContacts: StateFlow<List<ContactDTO>> get() = _unregisteredContacts
    
    
    //    override suspend fun fetchContacts(): List<ContactDTO>? {
//        try {
//
//
//            val newContacts = mutableListOf<ContactDTO>()
//
//            val contactsNative = ContactsProviderFactory.create().getContacts()
//
//
//
//
//
//            val contactsGet = origin().get<List<ContactDTO>>("user/getAll") ?: return null
//
//
//            // Функция для нормализации номера телефона
//            fun normalizePhoneNumber(phone: String): String {
//                return phone.replace(Regex("[^0-9]"), "")
//            }
//
//            // Преобразование контактов из backend в словарь по нормализованному номеру телефона для быстрого поиска
//            val backendContactsMap = contactsGet.associateBy {
//                normalizePhoneNumber(it.phone)
//            }
//
//
//            println("contactst11231 $backendContactsMap ")
//
//            // Сравнение контактов по нормализованному номеру телефона
//            for (contact in contactsNative) {
//                val normalizedPhone = normalizePhoneNumber(contact.phone)
//
//
//                val backendContact = backendContactsMap[normalizedPhone]
//
//
//                println("normalizedPhone $normalizedPhone $backendContactsMap")
//
//                if (backendContact != null) {
//                    newContacts.add(
//                        ContactDTO(
//                            backendContact.id,
//                            backendContact.login,
//                            backendContact.email,
//                            contact.firstName,
//                            contact.lastName,
//                            backendContact.description,
//                            normalizedPhone,
//                            backendContact.status,
//                            icon = backendContact.icon,
//                        )
//                    )
//                }
//            }
//
//
//            println("contactst $contactsGet $contactsNative")
//            println("newContacts $newContacts")
//
//            _contacts.value = newContacts
//
//            return newContacts
//        } catch (e: Exception) {
//
//            println("ERROR111: $e")
//
//            return null
//
//        }
//
//
//    }
    override suspend fun fetchContacts(): List<ContactDTO>? {
        try {
            val contactsNative = ContactsProviderFactory.create().getContacts()
            
            println("contacts contactsNative ${contactsNative}")
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("listContacts", Json.encodeToJsonElement(contactsNative))
                }
            )
            
            println("contacts jsonContent contactsGet ${jsonContent}")
            
            
            val contactsGet = origin().post("contacts/addContactsList", jsonContent) ?: return null
            
            println("contactsGet ${contactsGet}")
            
            val jsonElement = Json.parseToJsonElement(contactsGet)
            
            println("contacts jsonElement ${jsonElement}")
            
            
            val registeredUsers =
                jsonElement.jsonObject["registeredUsers"]?.jsonArray?.let { jsonArray ->
                    jsonArray.map { Json.decodeFromJsonElement<ContactDTO>(it) }
                } ?: emptyList()
            
            val unregisteredUsers =
                jsonElement.jsonObject["unregisteredUsers"]?.jsonArray?.let { jsonArray ->
                    jsonArray.map { Json.decodeFromJsonElement<ContactDTO>(it) }
                } ?: emptyList()
            
            
            _unregisteredContacts.value = unregisteredUsers
            _contacts.value = registeredUsers
            
            return registeredUsers
        } catch (e: Exception) {
            
            println("ERROR111: $e")
            
            return null
            
        }
        
        
    }
    
    
    override fun getContacts(): List<ContactDTO> {
        println("contacts.value ${contacts.value}")
        return contacts.value
    }
    
    
    override suspend fun createChat(profileId: String, contact: ContactDTO) {
        val wsUseCase: WsUseCase by inject()
        
        
        try {
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "createChat")
                    put("firstUserId", profileId)
                    put("secondUserId", contact.id)
                }
            )
            println("error createChat $jsonContent")
            
            
            sendMessageOrReconnect(
                wsUseCase.wsSession.value,
                jsonContent,
                WsReconnectionCase.ChatWs
            )
        } catch (e: Exception) {
            
            println("error createChat")
        }
        
        
    }
    
    override suspend fun createGroupChat(
        users: List<String?>,
        groupName: String,
        ownerId: String,
    ) {
        val wsUseCase: WsUseCase by inject()
        try {
            // Преобразуйте список пользователей в JSON-массив строк
            val usersJsonArray = buildJsonArray {
                users.forEach { user ->
                    add(JsonPrimitive(user))
                }
            }
            println(" createChat $usersJsonArray")
            
            // Создайте JSON-объект с массивом пользователей
            val jsonContentSocket = buildJsonObject {
                put("action", "createGroupChat")
                put("groupName", groupName)
                put("ownerId", ownerId)
                put("users", usersJsonArray)
            }
            
            val jsonString = Json.encodeToString(jsonContentSocket)
            
            println("error createChat $jsonString")
            
            sendMessageOrReconnect(
                wsUseCase.wsSession.value,
                jsonString,
                WsReconnectionCase.ChatWs
            )
            
        } catch (e: Exception) {
            println("error createGroupChat: ${e.message}")
        }
    }
    
    override fun clearData() {
        _contacts.value = emptyList()
    }
}
