package org.videotrade.shopot.multiplatform


import WebRTC.RTCAudioSession
import WebRTC.RTCAudioSessionConfiguration
import WebRTC.setConfiguration
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionAllowBluetooth
import platform.AVFAudio.AVAudioSessionCategoryOptionAllowBluetoothA2DP
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionPortOverrideNone
import platform.AVFAudio.AVAudioSessionPortOverrideSpeaker
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLog

actual class CallProvider {
    @OptIn(ExperimentalForeignApi::class)
    actual fun switchToSpeaker(switch: Boolean) {
        val audioSession = AVAudioSession.sharedInstance()
        val categoryError = audioSession.setCategory(AVAudioSessionCategoryPlayAndRecord, null)
        if (categoryError != null) {
            NSLog("Error setting audio session category")
            return
        }
        val overrideError: Boolean = if (switch) {
            audioSession.overrideOutputAudioPort(AVAudioSessionPortOverrideSpeaker, null)
        } else {
            audioSession.overrideOutputAudioPort(AVAudioSessionPortOverrideNone, null)
        }
        if (overrideError != null) {
            NSLog("Error overriding audio port: $overrideError")
            return
        }
        val activationError = audioSession.setActive(true, null)
        if (activationError != null) {
            NSLog("Error activating audio session")
        }
    }
}

actual object CallProviderFactory {
    actual fun create(): CallProvider {
        return CallProvider()
    }
}


actual fun onResumeCallActivity(navigator: Navigator) {
}

actual fun isCallActiveNatific() {
}

actual fun clearNotificationsForChannel(channelId: String) {
}

actual fun closeAppAndCloseCall() {

}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun settingCAudioSession() {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            with(RTCAudioSession.sharedInstance()) {
                val config = RTCAudioSessionConfiguration.webRTCConfiguration()
                config.category = AVAudioSessionCategoryPlayAndRecord.toString()
                config.categoryOptions = AVAudioSessionCategoryOptionAllowBluetooth or AVAudioSessionCategoryOptionAllowBluetoothA2DP
                lockForConfiguration()
                setConfiguration(config, error.ptr)
                error.value?.let {
                    Logger.e { "Error setting WebRTC audio session configuration: ${it.localizedDescription}" }
                }
                unlockForConfiguration()
            }
        }
}