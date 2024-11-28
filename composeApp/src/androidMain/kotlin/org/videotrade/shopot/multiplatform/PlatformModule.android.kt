package org.videotrade.shopot.multiplatform

import org.koin.core.module.Module
import org.koin.dsl.module
import org.videotrade.shopot.androidSpecificApi.getContextObj

actual val platformModule = module {
    single<NetworkHelper> { AndroidNetworkHelper(getContextObj.getContext()) }
}
