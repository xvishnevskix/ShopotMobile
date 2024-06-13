package org.videotrade.shopot.presentation.screens.common

import com.dokar.sonner.ToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent

class CommonViewModel : ViewModel(), KoinComponent {
    
    val toaster = ToasterState(viewModelScope)
    
    val showButtonNav = MutableStateFlow(true)
    
    
}
