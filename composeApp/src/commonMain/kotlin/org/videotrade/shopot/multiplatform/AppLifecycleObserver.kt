package org.videotrade.shopot.multiplatform

import cafe.adriel.voyager.navigator.Navigator
import org.videotrade.shopot.presentation.screens.common.CommonViewModel


interface AppLifecycleObserver {
    fun onAppBackgrounded()
    fun onAppForegrounded()
}


expect fun getAppLifecycleObserver(): AppLifecycleObserver
