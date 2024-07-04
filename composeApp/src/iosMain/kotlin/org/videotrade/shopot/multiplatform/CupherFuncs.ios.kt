package org.videotrade.shopot.multiplatform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.darwin.NSObject
import kotlin.experimental.ExperimentalObjCName
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.Foundation.*

actual fun getSharedSecret(publicKeyBase64: String): List<String> {
    // Creating a suspend function to handle the async work
    suspend fun getSharedSecretInternal(publicKeyBase64: String): List<String> =
        withContext(Dispatchers.Main) {
            SharedSecretWrapper.getSharedSecret(publicKeyBase64)
        }
    
    // Using runBlocking to call the suspend function synchronously
    return runBlocking {
        getSharedSecretInternal(publicKeyBase64)
    }
}

@OptIn(ExperimentalObjCName::class)
@ObjCName(name = "SharedSecretWrapper", swiftName = "SharedSecretWrapper")
class SharedSecretWrapper : NSObject() {
    companion object {
        @OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
        fun getSharedSecret(publicKeyBase64: String): List<String> {
            val publicKeyData = NSData.create(base64EncodedString = publicKeyBase64, options = 0u)
            val result = SharedSecretModule.encapsulate_with_public_key(publicKeyData?.bytes)
            val ciphertextData = NSData.dataWithBytes(
                result.ciphertext,
                SharedSecretModule.OQS_KEM_kyber_768_length_ciphertext.toULong()
            )
            val sharedSecretData = NSData.dataWithBytes(
                result.shared_secret,
                SharedSecretModule.OQS_KEM_kyber_768_length_shared_secret.toULong()
            )
            
            val ciphertextBase64 = ciphertextData.base64EncodedStringWithOptions(0u)
            val sharedSecretBase64 = sharedSecretData.base64EncodedStringWithOptions(0u)
            
            return listOf(ciphertextBase64, sharedSecretBase64)
        }
    }
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

