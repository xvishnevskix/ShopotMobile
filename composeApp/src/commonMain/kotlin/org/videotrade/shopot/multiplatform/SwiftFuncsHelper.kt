package org.videotrade.shopot.multiplatform

interface SwiftFuncsHelper {
    fun testFunc()
    
    fun endCall()
    
}

class SwiftFuncsClass(private val helper: SwiftFuncsHelper) {
    fun endCall() {
        helper.endCall()
    }
}