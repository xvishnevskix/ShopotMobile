package org.videotrade.shopot.multiplatform

import kotlinx.serialization.Serializable
import org.koin.core.module.Module
import org.koin.dsl.module

interface CipherInterface {
    fun getSharedSecretAndCipherText(publicKey: ByteArray): SharedSecretResult?
    fun encupsChachaMessage(message: String, sharedSecret: ByteArray): EncapsulationMessageResult
    fun decupsChachaMessage(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String?
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class CipherWrapper(cipherInterface: CipherInterface? = null) {
    fun getSharedSecretCommon(publicKey: ByteArray): SharedSecretResult?
    fun encupsChachaMessageCommon(
        message: String,
        sharedSecret: ByteArray
    ): EncapsulationMessageResult?
    
    fun decupsChachaMessageCommon(
        cipher: ByteArray,
        block: ByteArray,
        authTag: ByteArray,
        sharedSecret: ByteArray
    ): String?
}


@Serializable
data class SharedSecretResult(val ciphertext: ByteArray, val sharedSecret: ByteArray)

@Serializable
data class EncapsulationMessageResult(
    val cipher: ByteArray,
    val block: ByteArray,
    val authTag: ByteArray
)