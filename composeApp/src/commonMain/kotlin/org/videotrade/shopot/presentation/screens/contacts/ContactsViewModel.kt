package org.videotrade.shopot.presentation.screens.contacts


import androidx.compose.runtime.mutableStateListOf
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ContactDTO
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.test.sendMessageOrReconnect

class ContactsViewModel() : ViewModel(),
    KoinComponent {
    val selectedContacts = mutableStateListOf<ContactDTO>()
    
    
    private val contactsUseCase: ContactsUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    
    val unregisteredContacts = contactsUseCase.unregisteredContacts
    
    private val _contacts = MutableStateFlow<List<ContactDTO>>(emptyList())
    
    //    val contacts: StateFlow<List<ContactDTO>> get() = _contacts
    val contacts = contactsUseCase.contacts
    
    
    fun fetchContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = contactsUseCase.fetchContacts()
            
            if (contactsSort != null) {
                _contacts.value = contactsSort
            }
        }
    }
    
    
    fun getContacts() {
        viewModelScope.launch {
            
            
            val contactsSort = contactsUseCase.getContacts()
            
            println("contactsSort ${contactsSort}")
            
            if (contactsSort.isEmpty()) {
                fetchContacts()
            } else {
                
                _contacts.value = contactsSort
                
            }
        }
    }
    
    fun createChat(contact: ContactDTO, tabNavigator: TabNavigator? = null) {
        viewModelScope.launch {
            val profile = profileUseCase.getProfile()
            
            contactsUseCase.createChat(profile.id, contact)
            
        }
        
    }
    
    fun createGroupChat(
        groupName: String,
        ownerId: String
    ) {
        viewModelScope.launch {
            try {
                val idUsers = selectedContacts.map { it.id }.toMutableList()
                idUsers.add(profileUseCase.getProfile().id)
                contactsUseCase.createGroupChat(
                    idUsers, groupName,
                    ownerId
                )
            } finally {
                clearSelectedContacts()
            }
        }
    }
    
    fun isContactSelected(contact: ContactDTO): Boolean {
        return selectedContacts.contains(contact)
    }
    
    fun addContact(contact: ContactDTO) {
        if (!selectedContacts.contains(contact)) {
            selectedContacts.add(contact)
        }
    }
    
    fun removeContact(contact: ContactDTO) {
        selectedContacts.remove(contact)
    }
    
    fun clearSelectedContacts() {
        selectedContacts.clear()
    }
    
    fun addUsersToGroup(chatId: String) {
        viewModelScope.launch {
            
            val selectedContactIds = selectedContacts.mapNotNull { it.id }
            
            println("selectedContacts ${selectedContactIds}")
            
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "addUsersToGroup")
                    put("chatId", chatId)
                    put("userIds", buildJsonArray {
                        selectedContactIds.forEach { id ->
                            add(JsonPrimitive(id))
                        }
                    })
                }
            )
            
            println("jsonContent $jsonContent")
            
            
            sendMessageOrReconnect(
                wsSession = wsUseCase.wsSession.value,
                jsonContent = jsonContent,
                wsReconnectionCase = WsReconnectionCase.ChatWs
            )
        }
        
    }
    
    fun removeUserFromGroup(chatId: String, userId: String) {
        viewModelScope.launch {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "addUsersToGroup")
                    put("chatId", chatId)
                    put("userId", userId)
                    
                }
            )
            
            println("jsonContent $jsonContent")
            
            sendMessageOrReconnect(
                wsSession = wsUseCase.wsSession.value,
                jsonContent = jsonContent,
                wsReconnectionCase = WsReconnectionCase.ChatWs
            )
        }
    }
    
}