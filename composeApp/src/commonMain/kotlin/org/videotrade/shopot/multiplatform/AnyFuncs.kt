package org.videotrade.shopot.multiplatform

expect fun getPlatform(): String

expect fun getBuildVersion(): Long

expect fun startOutgoingCall()

expect fun simulateIncomingCall()


expect fun closeApp()


