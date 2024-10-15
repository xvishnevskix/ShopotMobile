package org.videotrade.shopot.multiplatform

import cafe.adriel.voyager.navigator.Navigator


expect class CallProvider {
    fun switchToSpeaker(switch: Boolean)
    
}


expect object CallProviderFactory {
    fun create(): CallProvider
}



expect fun onResumeCallActivity(navigator: Navigator)

expect fun isCallActiveNatific()

expect fun clearNotificationsForChannel(channelId: String)

expect fun closeAppAndCloseCall()
