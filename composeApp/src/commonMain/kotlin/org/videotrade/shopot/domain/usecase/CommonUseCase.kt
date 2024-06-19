package org.videotrade.shopot.domain.usecase

import cafe.adriel.voyager.navigator.Navigator
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.repository.CommonRepository
import org.videotrade.shopot.domain.repository.WsRepository

class CommonUseCase : KoinComponent {
    private val repository: CommonRepository by inject()
    val mainNavigator: StateFlow<Navigator?> get() = repository.mainNavigator
    
    fun setNavigator(mainNavigatorNew: Navigator) {
        repository.setNavigator(mainNavigatorNew)
    }
    
    
}