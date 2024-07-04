package org.videotrade.shopot.multiplatform

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64

actual fun getSharedSecret(publicKeyBase64: String): List<String> {
    TODO("Not yet implemented")
    
}


actual fun sharedSecret(publicKey: ByteArray): Array<ByteArray> {
    TODO("Not yet implemented")
}

actual fun encupsChachaMessage(
    message: String,
    sharedSecret: ByteArray
): EncapsulationResultJava {
    TODO("Not yet implemented")
}

actual fun decupsChachaMessage(
    cipher: ByteArray,
    block: ByteArray,
    authTag: ByteArray,
    sharedSecret: ByteArray
): String? {
    TODO("Not yet implemented")
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EncapsulateChecker actual constructor(
    private val checker: EncryptionWrapperChecker
) {
    @Serializable
    private data class ByteArrayWrapper(val bytes: ByteArray)
    
    actual fun encapsulateAvailable(publicKey: ByteArray): String? {
        // Преобразуем ByteArray в Base64 строку
        val publicKeyBase64 = Json.encodeToString(ByteArrayWrapper(publicKey))
        // Вызываем метод checker с Base64 строкой
        return checker.encapsulate(publicKeyBase64)
    }
}
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class InternetConnectionChecker actual constructor(
    private val checker: ConnectionChecker
) {
    actual fun isInternetAvailable(): Boolean = checker.isConnectedToInternet()
}

