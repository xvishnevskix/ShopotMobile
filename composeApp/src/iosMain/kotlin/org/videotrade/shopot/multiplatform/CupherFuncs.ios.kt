package org.videotrade.shopot.multiplatform

actual fun getSharedSecret(publicKeyBase64: String): List<String> {
    TODO("Not yet implemented")
    
}


actual fun sharedSecret(publicKey: ByteArray): Array<ByteArray> {
    TODO("Not yet implemented")
}

actual fun encupsChachaMessage(
    message: String,
    sharedSecret: ByteArray
): EncapsulationMessageResult {
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
actual class CipherWrapper actual constructor(
    private val cipherInterface: CipherInterface
) {
    actual fun getSharedSecretCommon(publicKey: ByteArray): SharedSecretResult? {
        return cipherInterface.getSharedSecretAndCipherText(publicKey)
    }
    
    actual fun encupsChachaMessageCommon(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationMessageResult {
        return cipherInterface.encupsChachaMessage(message, sharedSecret)
    }
    
    actual fun decupsChachaMessageCommon(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String? {
        return cipherInterface.decupsChachaMessage(cipher, block, authTag, sharedSecret)
        
    }
}
