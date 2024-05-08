package org.videotrade.shopot.domain.repository

import org.videotrade.shopot.domain.model.UserItem

interface UsersRepository {

//    suspend fun addChats(chats: List<UserItem>): Boolean


    //    fun getChats(): LiveData<List<UserItem>>
    fun getUsers(): List<UserItem>

    fun delUser(user: UserItem)
    fun addUser(user: UserItem)


}