package org.videotrade.shopot.presentation.screens.intro


import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.usecase.WsUseCase

class IntroViewModel : ViewModel(), KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    
    
    init {
        
        
        viewModelScope.launch {
            
            connectionWs()
            
            
        }
    }
    
    
    suspend fun connectionWs() {
        wsUseCase.connectionWs("cf9b66e2-9e18-4342-bbc0-c5144a593f71")
        
        
        
        
    }
    
}


