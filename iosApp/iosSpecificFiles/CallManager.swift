import Foundation
import CallKit
import ComposeApp

class CallManager: NSObject {
    let callController = CXCallController()
    let provider: CXProvider
    private let callHandler: CallHandler

    init(callHandler: CallHandler) {
        self.callHandler = callHandler

        let configuration: CXProviderConfiguration
        if #available(iOS 14.0, *) {
            configuration = CXProviderConfiguration(localizedName: Bundle.main.object(forInfoDictionaryKey: "CFBundleDisplayName") as? String ?? "YourAppName")
        } else {
            configuration = CXProviderConfiguration(localizedName: "YourAppName")
        }

        configuration.supportsVideo = true // Если поддерживаете видеозвонки
        configuration.maximumCallsPerCallGroup = 1
        configuration.supportedHandleTypes = [.phoneNumber, .generic]

        provider = CXProvider(configuration: configuration)
        super.init()
        provider.setDelegate(self, queue: nil)
    }
    
    @MainActor
    func reportIncomingCall(uuid: UUID, handle: String, hasVideo: Bool = false, callId: String) {
        Task {
            do {
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

        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: handle)
        update.hasVideo = hasVideo
        update.localizedCallerName = handle

        provider.reportNewIncomingCall(with: uuid, update: update) { error in
            if let error = error {
                print("Failed to report incoming call: \(error.localizedDescription)")
            } else {
                print("Incoming call reported successfully.")
            }
        }
    }

    func endCall(uuid: UUID) {
        let endCallAction = CXEndCallAction(call: uuid)
        let transaction = CXTransaction(action: endCallAction)
        callController.request(transaction) { error in
            if let error = error {
                print("Failed to end call: \(error.localizedDescription)")
            }
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

        DispatchQueue.main.async {
            self.callHandler.startWebRTCSession(callId: "1")
        }
        action.fulfill()
    }

    func provider(_ provider: CXProvider, perform action: CXEndCallAction) {
        print("Call ended")
        
        self.callHandler.rejectCallIos()

        action.fulfill()
    }
}
