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
): EncapsulationResultJava {
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

