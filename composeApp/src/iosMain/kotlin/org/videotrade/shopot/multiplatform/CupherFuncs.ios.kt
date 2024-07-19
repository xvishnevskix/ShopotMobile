package org.videotrade.shopot.multiplatform

import platform.Foundation.NSURL

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class CipherWrapper actual constructor(
    private val cipherInterface: CipherInterface?
) {
    actual fun getSharedSecretCommon(publicKey: ByteArray): SharedSecretResult? {
        return cipherInterface?.getSharedSecretAndCipherText(publicKey)
    }
    
    actual fun encupsChachaMessageCommon(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationMessageResult? {
        return cipherInterface?.encupsChachaMessage(message, sharedSecret)
    }
    
    actual fun decupsChachaMessageCommon(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String? {
        return cipherInterface?.decupsChachaMessage(cipher, block, authTag, sharedSecret)
        
    }
    
    actual fun encupsChachaFileCommon(
        filePath: String,
        cipherFilePath: String,
        sharedSecret: ByteArray
    ): EncapsulationFileResult? {
        val file = NSURL.fileURLWithPath(filePath)
        return cipherInterface?.encupsChachaFile(file.path!!, cipherFilePath, sharedSecret)
    }
    
    actual fun decupsChachaFileCommon(
        cipherFilePath: String,
        jEncryptedFilePath: String,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String? {
        return cipherInterface?.decupsChachaFile(
            cipherFilePath,
            jEncryptedFilePath,
            block,
            authTag,
            sharedSecret
        )
        
    }
}
