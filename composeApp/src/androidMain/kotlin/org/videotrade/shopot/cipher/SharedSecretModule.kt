package org.videotrade.shopot.cipher

import org.videotrade.shopot.multiplatform.SharedSecretResult

object SharedSecretModule {
    init {
        System.loadLibrary("sharedSecret")
    }
    
    @JvmStatic
    external fun sharedSecretC(publicKey: ByteArray): SharedSecretResult
}

