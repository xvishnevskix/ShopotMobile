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
    private val networkHelper: NetworkHelper by inject() // DI для NetworkHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    private val networkListener: NetworkListener by lazy {
        NetworkListener(networkHelper)
    }
    
    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        
        // Запускаем прослушивание сети
        coroutineScope.launch {
            networkListener.networkStatus.collect { _ ->
            }
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    override fun onAppBackgrounded() {
        coroutineScope.launch {
            val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
            callViewModel.replaceInCall.value = false
            println("App in background: disconnecting WebSocket")
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    override fun onAppForegrounded() {
        println("App in foreground: checking WebSocket connection")
        if (commonUseCase.mainNavigator.value !== null && wsUseCase.wsSession.value?.isActive == false) {
            wsUseCase.setConnection(false)
            println("Reconnecting WebSocket")
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
