import PushKit
import os.log

class PushKitHandler: NSObject, PKPushRegistryDelegate {
    private let callManager: CallManager
    private var pushRegistry: PKPushRegistry! // ✅ Теперь pushRegistry не исчезает

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


    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        print("📲 Новый VoIP Token: \(voipToken)")

        // ✅ Сохраняем VoIP токен в UserDefaults
        UserDefaults.standard.set(voipToken, forKey: "VoIPToken")
        UserDefaults.standard.synchronize()
    }



    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("🔔 PushKit получил VoIP push!")
        print("📦 Payload: \(payload.dictionaryPayload)")

        os_log("🔔 VoIP push получен! Payload: %@", log: OSLog(subsystem: "com.videotrade.shopot", category: "PushKit"), type: .info, payload.dictionaryPayload.description)

        handleIncomingCall(payload: payload)
        completion()
    }


    
    func handleIncomingCall(payload: PKPushPayload) {
        Logger.log("✅ handleIncomingCall вызван!")

        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        Logger.log("📞 Входящий звонок: \(callerName), Call ID: \(callId)")

        DispatchQueue.main.async {
            self.callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false, callId: callId)
        }
    }
}
