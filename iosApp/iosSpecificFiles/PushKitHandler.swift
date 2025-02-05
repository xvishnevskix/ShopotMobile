import PushKit
import os.log

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
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("🔔 VoIP push получен в фоне!")
        print("📦 Payload: \(payload.dictionaryPayload)")

        DispatchQueue.main.async {
            let callUUID = UUID()
            let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
            self.callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false, callId: "12345")
        }

        completion()
    }


    // 🔔 Получаем входящий звонок (PushKit)
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("🔔 PushKit получил VoIP push!")
        print("📦 Payload: \(payload.dictionaryPayload)")

        os_log("🔔 VoIP push получен! Payload: %@", log: OSLog(subsystem: "com.videotrade.shopot", category: "PushKit"), type: .info, payload.dictionaryPayload.description)

        handleIncomingCall(payload: payload)
        completion()
    }

    func handleIncomingCall(payload: PKPushPayload) {
        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        DispatchQueue.main.async {
            self.callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false, callId: callId)
        }
    }
}
