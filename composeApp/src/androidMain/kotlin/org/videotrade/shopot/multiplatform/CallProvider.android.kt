package org.videotrade.shopot.multiplatform

import android.content.Context
import android.media.AudioManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cafe.adriel.voyager.navigator.Navigator
import org.koin.mp.KoinPlatform
import org.videotrade.shopot.androidSpecificApi.getContextObj
import org.videotrade.shopot.presentation.screens.call.CallViewModel

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

actual fun onResumeCallActivity(navigator: Navigator) {
    val activity = getContextObj.getActivity() as ComponentActivity
    
    // Регистрируем наблюдателя за жизненным циклом
    activity.lifecycle.addObserver(MyLifecycleObserver(navigator))
}


class MyLifecycleObserver(private val navigator: Navigator) : DefaultLifecycleObserver {
    
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        println("onResume был вызван")
        val callViewModel: CallViewModel = KoinPlatform.getKoin().get()
        
        if(callViewModel.replaceInCall.value) {
            callViewModel.callScreenInfo.value?.let { navigator.push(it) }        // Ваш код, который нужно выполнить при onResume
        }
        
       
    }
}

actual fun isCallActiveNatific() {

}