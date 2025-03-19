package org.videotrade.shopot.multiplatform

interface SwiftFuncsHelper {
    fun endCall()
    fun stopAVAudioSession()
    
    fun setAVAudioSession()
    
    fun initCallKit(phone: String, callId: String)
    
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
    
    fun initCallKit(phone: String, callId: String) {
        helper.initCallKit(phone, callId)
    }
    
}