package org.videotrade.shopot.presentation.commonViewModules

import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.usecase.WsUseCase

class CommonViewModel : ViewModel(), KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    
    val toaster = ToasterState(viewModelScope)

//    val showButtonNav = MutableStateFlow(true)
    
    val mainNavigator = MutableStateFlow<Navigator?>(null)
    
    
    fun setMainNavigator(value: Navigator) {
        mainNavigator.value = value
    }
    
    fun connectionWs(navigator: Navigator) {
        viewModelScope.launch {
            wsUseCase.connectionWs("11111", navigator)
            
        }
    }
//    fun setShowButtonNav(value: Boolean) {
//        showButtonNav.value = value
//    }
}
