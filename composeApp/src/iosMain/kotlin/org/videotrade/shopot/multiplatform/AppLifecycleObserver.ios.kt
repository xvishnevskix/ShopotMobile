package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.videotrade.shopot.domain.usecase.CommonUseCase
import org.videotrade.shopot.domain.usecase.ProfileUseCase
import org.videotrade.shopot.domain.usecase.WsUseCase
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationWillResignActiveNotification
import platform.darwin.NSObject

class IOSAppLifecycleObserver : AppLifecycleObserver, KoinComponent {
    private val wsUseCase: WsUseCase by inject()
    private val commonUseCase: CommonUseCase by inject()
    private val profileUseCase: ProfileUseCase by inject()
    private val networkHelper: NetworkHelper by inject() // DI для NetworkHelper
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    
    private val networkListener: NetworkListener by lazy {
        NetworkListener(networkHelper)
    }
    
    init {
        coroutineScope.launch {
            networkListener.networkStatus.collect { _ ->
            }
        }
    }
    
    override fun onAppBackgrounded() {
        coroutineScope.launch {
            println("iOS: App moved to background - disconnect")
        }
    }
    
    override fun onAppForegrounded() {
        println("iOS: App moved to foreground - checking WebSocket connection")
        if (commonUseCase.mainNavigator.value !== null && wsUseCase.wsSession.value?.isActive == false) {
            wsUseCase.setConnection(false)
            println("iOS: Reconnecting WebSocket")
            coroutineScope.launch {
                wsUseCase.connectionWs(
                    profileUseCase.getProfile().id,
                    commonUseCase.mainNavigator.value!!
                )
            }
        }
    }
}

class LifecycleNotifier : NSObject() {
    
    private lateinit var lifecycleObserver: IOSAppLifecycleObserver
    
    @OptIn(ExperimentalForeignApi::class)
    fun setup(observer: IOSAppLifecycleObserver) {
        this.lifecycleObserver = observer
        
        val notificationCenter = NSNotificationCenter.defaultCenter
        notificationCenter.addObserver(
            this,
            NSSelectorFromString("onAppForegrounded"),
            UIApplicationDidBecomeActiveNotification,
            null
        )
        notificationCenter.addObserver(
            this,
            NSSelectorFromString("onAppBackgrounded"),
            UIApplicationWillResignActiveNotification,
            null
        )
    }
    
    @ObjCAction
    fun onAppForegrounded() {
        lifecycleObserver.onAppForegrounded()
    }
    
    @ObjCAction
    fun onAppBackgrounded() {
        lifecycleObserver.onAppBackgrounded()
    }
}


actual fun getAppLifecycleObserver(): AppLifecycleObserver {
    val observer = IOSAppLifecycleObserver()
    val notifier = LifecycleNotifier()
    notifier.setup(observer)
    return observer
}
