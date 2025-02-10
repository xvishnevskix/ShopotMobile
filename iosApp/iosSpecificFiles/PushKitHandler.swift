import PushKit
import os.log
import AVFoundation
import UIKit
import CallKit
import ComposeApp

class PushKitHandler: NSObject, PKPushRegistryDelegate, CXProviderDelegate {
    private let callManager: CallManager
    private var pushRegistry: PKPushRegistry!
    private var callProvider: CXProvider!
    private let callController = CXCallController()
    
    
    init(callManager: CallManager) {
        self.callManager = callManager
        super.init()
        setupCallKit()
        registerForPushKit()
    }

    var backgroundTask: UIBackgroundTaskIdentifier = .invalid

    func beginBackgroundTask() {
        backgroundTask = UIApplication.shared.beginBackgroundTask {
            // Если iOS хочет завершить задачу, завершите её вручную
            UIApplication.shared.endBackgroundTask(self.backgroundTask)
            self.backgroundTask = .invalid
        }
    }


    private func setupCallKit() {
        let configuration = CXProviderConfiguration(localizedName: "My VoIP App")
        configuration.supportsVideo = true
        configuration.includesCallsInRecents = true
        configuration.ringtoneSound = "ES_CellRingtone23.mp3"

        callProvider = CXProvider(configuration: configuration)
        callProvider.setDelegate(self, queue: nil)
    }


    /// ✅ Подписываемся на PushKit
    func registerForPushKit() {
        self.pushRegistry = PKPushRegistry(queue: DispatchQueue.main)
        self.pushRegistry.delegate = self
        self.pushRegistry.desiredPushTypes = [.voIP]
        Logger.log("✅ PushKit зарегистрирован и подписан на VoIP уведомления!")
    }

    // 📲 Получаем VoIP Token
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        Logger.log("📲 Новый VoIP Token: \(voipToken)")
    }
 
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        Logger.log("🔔 VoIP push получен!")
        
        beginBackgroundTask() // ✅ Запускаем фоновую задачу

        let callHandler: CallHandler = KoinHelperKt.getCallHandler()
        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        Logger.log("📞 callerName: \(callerName)")
        Logger.log("📞 callId: \(callId)")

        let callUpdate = CXCallUpdate()
        callUpdate.remoteHandle = CXHandle(type: .generic, value: callerName)
        callUpdate.hasVideo = true

        callProvider.reportNewIncomingCall(with: callUUID, update: callUpdate) { error in
            if let error = error {
                Logger.log("❌ Ошибка CallKit: \(error.localizedDescription)")
            } else {
                Logger.log("📞 Входящий звонок зарегистрирован в CallKit")
                
                // ✅ Включаем аудиосессию
                self.activateAudioSession()
                
                // ✅ Запускаем WebRTC-сессию
                callHandler.startWebRTCSession(callId: callId)
                Logger.log("📞 WebRTC-сессия запущена")

                // ✅ Завершаем фоновую задачу
                UIApplication.shared.endBackgroundTask(self.backgroundTask)
                self.backgroundTask = .invalid
            }
        }

        // ✅ Отложенный вызов completion() (чтобы iOS не сбрасывала звонок)
        DispatchQueue.global(qos: .background).asyncAfter(deadline: .now() + 1) {
            completion()
        }
    }


    /// ✅ Активация аудиосессии перед звонком
    func activateAudioSession() {
        let audioSession = AVAudioSession.sharedInstance()
        do {
            try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.allowBluetooth, .defaultToSpeaker])
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation) // ✅ Добавлен `notifyOthersOnDeactivation`
            Logger.log("🔊 Аудиосессия успешно активирована")
        } catch {
            Logger.log("❌ Ошибка активации аудиосессии: \(error.localizedDescription)")
        }
    }


    // MARK: - CXProviderDelegate (Необходимо для управления звонками)

    /// ✅ Если CallKit был сброшен
    func providerDidReset(_ provider: CXProvider) {
        Logger.log("🔄 Провайдер CallKit был сброшен")
    }
    
    func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        Logger.log("✅ Звонок принят")
        
        // ✅ Активация аудиосессии
        activateAudioSession()
        
        // ✅ Уведомляем CallKit, что действие выполнено
        action.fulfill()
    }


    /// ✅ Обработка завершения звонка
    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        Logger.log("☎️ Звонок завершен")
        action.fulfill()
    }
}
