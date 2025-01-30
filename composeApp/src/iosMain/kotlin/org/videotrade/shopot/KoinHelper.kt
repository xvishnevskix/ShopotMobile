package org.videotrade.shopot

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.CipherInterface
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.IosApplicationComponent
import org.videotrade.shopot.multiplatform.SwiftFuncsIos
import org.videotrade.shopot.multiplatform.platformModule

internal fun provideEncapsulateChecker(cipherInterface: CipherInterface): Module = module {
    single<CipherWrapper> { CipherWrapper(cipherInterface) }
}

fun doInitKoin(
    cipherInterface: CipherInterface,
    appComponent: IosApplicationComponent,
    swiftFuncs: SwiftFuncsIos,
    additionalModules: List<Module> = listOf(),
    appDeclaration: KoinAppDeclaration = {},
) {
    startKoin {
        appDeclaration()
        modules(
            additionalModules + getSharedModules() + listOf(
                provideEncapsulateChecker(cipherInterface), // Добавляем модуль для CipherInterface
                module {
                    single { appComponent }
                    single { swiftFuncs }
                       },         // Добавляем модуль для IosApplicationComponent
                platformModule,                             // Добавляем платформозависимый модуль
            )
        )
    }
}

