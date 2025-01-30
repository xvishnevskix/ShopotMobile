import PushKit

class PushKitHandler: NSObject, PKPushRegistryDelegate {
    private let callManager: CallManager

    // Инициализатор, принимающий callManager
    init(callManager: CallManager) {
        self.callManager = callManager
    }

    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        let voipToken = pushCredentials.token.map { String(format: "%02x", $0) }.joined()
        print("PushKit VoIP Token: \(voipToken)")
        // Здесь можно сохранить токен на сервере
    }

    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        print("Received VoIP Push: \(payload.dictionaryPayload)")
        handleIncomingCall(payload: payload)
        completion()
    }
    
    func handleIncomingCall(payload: PKPushPayload) {
        let callUUID = UUID()
        let callerName = payload.dictionaryPayload["callerName"] as? String ?? "Unknown Caller"
        let callId = payload.dictionaryPayload["callId"] as? String ?? "0"

        
        // http запрос на получение данных
        Task { @MainActor in
            callManager.reportIncomingCall(uuid: callUUID, handle: callerName, hasVideo: false ,callId: callId)
        }
    }
}
