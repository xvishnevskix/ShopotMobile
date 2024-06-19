package org.videotrade.shopot.data.remote.repository

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.videotrade.shopot.domain.repository.CommonRepository

class CommonRepositoryImpl : CommonRepository, KoinComponent {
    
    
    private val _mainNavigator = MutableStateFlow<Navigator?>(null)
    override val mainNavigator: StateFlow<Navigator?> get() = _mainNavigator
    
    
    override fun setNavigator(mainNavigatorNew: Navigator) {
        
        _mainNavigator.value = mainNavigatorNew
    }
    
    
}