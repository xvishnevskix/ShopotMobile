package org.videotrade.shopot.multiplatform

interface SwiftFuncsHelper {
    fun testFunc()
}

class SwiftFuncsClass(private val helper: SwiftFuncsHelper) {
    fun sendAA() {
        helper.testFunc()
    }
}