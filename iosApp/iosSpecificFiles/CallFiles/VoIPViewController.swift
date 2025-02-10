import UIKit
import CallKit
import PushKit
import AVFoundation

class VoIPViewController: UIViewController, CXProviderDelegate, PKPushRegistryDelegate {
    var pushRegistry: PKPushRegistry!
    var callProvider: CXProvider!
    var callController: CXCallController!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupVoIP()
    }
    
    private func setupVoIP() {
        pushRegistry = PKPushRegistry(queue: DispatchQueue.main)
        pushRegistry.delegate = self
        pushRegistry.desiredPushTypes = [.voIP]

        let config = CXProviderConfiguration(localizedName: "MyApp")
        config.supportsVideo = true
        config.includesCallsInRecents = true
        config.ringtoneSound = "ES_CellRingtone23.mp3"

        callProvider = CXProvider(configuration: config)
        callProvider.setDelegate(self, queue: nil)
        callController = CXCallController()
    }

    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        guard type == .voIP else { return }
        
        let uuid = UUID()
        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: "Caller Name")
        update.hasVideo = true

        callProvider.reportNewIncomingCall(with: uuid, update: update) { error in
            if let error = error {
                print("Ошибка обработки входящего вызова: \(error.localizedDescription)")
                return
            }
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
                let transaction = CXTransaction(action: CXAnswerCallAction(call: uuid))
                self.callController.request(transaction) { error in
                    if let error = error {
                        print("Ошибка обработки вызова: \(error.localizedDescription)")
                    }
                }
            }
        }
    }

    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        if type == .voIP {
            let token = pushCredentials.token.map { String(format: "%02.2hhx", $0) }.joined()
            print("VoIP токен: \(token)")
        }
    }

    func provider(_ provider: CXProvider, didActivate audioSession: AVAudioSession) {
        do {
            try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.allowBluetooth, .allowBluetoothA2DP])
            try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
        } catch {
            print("Ошибка активации аудиосессии: \(error.localizedDescription)")
        }
    }

    func providerDidReset(_ provider: CXProvider) {
        print("CallKit сброшен")
    }
}
