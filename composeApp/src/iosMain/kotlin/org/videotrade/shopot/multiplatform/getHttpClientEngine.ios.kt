package org.videotrade.shopot.multiplatform

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication
import platform.UIKit.UITapGestureRecognizer
import platform.UIKit.endEditing
import platform.darwin.NSObject


actual fun getHttpClientEngine(): HttpClientEngineFactory<HttpClientEngineConfig> {
    return Darwin
}


class KeyboardDismissHandler : NSObject() {
    @ObjCAction
    fun dismissKeyboard() {
        UIApplication.sharedApplication.keyWindow?.endEditing(true)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun Modifier.hideKeyboardOnTap(): Modifier = this.then(
    Modifier.pointerInput(Unit) {
        // Пустой обработчик указателей для инициализации
    }.also {
        // Создаем экземпляр обработчика
        val handler = KeyboardDismissHandler()
        // Создаем распознаватель касаний
        val tapGesture = UITapGestureRecognizer(
            target = handler,
            action = NSSelectorFromString("dismissKeyboard")
        )
        // Добавляем распознаватель жестов к окну приложения
        UIApplication.sharedApplication.keyWindow?.addGestureRecognizer(tapGesture)
    }
)
