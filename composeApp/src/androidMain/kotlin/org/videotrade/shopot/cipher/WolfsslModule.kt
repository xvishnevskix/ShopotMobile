package org.videotrade.shopot.cipher

import org.videotrade.shopot.multiplatform.EncapsulationFileResult
import org.videotrade.shopot.multiplatform.EncapsulationMessageResult


object WolfsslModule {
    init {
        System.loadLibrary("wolfsslNative")
    }
    
    @JvmStatic
    external fun encupsChachaMessage(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationMessageResult
    
    
    external fun decupsChachaMessage(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): ByteArray?
    
    
    external fun encupsChachaFile(
        filePath: String,
        cipherFilePath: String,
        sharedSecret: ByteArray
    ): EncapsulationFileResult
    
    
    external fun decupsChachaFile(
        cipherFilePath: String,
        jEncryptedFilePath: String,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray,
    ): String
    
}


