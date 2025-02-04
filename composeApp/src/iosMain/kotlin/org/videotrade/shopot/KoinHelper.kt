package org.videotrade.shopot

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.di.getSharedModules
import org.videotrade.shopot.multiplatform.CipherInterface
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.IosApplicationComponent
import org.videotrade.shopot.multiplatform.SwiftFuncsIos
import org.videotrade.shopot.multiplatform.iosCall.CallHandler
import org.videotrade.shopot.multiplatform.iosCall.CallHandler.getKoin
import org.videotrade.shopot.multiplatform.platformModule

internal fun provideEncapsulateChecker(cipherInterface: CipherInterface): Module = module {
    single<CipherWrapper> { CipherWrapper(cipherInterface) }
}

@OptIn(KoinInternalApi::class)
fun doInitKoin(
    cipherInterface: CipherInterface,
    appComponent: IosApplicationComponent,
    swiftFuncs: SwiftFuncsIos,
    additionalModules: List<Module> = listOf(),
    appDeclaration: KoinAppDeclaration = {},
) {
    val allModules = additionalModules + getSharedModules() + listOf(
        provideEncapsulateChecker(cipherInterface),
        module {
            single { appComponent }
            single { swiftFuncs }
        },
        platformModule
    )
    
    println("✅ Загружаемые модули в Koin:")
    allModules.forEach { module ->
        println("🔹 Модуль: $module")
    }
    
    startKoin {
        appDeclaration()
        modules(allModules)
    }
    
    // Проверка зарегистрированных зависимостей
    val koin = getKoin()
    val registeredDefinitions = koin.instanceRegistry.instances.keys
    println("✅ Зарегистрированные зависимости в Koin: $registeredDefinitions")
}



fun getCallHandler(): CallHandler {
    return getKoin().get()
}
