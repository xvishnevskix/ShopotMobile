package org.videotrade.shopot.multiplatform

import android.net.Uri
import kotlinx.coroutines.runBlocking
import org.videotrade.shopot.androidSpecificApi.getContextObj
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
    
    
    actual fun encupsChachaFileCommon(
        filePath: String,
        cipherFilePath: String,
        sharedSecret: ByteArray
    ): EncapsulationFileResult? {
        return WolfsslModule.encupsChachaFile(filePath, cipherFilePath, sharedSecret)
    }
    
    actual fun decupsChachaFileCommon(
        cipherFilePath: String,
        jEncryptedFilePath: String,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray,
    ): String? {
        return WolfsslModule.decupsChachaFile(
            cipherFilePath,
            jEncryptedFilePath,
            block,
            authTag,
            sharedSecret
        )
        
        
    }
    
}