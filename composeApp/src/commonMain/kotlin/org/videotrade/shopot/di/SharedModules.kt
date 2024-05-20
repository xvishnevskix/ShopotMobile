package org.videotrade.shopot.di


import org.koin.dsl.module
import org.videotrade.shopot.data.remote.repository.ChatRepositoryImpl
import org.videotrade.shopot.data.remote.repository.ProfileRepositoryImpl
import org.videotrade.shopot.data.remote.repository.UsersRepositoryImpl
import org.videotrade.shopot.data.remote.repository.WsRepositoryImpl
import org.videotrade.shopot.domain.repository.ChatRepository
import org.videotrade.shopot.domain.repository.ProfileRepository
import org.videotrade.shopot.domain.repository.UsersRepository
import org.videotrade.shopot.domain.repository.WsRepository
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.UsersUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import org.videotrade.shopot.presentation.screens.contacts.SharedViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel


private val domainModule = module {
    factory { ChatUseCase() }
    factory { UsersUseCase() }
    factory { ProfileUseCase() }
    factory { WsUseCase() }
    
}


private val presentationModule = module {
    single<WsRepository> {
        WsRepositoryImpl()
    }
    
    single<UsersRepository> {
        UsersRepositoryImpl()
    }
    single<ProfileRepository> {
        ProfileRepositoryImpl()
    }
    
    single<WsRepository> {
        WsRepositoryImpl()
    }
    
    single { MainViewModel() }
    single { IntroViewModel() }
    
    
    
    single<ChatRepository> {
        ChatRepositoryImpl()
    }
    single { ChatViewModel() }
    
    
    
    single { ContactsViewModel() }
    single { SharedViewModel() }
    
}

private fun getAllModules() = listOf(
    domainModule, presentationModule
)

fun getSharedModules() = getAllModules()