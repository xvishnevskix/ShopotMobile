package org.videotrade.shopot.multiplatform


expect class CallProvider {
    fun switchToSpeaker(switch: Boolean)
    
}


expect object CallProviderFactory {
    fun create(): CallProvider
}
