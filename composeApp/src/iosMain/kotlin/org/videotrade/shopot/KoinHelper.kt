package org.videotrade.shopot

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.ConnectionChecker
import org.videotrade.shopot.multiplatform.EncapsulateChecker
import org.videotrade.shopot.multiplatform.EncryptionWrapperChecker
import org.videotrade.shopot.multiplatform.InternetConnectionChecker


internal fun provideChecker(realChecker: ConnectionChecker): Module = module {
    single<InternetConnectionChecker> { InternetConnectionChecker(realChecker) }
}

internal fun provideEncapsulateChecker(realChecker: EncryptionWrapperChecker): Module = module {
    single<EncapsulateChecker> { EncapsulateChecker(realChecker) }
}

fun doInitKoin(checker: EncryptionWrapperChecker) {
    val modules = getSharedModules() + provideEncapsulateChecker(checker)
    
    startKoin {
        modules(modules)
    }
}