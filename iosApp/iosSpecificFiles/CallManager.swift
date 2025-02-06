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
        Logger.log("📦 push:2 ")
//
//        DispatchQueue.main.async {
//            Task {
//                do {
//                    Logger.log("📦 Payload:4 ")
//
//                    let callHandler = KoinHelperKt.getCallHandler() // ✅ Берем CallHandler из Koin внутри метода
//                    Logger.log("📦 Payload:15 ")
//
//                    let callInfo = try await callHandler.getCallInfo(callId: callId)
//                    Logger.log("📦 Payload:16 ")
//
//                    if let callInfo = callInfo {
//                        Logger.log("Call info retrieved successfully: \(callInfo)")
//                    } else {
//                        Logger.log("Call info is nil")
//                    }
//                } catch {
//                    Logger.log("Failed to retrieve call info: \(error)")
//                }
//            }
//        }

        Logger.log("📦 push:3 ")

        let update = CXCallUpdate()
        Logger.log("📦 push:4 ")

        update.remoteHandle = CXHandle(type: .generic, value: handle)
        
        Logger.log("📦 push:5 ")

        update.hasVideo = hasVideo
        update.localizedCallerName = handle
        Logger.log("📦 push:6 ")

        provider.reportNewIncomingCall(with: uuid, update: update) { error in
            if let error = error {
                Logger.log("❌ Ошибка отображения звонка: \(error.localizedDescription)")
            } else {
                Logger.log("📞 Входящий звонок отображен!")
            }
        }
        
        Logger.log("📦 push:7 ")

        
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

        // 2️⃣ Гарантируем, что CallKit знает о принятии вызова
        action.fulfill()

//        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { // ⏳ Даем время загрузиться приложению
//            if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
//               let window = scene.windows.first {
//                print("✅ Меняем rootViewController на MainViewController")
//                window.rootViewController = MainKt.MainViewController()
//                window.makeKeyAndVisible()
//            } else {
//                print("⚠️ Не удалось получить windowScene")
//            }
//
//            // 4️⃣ Запускаем WebRTC после небольшой задержки
//            Task {
//                do {
//                    let callHandler = KoinHelperKt.getCallHandler()
//                    print("⏳ Запуск WebRTC-сессии...")
//                    try await callHandler.startWebRTCSession(callId: "123") // 👈 Убедись, что callId правильный
//                    print("✅ WebRTC-сессия успешно запущена")
//                } catch {
//                    print("❌ Ошибка при запуске WebRTC: \(error.localizedDescription)")
//                }
//            }
//        }
    }



    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        print("Call ended")

        let callHandler = KoinHelperKt.getCallHandler() // ✅ Вызываем Koin внутри метода
        callHandler.rejectCallIos()
        
        action.fulfill()
    }
}
