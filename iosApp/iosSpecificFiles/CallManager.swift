import Foundation
import CallKit
import UIKit
import ComposeApp
import AVFoundation // ✅ Добавляем импорт

class CallManager: NSObject {
    let callController = CXCallController()
    let provider: CXProvider

    override init() {
        let configuration = CXProviderConfiguration(localizedName: "My VoIP App")
        configuration.supportsVideo = true
        configuration.maximumCallsPerCallGroup = 1
        configuration.supportedHandleTypes = [.phoneNumber, .generic]

        provider = CXProvider(configuration: configuration)
        super.init() // ✅ Вызываем init родительского класса

        provider.setDelegate(self, queue: nil)
    }

    // 📞 Показываем входящий звонок на экране
    func reportIncomingCall(uuid: UUID, handle: String, hasVideo: Bool, callId: String) {
        Logger.log("📦 push:2 - Начинаем обработку входящего вызова")
        
        // 🔍 Логируем входящие данные
        Logger.log("📞 Входящий вызов UUID: \(uuid)")
        Logger.log("📞 Вызов от: \(handle)")
        Logger.log("📞 callId: \(callId)")
        
        let update = CXCallUpdate()
        
        Logger.log("📦 push:4 - Создан CXCallUpdate")

        update.remoteHandle = CXHandle(type: .generic, value: handle)
        update.hasVideo = hasVideo
        update.localizedCallerName = handle

        Logger.log("📦 push:6 - Готовимся к reportNewIncomingCall")

//        DispatchQueue.main.async {
            Logger.log("📦 push:6 - Вызываем reportNewIncomingCall в CallKit")

            self.provider.reportNewIncomingCall(with: uuid, update: update) { error in
                if let error = error {
                    Logger.log("❌ Ошибка отображения звонка: \(error.localizedDescription)")
                } else {
                    Logger.log("📞 Входящий звонок успешно отображен!")
                }
            }
//        }

        Logger.log("📦 push:6 - Код после DispatchQueue.main.async")

    }


    func endCall(uuid: UUID) {
        let endCallAction = CXEndCallAction(call: uuid)
        let transaction = CXTransaction(action: endCallAction)
        callController.request(transaction) { error in
            if let error = error {
                Logger.log("❌ Ошибка завершения звонка: \(error.localizedDescription)")
            }
        }
    }

    @objc func endAllCalls() {
        print("🔴 Завершаем все звонки")
        let transactions = callController.callObserver.calls

        for call in transactions {
            let endCallAction = CXEndCallAction(call: call.uuid)
            let transaction = CXTransaction(action: endCallAction)

            callController.request(transaction) { error in
                if let error = error {
                    print("❌ Ошибка завершения звонка: \(error.localizedDescription)")
                } else {
                    print("✅ Все звонки завершены!")
                }
            }
        }

        if transactions.isEmpty {
            print("⚠️ Нет активных звонков для завершения")
        }
    }
}

// MARK: - CXProviderDelegate
extension CallManager: CXProviderDelegate {
    func providerDidReset(_ provider: CXProvider) {
        print("Provider reset - Clean up ongoing calls")
    }


    func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        Logger.log("📞 Вызов принят через CallKit!")

        DispatchQueue.main.async {
            if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
               let window = scene.windows.first {
                Logger.log("✅ Открываем главный экран после принятия звонка")
                window.rootViewController = MainKt.MainViewController()
                window.makeKeyAndVisible()
            } else {
                Logger.log("⚠️ Ошибка: не удалось найти window")
            }
        }
        
        action.fulfill()
    }


    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        print("Call ended")

        let callHandler = KoinHelperKt.getCallHandler() // ✅ Вызываем Koin внутри метода
        callHandler.rejectCallIos()
        
        action.fulfill()
    }
}
