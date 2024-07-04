package org.videotrade.shopot.cipher

import org.videotrade.shopot.multiplatform.EncapsulationResultJava


object WolfsslModule {
    init {
        System.loadLibrary("wolfsslNative")
    }
    
    @JvmStatic
    external fun encupsChachaMessage(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationResultJava
    
    
    external fun decupsChachaMessage(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): ByteArray?
    
}


