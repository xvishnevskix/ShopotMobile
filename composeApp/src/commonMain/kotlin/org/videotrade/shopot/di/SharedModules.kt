package org.videotrade.shopot.di


import org.koin.dsl.module
import org.videotrade.shopot.data.remote.repository.CallRepositoryImpl
import org.videotrade.shopot.data.remote.repository.ChatRepositoryImpl
import org.videotrade.shopot.data.remote.repository.ContactsRepositoryImpl
import org.videotrade.shopot.data.remote.repository.ProfileRepositoryImpl
import org.videotrade.shopot.data.remote.repository.ChatsRepositoryImpl
import org.videotrade.shopot.data.remote.repository.WsRepositoryImpl
import org.videotrade.shopot.domain.repository.CallRepository
import org.videotrade.shopot.domain.repository.ChatRepository
import org.videotrade.shopot.domain.repository.ContactsRepository
import org.videotrade.shopot.domain.repository.ProfileRepository
import org.videotrade.shopot.domain.repository.ChatsRepository
import org.videotrade.shopot.domain.repository.WsRepository
import org.videotrade.shopot.domain.usecase.CallUseCase
import org.videotrade.shopot.domain.usecase.ChatUseCase
import org.videotrade.shopot.domain.usecase.ContactsUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.ChatsUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel
import org.videotrade.shopot.presentation.screens.common.CommonViewModel
import org.videotrade.shopot.presentation.screens.contacts.ContactsViewModel
import org.videotrade.shopot.presentation.screens.intro.IntroViewModel
import org.videotrade.shopot.presentation.screens.main.MainViewModel


private val domainModule = module {
    factory { ChatUseCase() }
    factory { ChatsUseCase() }
    factory { ProfileUseCase() }
    factory { WsUseCase() }
    factory { CallUseCase() }
    factory { ContactsUseCase() }
    
}


private val presentationModule = module {
    single<WsRepository> {
        WsRepositoryImpl()
    }
    
    single<ChatsRepository> {
        ChatsRepositoryImpl()
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
    
    
    single<CallRepository> {
        CallRepositoryImpl()
    }
    single { CallViewModel() }
    
    
    single<ContactsRepository> {
        ContactsRepositoryImpl()
    }
    single { ContactsViewModel() }
    
    single { CommonViewModel() }
    
    
}

private fun getAllModules() = listOf(
    domainModule, presentationModule
)

fun getSharedModules() = getAllModules()