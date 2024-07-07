package org.videotrade.shopot.multiplatform

import org.videotrade.shopot.cipher.SharedSecretModule
import org.videotrade.shopot.cipher.WolfsslModule

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CipherWrapper actual constructor(cipherInterface: CipherInterface?) {
    actual fun getSharedSecretCommon(publicKey: ByteArray): SharedSecretResult? {
        return SharedSecretModule.sharedSecretC(publicKey)
    }
    
    actual fun encupsChachaMessageCommon(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationMessageResult? {
        return WolfsslModule.encupsChachaMessage(message, sharedSecret)
    }
    
    actual fun decupsChachaMessageCommon(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String? {
        return WolfsslModule.decupsChachaMessage(cipher, block, authTag, sharedSecret)
            ?.let { String(it, charset("UTF-8")) }
    }
    
}