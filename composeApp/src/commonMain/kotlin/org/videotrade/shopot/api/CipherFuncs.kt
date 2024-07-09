package org.videotrade.shopot.api

import io.ktor.utils.io.core.toByteArray
import okio.ByteString.Companion.decodeBase64
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationMessageResult

fun decupsMessage(
    cipher: String,
    block: String,
    authTag: String,
    cipherWrapper: CipherWrapper
): String? {
    try {
        val sharedSecret = getValueInStorage("sharedSecret")
        
        val cipherBytes =
            cipher.decodeBase64()?.toByteArray()
        val blockBytes =
            block.decodeBase64()?.toByteArray()
        val authTagBytes =
            authTag.decodeBase64()?.toByteArray()
        
        
        if (cipherBytes !== null && blockBytes !== null && authTagBytes !== null && sharedSecret !== null) {
            val cipherValue = cipherWrapper.decupsChachaMessageCommon(
                cipherBytes,
                blockBytes,
                authTagBytes,
                sharedSecret.toByteArray()
            )
            return cipherValue
        }
        
    } catch (e: Exception) {
        return null
        
    }
    return null
    
}


fun encupsMessage(text: String, cipherWrapper: CipherWrapper): EncapsulationMessageResult? {
    try {
        val sharedSecret = getValueInStorage("sharedSecret")
        
        
        
        if (sharedSecret !== null) {
            val cipherValue = cipherWrapper.encupsChachaMessageCommon(
                text,
                sharedSecret.toByteArray()
            )
            return cipherValue
        }
        
    } catch (e: Exception) {
        return null
    }
    return null
    
}