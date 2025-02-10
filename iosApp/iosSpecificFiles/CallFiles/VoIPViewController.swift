import UIKit
import CallKit
import PushKit

class VoIPViewController: UIViewController, CXProviderDelegate, PKPushRegistryDelegate {
    override func viewDidLoad() {
        let callRegistry = PKPushRegistry(queue: nil)
        callRegistry.delegate = self
        // Register to receive push notifications
        callRegistry.desiredPushTypes = [PKPushType.voIP]
    }

    // Call this function when the app receives push notifications
    func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        
        // Create an object to handle call configurations and settings
        let callConfigObject = CXProviderConfiguration()
        // Enable video calls
        callConfigObject.supportsVideo = true;
        // Show missed, received and sent calls in the phone app's Recents category
        callConfigObject.includesCallsInRecents = true;
        // Set a custom ring tone for incoming calls
        callConfigObject.ringtoneSound = "ES_CellRingtone23.mp3"
        
        // Create an object to give update about call-related events
        let callReport = CXCallUpdate()
        // Display the name of the caller
        callReport.remoteHandle = CXHandle(type: .generic, value: "Amos Gyamfi")
        // Enable video call
        callReport.hasVideo = true
        
        // Create an object to give update about incoming calls
        let callProvider = CXProvider(configuration: callConfigObject)
        callProvider.reportNewIncomingCall(with: UUID(), update: callReport, completion: { error in })
        callProvider.setDelegate(self, queue: nil)
    }
    
    // Call this function when the app receives push credentials
    func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        // Display the iOS device token in the Xcode console
        print(pushCredentials.token.map { String(format: "%02.2hhx", $0) }.joined())
    }
    
    func providerDidReset(_ callProvider: CXProvider) {
        //
    }
}
