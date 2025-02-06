import PushKit
import os.log
import AVFoundation // ✅ Добавляем импорт
import UIKit

class PushKitHandler: NSObject, PKPushRegistryDelegate {
    private let callManager: CallManager
    private var pushRegistry: PKPushRegistry!

    init(callManager: CallManager) {
        self.callManager = callManager
        super.init()
        registerForPushKit()
    }

    func registerForPushKit() {
        self.pushRegistry = PKPushRegistry(queue: DispatchQueue.main)
        self.pushRegistry.delegate = self
        self.pushRegistry.desiredPushTypes = [.voIP]
        print("✅ PushKit зарегистрирован и подписан на VoIP уведомления!")
    }

    // 📲 Получаем VoIP Token
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        print("📲 Новый VoIP Token: \(voipToken)")

//        // Сохраняем токен (для отправки на сервер)
//        UserDefaults.standard.set(voipToken, forKey: "VoIPToken")
//        UserDefaults.standard.synchronize()
    }

 
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        Logger.log("🔔 VoIP push получен!")
        Logger.log("📦 Payload: \(payload.dictionaryPayload)")

        activateAudioSession()

        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        Logger.log("📦 push:1 - Разбираем payload")
        Logger.log("📞 callerName: \(callerName)")
        Logger.log("📞 callId: \(callId)")

        // 🔹 Принудительно держим приложение живым
        var backgroundTask: UIBackgroundTaskIdentifier = .invalid
        backgroundTask = UIApplication.shared.beginBackgroundTask {
            Logger.log("⚠️ Background task expired")
            UIApplication.shared.endBackgroundTask(backgroundTask)
        }

        Logger.log("📦 push:2 - Запускаем обработку звонка")

        // ❗️ ВАЖНО: Вызов CallKit ДОЛЖЕН быть сразу, без задержек
        self.callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false, callId: callId)

        Logger.log("📦 push:end - Завершение pushRegistry")
        
        // Завершаем background task, если он активен
        if backgroundTask != .invalid {
            UIApplication.shared.endBackgroundTask(backgroundTask)
        }

        completion() // Сообщаем iOS, что push обработан
    }

    func activateAudioSession() {
DispatchQueue.main.async {
    let audioSession = AVAudioSession.sharedInstance()
    do {
        try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.allowBluetooth, .defaultToSpeaker])
        try audioSession.setActive(true)
        print("🔊 Аудиосессия успешно активирована")
    } catch {
        print("❌ Ошибка активации аудиосессии: \(error.localizedDescription)")
    }
}

       }
}
