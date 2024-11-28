package org.videotrade.shopot.androidSpecificApi

import org.koin.dsl.module
import org.videotrade.shopot.multiplatform.AndroidNetworkHelper
import org.videotrade.shopot.multiplatform.NetworkHelper

// PlatformModule.android.kt
actual val platformModule = module {
    single<NetworkHelper> { AndroidNetworkHelper(getContextObj.getContext()) }
}
