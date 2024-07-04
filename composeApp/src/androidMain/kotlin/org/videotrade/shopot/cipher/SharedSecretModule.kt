package org.videotrade.shopot.cipher

object SharedSecretModule {
    init {
        System.loadLibrary("sharedSecret")
    }
    
    @JvmStatic
    external fun sharedSecretC(publicKey: ByteArray): Array<ByteArray>
}


fun sharedSecret(publicKey: ByteArray): ByteArray {
    val result = SharedSecretModule.sharedSecretC(publicKey)
    return result[1] // Предположим, что второй элемент массива - это ваш общий секрет (shared secret)
}