package org.videotrade.shopot.multiplatform

import org.videotrade.shopot.cipher.SharedSecretModule
import org.videotrade.shopot.cipher.WolfsslModule

actual fun sharedSecret(publicKey: ByteArray): Array<ByteArray> {
    return SharedSecretModule.sharedSecretC(publicKey)
}

actual fun encupsChachaMessage(message: String, sharedSecret: ByteArray): EncapsulationResultJava {
    return WolfsslModule.encupsChachaMessage(message, sharedSecret)
}

actual fun decupsChachaMessage(
    cipher: ByteArray,
    block: ByteArray,
    authTag: ByteArray,
    sharedSecret: ByteArray
): String? {
    return WolfsslModule.decupsChachaMessage(cipher, block, authTag, sharedSecret)
        ?.let { String(it, charset("UTF-8")) }
}

actual fun getSharedSecret(publicKeyBase64: String): List<String> {
    TODO("Not yet implemented")
}