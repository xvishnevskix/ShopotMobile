package org.videotrade.shopot.api

import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json
import org.videotrade.shopot.multiplatform.CipherWrapper
import org.videotrade.shopot.multiplatform.EncapsulationMessageResult

fun decupsMessage(
    contentCipher: String,
    cipherWrapper: CipherWrapper
): String? {
    try {
        val sharedSecret = getValueInStorage("sharedSecret")
        
        val contentDecode: EncapsulationMessageResult =
            Json.decodeFromString(contentCipher)
        
        if (sharedSecret !== null) {
            
            println("sharedSecret $sharedSecret")
            
            val cipherValue = cipherWrapper.decupsChachaMessageCommon(
                contentDecode.cipher,
                contentDecode.block,
                contentDecode.authTag,
                sharedSecret.toByteArray()
            )
            println("cipherValue $cipherValue")
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