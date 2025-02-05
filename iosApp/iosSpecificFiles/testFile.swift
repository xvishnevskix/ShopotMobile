import Foundation
import ComposeApp

class IosSwiftFuncsHelper : SwiftFuncsHelper {
    private let callManager: CallManager

    init(callManager: CallManager) {
        self.callManager = callManager
    }

    func testFunc() {
        print("ADDADAD")
    }

    @objc func endCall() {
        print("🔴 Завершаем звонок из IosSwiftFuncsHelper")

        callManager.endAllCalls() // ✅ Вызываем метод у переданного объекта CallManager
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

