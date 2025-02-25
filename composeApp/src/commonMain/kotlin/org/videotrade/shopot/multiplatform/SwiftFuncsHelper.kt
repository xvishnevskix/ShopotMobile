package org.videotrade.shopot.multiplatform

interface SwiftFuncsHelper {
    fun testFunc()
    
    fun endCall()
    fun stopAVAudioSession()
    
    fun setAVAudioSession()
    
}

class SwiftFuncsClass(private val helper: SwiftFuncsHelper) {
    fun endCall() {
        helper.endCall()
    }
    fun stopAVAudioSession() {
        helper.stopAVAudioSession()
    }
    
    fun setAVAudioSession() {
        helper.setAVAudioSession()
    }
    
    
    
}