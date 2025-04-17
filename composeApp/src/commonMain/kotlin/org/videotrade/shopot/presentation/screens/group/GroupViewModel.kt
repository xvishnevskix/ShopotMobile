package org.videotrade.shopot.presentation.screens.group


import androidx.compose.runtime.mutableStateMapOf
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
import org.videotrade.shopot.data.origin
import org.videotrade.shopot.domain.model.GroupInfo
import org.videotrade.shopot.domain.model.GroupUserDTO
import org.videotrade.shopot.domain.model.GroupUserRole
import org.videotrade.shopot.domain.model.WsReconnectionCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import org.videotrade.shopot.presentation.screens.test.sendMessageOrReconnect

class GroupViewModel : ViewModel(), KoinComponent {
    private val contactsUseCase: ContactsUseCase by inject()
    private val wsUseCase: WsUseCase by inject()
    private val contactsViewModel: ContactsViewModel by inject()
    private val profileUseCase: ProfileUseCase by inject()
    
    val groupUserRole = MutableStateFlow<GroupUserRole?>(null)
    val groupUsers = MutableStateFlow<List<GroupUserDTO>>(listOf())
    
    
    fun addUsersToGroup(chatId: String) {
        viewModelScope.launch {
            
            val selectedContactIds = contactsViewModel.selectedContacts.mapNotNull { it.id }
            
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
            
            
            val res = sendMessageOrReconnect(
                wsSession = wsUseCase.wsSession.value,
                jsonContent = jsonContent,
                wsReconnectionCase = WsReconnectionCase.ChatWs
            )
            
            if (res) {
                
                loadGroupUsers(chatId)
                
                contactsViewModel.clearSelectedContacts()
            }
        }
        
    }
    
    fun removeUserFromGroup(chatId: String, userId: String) {
        viewModelScope.launch {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "removeUserFromGroup")
                    put("chatId", chatId)
                    put("userId", userId)
                    
                }
            )
            
            println("jsonContent $jsonContent")
            
            val res = sendMessageOrReconnect(
                wsSession = wsUseCase.wsSession.value,
                jsonContent = jsonContent,
                wsReconnectionCase = WsReconnectionCase.ChatWs
            )
            
            if (res) removeUserById(userId)
            
        }
    }
    
    
    fun leaveGroupChat(chatId: String) {
        viewModelScope.launch {
            
            val jsonContent = Json.encodeToString(
                buildJsonObject {
                    put("action", "leaveGroupChat")
                    put("chatId", chatId)
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
    
    
    fun loadGroupUsers(chatId: String) {
        
        viewModelScope.launch {
            try {
                val groupUsersGet =
                    origin().get<GroupInfo>("group_chat/chatInfo?chatId=$chatId")
                
                val groupUsersFilter = mutableListOf<GroupUserDTO>()
                
                if (groupUsersGet != null) {
                    groupUserRole.value = groupUsersGet.senderRole
                    
                    
                    for (groupUser in groupUsersGet.groupUsers) {
                        fun normalizePhoneNumber(phone: String): String {
                            return phone.replace(Regex("[^0-9]"), "")
                        }
                        
                        if (profileUseCase.getProfile().phone == normalizePhoneNumber(groupUser.phone)) {
                            groupUsersFilter.add(
                                groupUser.copy(
                                    firstName = "Вы",
                                    lastName = ""
                                )
                            )
                            continue
                        }
                        
                        val findInContacts = contactsUseCase.contacts.value.find {
                            normalizePhoneNumber(it.phone) == normalizePhoneNumber(groupUser.phone)
                        }
                        
                        if (findInContacts !== null && findInContacts.firstName !== null && findInContacts.lastName !== null) {
                            groupUsersFilter.add(
                                groupUser.copy(
                                    firstName = findInContacts.firstName,
                                    lastName = findInContacts.lastName
                                )
                            )
                        } else {
                            groupUsersFilter.add(groupUser)
                        }
                        
                    }
                }
                
                groupUsers.value = groupUsersFilter
                
            } catch (e: Exception) {
            }
        }
        
    }
    
    
    private val _cachedGroupUsers =
        mutableStateMapOf<String, List<GroupUserDTO>>() // Кэш пользователей чатов
    val cachedGroupUsers: Map<String, List<GroupUserDTO>> get() = _cachedGroupUsers
    
    suspend fun getGroupUsers(chatId: String): List<GroupUserDTO> {
        
        _cachedGroupUsers[chatId]?.let { return it }
        
        return try {
            val groupUsersGet =
                origin().get<GroupInfo>("group_chat/chatInfo?chatId=$chatId")
            
            
            if (groupUsersGet != null) {
                _cachedGroupUsers[chatId] = groupUsersGet.groupUsers
            } // Сохраняем в кэше
            
            groupUsersGet?.groupUsers ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun clearCache() {
        _cachedGroupUsers.clear() // Очищаем кэш (например, если юзер вышел)
    }
    
    fun removeUserById(userId: String) {
        groupUsers.value = groupUsers.value.filter { it.id != userId }
    }
}


