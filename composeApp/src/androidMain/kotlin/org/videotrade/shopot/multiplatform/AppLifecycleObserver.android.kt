package org.videotrade.shopot.multiplatform

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import org.videotrade.shopot.presentation.screens.call.CallViewModel
import org.videotrade.shopot.presentation.screens.chat.ChatViewModel


class AndroidAppLifecycleObserver : LifecycleObserver, AppLifecycleObserver, KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    private val commonUseCase: CommonUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onAppBackgrounded() {
        coroutineScope.launch {
            val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
            
            callViewModel.replaceInCall.value = false
            println("iOS: Приложение свернуто disconnect")
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onAppForegrounded() {
        
        println("iOS: Приложение развернуто ${wsUseCase.wsSession.value?.isActive}")
        
        if (commonUseCase.mainNavigator.value !== null && wsUseCase.wsSession.value?.isActive == false) {
            wsUseCase.setConnection(false)
            println("iOS: Reconnect")
            
            coroutineScope.launch {
                wsUseCase.connectionWs(
                    profileUseCase.getProfile().id,
                    commonUseCase.mainNavigator.value!!
                )
            }
        }
    }
}

actual fun getAppLifecycleObserver(): AppLifecycleObserver {
    return AndroidAppLifecycleObserver()
}
