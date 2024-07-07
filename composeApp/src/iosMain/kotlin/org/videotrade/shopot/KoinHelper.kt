package org.videotrade.shopot

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.CipherInterface
import org.videotrade.shopot.multiplatform.CipherWrapper

internal fun provideEncapsulateChecker(cipherInterface: CipherInterface): Module = module {
    single<CipherWrapper> { CipherWrapper(cipherInterface) }
}

fun doInitKoin(checker: CipherInterface) {
    val modules = getSharedModules() + provideEncapsulateChecker(checker)
    
    startKoin {
        modules(modules)
    }
}