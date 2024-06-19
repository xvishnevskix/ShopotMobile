//package org.videotrade.shopot.androidSpecificApi
//
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleObserver
//import androidx.lifecycle.OnLifecycleEvent
//import org.koin.core.component.KoinComponent
//import org.koin.core.component.inject
//import org.videotrade.shopot.domain.usecase.WsUseCase
//
//class AppLifecycleObserver : LifecycleObserver, KoinComponent {
//    private val wsUseCase: WsUseCase by inject()
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onAppBackgrounded() {
//        // Приложение свернуто
//        println("Приложение свернуто")
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//     fun onAppForegrounded() {
//        // Приложение развернуто
//
//
//        println("Приложение развернуто ${wsUseCase.wsSession.value}")
//    }
//}
