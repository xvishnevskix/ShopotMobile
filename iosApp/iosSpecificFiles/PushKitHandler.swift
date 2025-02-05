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
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        print("📲 Новый VoIP Token: \(voipToken)")

        // Сохраняем токен (для отправки на сервер)
        UserDefaults.standard.set(voipToken, forKey: "VoIPToken")
        UserDefaults.standard.synchronize()
    }

    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("🔔 VoIP push получен!")
        print("📦 Payload: \(payload.dictionaryPayload)")

        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        // ❗️ ВАЖНО: Вызов CallKit ДОЛЖЕН быть сразу, без задержек
        self.callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false, callId: callId)

        completion() // Сообщаем iOS, что push обработан
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
