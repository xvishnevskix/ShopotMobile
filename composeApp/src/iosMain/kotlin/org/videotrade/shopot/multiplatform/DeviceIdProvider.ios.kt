package org.videotrade.shopot.multiplatform

import platform.UIKit.UIDevice

actual class DeviceIdProvider {
    actual fun getDeviceId(): String? {
        // В iOS используйте идентификатор устройства или подобный идентификатор.
        // Здесь используется модель устройства в качестве примера.
        return UIDevice.currentDevice.identifierForVendor?.UUIDString() ?: null
    }
}

actual object DeviceIdProviderFactory {
    actual fun create(): DeviceIdProvider = DeviceIdProvider()
}