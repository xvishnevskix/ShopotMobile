package org.videotrade.shopot.multiplatform

import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityFlags
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.SystemConfiguration.kSCNetworkReachabilityFlagsReachable

@OptIn(ExperimentalForeignApi::class)
actual fun checkNetwork(): Boolean {
    memScoped {
        val reachability = SCNetworkReachabilityCreateWithName(null, "www.google.com") ?: return false
        val flags = alloc<UIntVar>() // Используем UIntVar для флагов
        if (!SCNetworkReachabilityGetFlags(reachability, flags.ptr)) { // Проверяем булев результат
            return false
        }
        return (flags.value and kSCNetworkReachabilityFlagsReachable) != 0u
    }
}