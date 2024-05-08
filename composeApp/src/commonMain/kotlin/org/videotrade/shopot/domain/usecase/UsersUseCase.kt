package org.videotrade.shopot.domain.usecase

import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.domain.repository.UsersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UsersUseCase : KoinComponent {
    private val repository: UsersRepository by inject()

    fun getUsers(): List<UserItem> {
        return repository.getUsers()
    }


    fun delUser(user: UserItem) {
        return repository.delUser(user)
    }

    fun addUser(user: UserItem) {
        return repository.addUser(user)
    }

}