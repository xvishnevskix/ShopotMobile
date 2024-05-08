package org.videotrade.shopot

import org.koin.core.context.startKoin
import org.videotrade.shopot.di.getSharedModules

fun doInitKoin() {
    startKoin {
        modules(getSharedModules())
    }
}