package org.videotrade.shopot.multiplatform

import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<NetworkHelper> { get<IosApplicationComponent>().networkHelper }
    single<TestFileHelper> { get<IosApplicationComponent>().testFileHelper }
}