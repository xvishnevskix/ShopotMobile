package org.videotrade.shopot.presentation.screens.main

import co.touchlab.kermit.Logger
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.model.ProfileDTO
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.UsersUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase

class MainViewModel : ViewModel(), KoinComponent {
    private val userUseCase: UsersUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val WsUseCase: WsUseCase by inject()
    
    private val _chats = MutableStateFlow<List<UserItem>>(listOf())
    
    val chats: StateFlow<List<UserItem>> = _chats.asStateFlow()
    
    
    val profile = MutableStateFlow<ProfileDTO?>(null)
    
    
    init {
        viewModelScope.launch {
            downloadProfile()
            
            connectionWs("ebe19811-b218-475c-965b-f8c03464ed4f")
            
            
//            profile.collect { updatedProfile ->
//
//
//
//                updatedProfile?.let {
//
//                    loadUsers()
//
//                }
//            }
        }
    }
    
    
    fun connectionWs(userId: String) {
        viewModelScope.launch {
            WsUseCase.connectionWs(userId)
        }
    }
    
    fun getWsSession() {
        viewModelScope.launch {
            val ws = WsUseCase.getWsSession() ?: return@launch
            
            
        }
    }
    
    
    fun getProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase.message
            
            
        }
    }
    
    
    fun downloadProfile() {
        viewModelScope.launch {
            val profileCase = profileUseCase.downloadProfile() ?: return@launch
            
            
            profile.value = profileCase.message
            
            
        }
    }
    
    private fun loadUsers() {
        viewModelScope.launch {
            _chats.value = userUseCase.getUsers()
        }
    }
    
    fun deleteUserItem(user: UserItem) {
        viewModelScope.launch {
            userUseCase.delUser(user)
            // После удаления обновить список
            _chats.update { currentUsers ->
                currentUsers.filter { it.id != user.id }
            }
        }
    }
    
    
    fun addUserItem(user: UserItem) {
        viewModelScope.launch {
            userUseCase.addUser(user)
            // Обновляем список чатов, добавляя новый элемент, если его ещё нет в списке
            val updatedUsers =
                _chats.value.toMutableList()  // Создаем изменяемую копию текущего списка
            if (!updatedUsers.any { it.id == user.id }) {    // Проверяем, нет ли уже такого чата в списке
                updatedUsers.add(user)                      // Добавляем новый чат
                _chats.value = updatedUsers                 // Обновляем значение StateFlow
            }
        }
    }
}