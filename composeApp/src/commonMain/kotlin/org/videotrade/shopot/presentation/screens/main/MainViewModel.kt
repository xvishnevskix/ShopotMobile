package org.videotrade.shopot.presentation.screens.main

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.domain.usecase.UsersUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {
    private val useCase: UsersUseCase by inject()
    
    private val _chats = MutableStateFlow<List<UserItem>>(listOf())
    
    val chats: StateFlow<List<UserItem>> = _chats.asStateFlow()
    
    init {
        loadUsers()
    }
    
    private fun loadUsers() {
        viewModelScope.launch {
            _chats.value = useCase.getUsers()
        }
    }
    
    fun deleteUserItem(user: UserItem) {
        viewModelScope.launch {
            useCase.delUser(user)
            // После удаления обновить список
            _chats.update { currentUsers ->
                currentUsers.filter { it.id != user.id }
            }
        }
    }
    
    
    fun addUserItem(user: UserItem) {
        viewModelScope.launch {
            useCase.addUser(user)
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