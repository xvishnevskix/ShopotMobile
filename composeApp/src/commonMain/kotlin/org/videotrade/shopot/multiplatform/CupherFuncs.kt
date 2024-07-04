package org.videotrade.shopot.multiplatform

import org.koin.core.module.Module
import org.koin.dsl.module

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

// Общий модуль
data class EncapsulationResult(val ciphertext: ByteArray, val sharedSecret: ByteArray)


interface EncryptionWrapperChecker {
    fun encapsulate(publicKey: String): String?
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class EncapsulateChecker(checker: EncryptionWrapperChecker) {
    fun encapsulateAvailable(publicKey: ByteArray): String?
}


interface ConnectionChecker {
    fun isConnectedToInternet(): Boolean
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class InternetConnectionChecker(checker: ConnectionChecker) {
    fun isInternetAvailable(): Boolean
}

