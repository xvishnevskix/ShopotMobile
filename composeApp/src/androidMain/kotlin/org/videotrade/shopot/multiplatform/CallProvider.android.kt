package org.videotrade.shopot.multiplatform

import android.content.Context
import android.media.AudioManager

actual class CallProvider(private val context: Context) {
    actual fun switchToSpeaker(switch: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        
        // Устанавливаем режим связи
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        
        // Переключаем динамик
        audioManager.isSpeakerphoneOn = switch
    }
}

actual object CallProviderFactory {
    private lateinit var applicationContext: Context
    
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }
    
    actual fun create(): CallProvider {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("CallProviderFactory is not initialized")
        }
        return CallProvider(applicationContext)
    }
}
