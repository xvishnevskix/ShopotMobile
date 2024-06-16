package org.videotrade.shopot.presentation.screens.common

import cafe.adriel.voyager.navigator.Navigator
import com.dokar.sonner.ToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent

class CommonViewModel : ViewModel(), KoinComponent {
    
    val toaster = ToasterState(viewModelScope)

//    val showButtonNav = MutableStateFlow(true)
    
    val mainNavigator = MutableStateFlow<Navigator?>(null)
    
    
    fun setMainNavigator(value: Navigator) {
        mainNavigator.value = value
    }
//    fun setShowButtonNav(value: Boolean) {
//        showButtonNav.value = value
//    }
}
