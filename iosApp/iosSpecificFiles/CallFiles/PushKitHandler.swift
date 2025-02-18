import PushKit
import os.log
import AVFoundation // ✅ Добавляем импорт
import UIKit
import CallKit
import PushKit
import ComposeApp

class PushKitHandler: NSObject, PKPushRegistryDelegate, CXProviderDelegate {

    private var pushRegistry: PKPushRegistry!
    private var callProvider: CXProvider!
    let callController = CXCallController()

    override init() {
        super.init()
        registerForPushKit()
        setupCallKit()
    }

    func registerForPushKit() {
        self.pushRegistry = PKPushRegistry(queue: DispatchQueue.main)
        self.pushRegistry.delegate = self
        self.pushRegistry.desiredPushTypes = [.voIP]
        print("✅ PushKit зарегистрирован и подписан на VoIP уведомления!")
    }

    // ✅ Настройка CallKit
    private func setupCallKit() {
        let configuration = CXProviderConfiguration()
        configuration.supportsVideo = true
        configuration.includesCallsInRecents = true
//        configuration.ringtoneSound = "ES_CellRingtone23.mp3"

        callProvider = CXProvider(configuration: configuration)
        callProvider.setDelegate(self, queue: nil)
    }

    // 📲 Получаем VoIP Token
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        Logger.log("📲 Новый VoIP Token: \(voipToken)")
        
        
        LocalStorageKt.addValueInStorage(
            key: "voipToken",
            value: voipToken
        )
    }

    // ✅ Обрабатываем входящий VoIP-звонок
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        Logger.log("🔔 VoIP push получен!")
        Logger.log("📦 Payload: \(payload.dictionaryPayload)")
        
        
        let appState = UIApplication.shared.applicationState
        let callHandler: CallHandler = KoinHelperKt.getCallHandler()


        switch appState {
        case .active:
            callHandler.setIsIncomingCall(isIncomingCall: true)

            Logger.log("📲 Приложение активно (foreground)")
        case .background:
            Logger.log("🌙 Приложение в фоне (background)")
            
            callHandler.setAppIsActive(appIsActive: false)
            
            callHandler.setIsCallBackground(isCallBackground: true)
            

        @unknown default:
            Logger.log("⚠️ Неизвестное состояние приложения")
        }
        


        let uuid = UUID()
        let phone = payload.dictionaryPayload["phone"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: "+\(phone)")
        update.hasVideo = true
        
//        activateAudioSession()

        callProvider.reportNewIncomingCall(with: uuid, update: update) { error in
            if let error = error {
                Logger.log("❌ Ошибка обработки вызова: \(error.localizedDescription)")
            } else {
                Logger.log("✅ Вызов успешно зарегистрирован в CallKit")
            }
        }
        

        
        DispatchQueue.main.async {
                  Task {
                      do {
                          let callHandler = KoinHelperKt.getCallHandler() // ✅ Берем CallHandler из Koin внутри метода
                          let callInfo = try await callHandler.getCallInfo(callId: callId)
                          if let callInfo = callInfo {
                          } else {
                              print("Call info is nil")
                          }
                      } catch {
                          print("Failed to retrieve call info: \(error)")
                      }
                  }
              }


        completion()
    }

    func activateAudioSession() {
        DispatchQueue.main.async {
            let audioSession = AVAudioSession.sharedInstance()
            do {
                // Деактивируем перед изменениями
                try audioSession.setActive(false)

                try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.allowBluetooth, .defaultToSpeaker])
                try audioSession.setActive(true)
                
                Logger.log("🔊 Аудиосессия успешно активирована")
            } catch {
                Logger.log("❌ Ошибка активации аудиосессии: \(error.localizedDescription)")
            }
        }
    }


    // ✅ Реализация CXProviderDelegate

    // CallKit требует обработки сброса состояния
    func providerDidReset(_ provider: CXProvider) {
        Logger.log("🔄 CallKit был сброшен")
    }

    // CallKit требует обработки принятия звонка
    func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        Logger.log("📞 Входящий звонок принят")
//        activateAudioSession()
        
        let callHandler: CallHandler = KoinHelperKt.getCallHandler()
        

        
        let appState = UIApplication.shared.applicationState

        switch appState {
        case .active:
            Logger.log("📲 Приложение активно (foreground)")
            let callHandler = KoinHelperKt.getCallHandler()

            DispatchQueue.main.async {
              callHandler.startWebRTCSession()
            }
        case .background:
            Logger.log("🌙 Приложение в фоне (background)")
            
            let callHandler: CallHandler = KoinHelperKt.getCallHandler()
            
            callHandler.setAppIsActive(appIsActive: true)
        @unknown default:
            Logger.log("⚠️ Неизвестное состояние приложения")
        }
        
        
        action.fulfill()
    }

    // CallKit требует обработки завершения звонка
    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        Logger.log("📞 Звонок завершен")
        let callHandler = KoinHelperKt.getCallHandler() // ✅ Вызываем Koin внутри метода
        
            callHandler.rejectCallIos()
        
        action.fulfill()
    }
    
    @objc func endAllCalls() {
        print("🔴 Завершаем все звонки")

        let activeCalls = callController.callObserver.calls
        if activeCalls.isEmpty {
            print("⚠️ Нет активных звонков для завершения")
            return
        }

        for call in activeCalls {
            let endCallAction = CXEndCallAction(call: call.uuid)
            let transaction = CXTransaction(action: endCallAction)

            callController.request(transaction) { error in
                if let error = error {
                    print("❌ Ошибка завершения звонка: \(error.localizedDescription)")
                } else {
                    print("✅ Звонок \(call.uuid) завершен!")
                }
            }
        }
    }

}
