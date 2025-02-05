import Foundation
import CallKit
import ComposeApp

class CallManager: NSObject {
    let callController = CXCallController()
    let provider: CXProvider

    
    override  init() {
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
        DispatchQueue.main.async {
            Task {
                do {
                    let callHandler = KoinHelperKt.getCallHandler() // ✅ Берем CallHandler из Koin внутри метода
                    let callInfo = try await callHandler.getCallInfo(callId: callId)
                    if let callInfo = callInfo {
                        print("Call info retrieved successfully: \(callInfo)")
                    } else {
                        print("Call info is nil")
                    }
                } catch {
                    print("Failed to retrieve call info: \(error)")
                }
            }
        }

        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: handle)
        update.hasVideo = hasVideo
        update.localizedCallerName = handle

        provider.reportNewIncomingCall(with: uuid, update: update) { error in
            if let error = error {
                print("❌ Ошибка отображения звонка: \(error.localizedDescription)")
            } else {
                print("📞 Входящий звонок отображен!")
            }
        }
    }

    func endCall(uuid: UUID) {
        let endCallAction = CXEndCallAction(call: uuid)
        let transaction = CXTransaction(action: endCallAction)
        callController.request(transaction) { error in
            if let error = error {
                print("❌ Ошибка завершения звонка: \(error.localizedDescription)")
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
        print("Call answered")
        
        // ✅ Получаем CallHandler через Koin внутри метода
        let callHandler = KoinHelperKt.getCallHandler()

        DispatchQueue.main.async {
          callHandler.startWebRTCSession(callId: "1")
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
