package org.videotrade.shopot.multiplatform

expect class DeviceIdProvider {
    fun getDeviceId(): String?
}

expect object DeviceIdProviderFactory {
    fun create(): DeviceIdProvider
}
