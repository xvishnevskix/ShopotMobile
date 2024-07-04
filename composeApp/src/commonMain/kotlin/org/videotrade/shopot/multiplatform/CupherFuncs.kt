package org.videotrade.shopot.multiplatform

expect fun sharedSecret(publicKey: ByteArray): Array<ByteArray>

expect fun encupsChachaMessage(message: String, sharedSecret: ByteArray): EncapsulationResultJava
expect fun decupsChachaMessage(
    cipher: ByteArray,
    block: ByteArray,
    authTag: ByteArray,
    sharedSecret: ByteArray
): String?


// commonMain
expect fun getSharedSecret(publicKeyBase64: String): List<String>


data class EncapsulationResultJava(
    val cipher: ByteArray,
    val block: ByteArray,
    val authTag: ByteArray
)
