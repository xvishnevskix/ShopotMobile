package org.videotrade.shopot.di


import org.videotrade.shopot.data.remote.repository.ChatRepositoryImpl
import org.videotrade.shopot.data.remote.repository.UsersRepositoryImpl
import org.videotrade.shopot.domain.repository.ChatRepository
import org.videotrade.shopot.domain.repository.UsersRepository
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.UsersUseCase
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel
import org.koin.dsl.module




private val domainModule = module {
    factory { ChatUseCase() }
    factory { UsersUseCase() }

}




private val presentationModule = module {
    single<UsersRepository> {
        UsersRepositoryImpl()
    }
    single { MainViewModel() }

    single<ChatRepository> {
        ChatRepositoryImpl()
    }
    single { ChatViewModel() }
}

private fun getAllModules() = listOf(
    domainModule, presentationModule
)

fun getSharedModules() = getAllModules()