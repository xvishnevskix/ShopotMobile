import Foundation
import ComposeApp
import AVFoundation // ✅ Добавляем импорт

class IosSwiftFuncsHelper: SwiftFuncsHelper {
    func doInitCallKit(phone: String, callId: String) {
        print("📞 Инициализируем CallKit с номером: \(phone) и callId: \(callId)")
        pushKitHandler.initializeCall(phone: phone, callId: callId)    }
    
    private let pushKitHandler: PushKitHandler

    init(pushKitHandler: PushKitHandler) {
        self.pushKitHandler = pushKitHandler
    }

    func endCall() {
        print("🔴 Завершаем звонок из IosSwiftFuncsHelper")
        pushKitHandler.endAllCalls()
    }

    func stopAVAudioSession() {
        print("🔇 Останавливаем AVAudioSession")
        do {
            let audioSession = AVAudioSession.sharedInstance()
            try audioSession.setActive(false, options: .notifyOthersOnDeactivation)
            print("🛑 AVAudioSession деактивирован")
        } catch {
            print("❌ Ошибка при деактивации AVAudioSession: \(error.localizedDescription)")
        }
    }

    func setAVAudioSession() {
        print("🎤 Активируем AVAudioSession")
//        do {
//            let audioSession = AVAudioSession.sharedInstance()
//            try audioSession.setCategory(.playAndRecord, mode: .voiceChat, options: [.allowBluetooth, .defaultToSpeaker])
//            try audioSession.setActive(true)
//            print("✅ AVAudioSession успешно активирован")
//        } catch {
//            print("❌ Ошибка при активации AVAudioSession: \(error.localizedDescription)")
//        }
    }
}



class Logger {
    static let logFileName = "app_logs.txt"

    static func log(_ message: String) {
        let logMessage = "\(Date()) - \(message)\n"
        print(logMessage) // Для отладки в Xcode

        let fileURL = getLogFilePath()
        if let data = logMessage.data(using: .utf8) {
            if FileManager.default.fileExists(atPath: fileURL.path) {
                if let fileHandle = try? FileHandle(forWritingTo: fileURL) {
                    fileHandle.seekToEndOfFile()
                    fileHandle.write(data)
                    fileHandle.closeFile()
                }
            } else {
                try? data.write(to: fileURL, options: .atomic)
            }
        }
    }

    static func getLogFilePath() -> URL {
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        return paths[0].appendingPathComponent(logFileName)
    }

    static func readLogs() -> String {
        let fileURL = getLogFilePath()
        if let logData = try? String(contentsOf: fileURL, encoding: .utf8) {
            print("📄 Логи приложения:\n\(logData)")
            return logData
        }
        return "No logs found"
    }
}

