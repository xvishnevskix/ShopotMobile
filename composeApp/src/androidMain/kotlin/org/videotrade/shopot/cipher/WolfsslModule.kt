package org.videotrade.shopot.cipher

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
    
}


