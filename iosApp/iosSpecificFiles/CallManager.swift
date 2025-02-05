import Foundation
import CallKit

class CallManager: NSObject {
    let callController = CXCallController()
    let provider: CXProvider

    override init() {
        let configuration = CXProviderConfiguration(localizedName: "My VoIP App")
        configuration.supportsVideo = true
        configuration.maximumCallsPerCallGroup = 1
        configuration.supportedHandleTypes = [.phoneNumber, .generic]

        provider = CXProvider(configuration: configuration)
        super.init()
        provider.setDelegate(self, queue: nil)
    }
    
    // 📞 Показываем входящий звонок на экране
    func reportIncomingCall(uuid: UUID, handle: String, hasVideo: Bool, callId: String) {
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
}

// MARK: - CXProviderDelegate
extension CallManager: CXProviderDelegate {
    func providerDidReset(_ provider: CXProvider) {
        print("🔄 CallKit был сброшен (все звонки завершены)")
    }

    func provider(_ provider: CXProvider, perform action: CXAnswerCallAction) {
        print("✅ Звонок принят")
        action.fulfill()
    }

    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        print("🔴 Звонок завершен")
        action.fulfill()
    }
}
