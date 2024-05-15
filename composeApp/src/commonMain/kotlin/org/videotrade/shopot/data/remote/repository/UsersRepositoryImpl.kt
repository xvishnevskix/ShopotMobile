package org.videotrade.shopot.data.remote.repository
import org.videotrade.shopot.domain.model.UserItem
import org.videotrade.shopot.domain.repository.UsersRepository

class UsersRepositoryImpl : UsersRepository {

    private var users =
        mutableListOf(
            UserItem("1", true, "", "Антон", "Иванов", "", 2, "10", "",""),
            UserItem("2", true, "", "Мансур", "Дандаев", "", 2, "10", "","")
        )


    override fun getUsers(): List<UserItem> {

        return users.toList()

    }


    override fun delUser(user: UserItem) {
        users = users.filter { it.id != user.id }.toMutableList()
    }



    override fun addUser(user: UserItem) {

         users.add(user)
    }

}