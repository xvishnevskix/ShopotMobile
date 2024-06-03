package org.videotrade.shopot.presentation.screens.common

import com.dokar.sonner.ToasterState
import com.dokar.sonner.rememberToasterState
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import org.koin.core.component.KoinComponent

class ToasterViewModel : ViewModel(), KoinComponent {
    
    val toaster = ToasterState(viewModelScope)
    
    
    
}
