package org.videotrade.shopot.multiplatform


import cafe.adriel.voyager.navigator.Navigator
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVAudioSessionPortOverrideNone
import platform.AVFAudio.AVAudioSessionPortOverrideSpeaker
import platform.AVFAudio.setActive
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