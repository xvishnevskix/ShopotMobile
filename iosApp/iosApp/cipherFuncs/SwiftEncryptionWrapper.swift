import Foundation
import ComposeApp
import UIKit

@objcMembers
class EncryptionWrapperIOS: NSObject {
    func getSharedSecretAndCipherText(_ publicKey: Data) -> ComposeApp.SharedSecretResult? {
        let publicKeyBytes = [UInt8](publicKey)
        var encapsulationResult = encapsulate_with_public_key(UnsafeMutablePointer(mutating: publicKeyBytes))
        
        guard let ciphertext = encapsulationResult.ciphertext, let sharedSecret = encapsulationResult.shared_secret else {
            return nil
        }
        
        let ciphertextData = Data(bytes: ciphertext, count: Int(OQS_KEM_kyber_768_length_ciphertext))
        let sharedSecretData = Data(bytes: sharedSecret, count: Int(OQS_KEM_kyber_768_length_shared_secret))
        
        free(encapsulationResult.ciphertext)
        free(encapsulationResult.shared_secret)
        
        let ciphertextKotlinByteArray = KotlinByteArray(size: Int32(ciphertextData.count))
        for (index, byte) in ciphertextData.enumerated() {
            ciphertextKotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        
        let sharedSecretKotlinByteArray = KotlinByteArray(size: Int32(sharedSecretData.count))
        for (index, byte) in sharedSecretData.enumerated() {
            sharedSecretKotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        
        return ComposeApp.SharedSecretResult(ciphertext: ciphertextKotlinByteArray, sharedSecret: sharedSecretKotlinByteArray)
    }
    
    func encupsChachaMessage(_ message: String, sharedSecret: Data) -> ComposeApp.EncapsulationMessageResult {
        let messageBytes = [UInt8](message.utf8)
        let sharedSecretBytes = [UInt8](sharedSecret)
        
        var result = encapsulate_with_chacha(UnsafeMutablePointer(mutating: messageBytes), UnsafeMutablePointer(mutating: sharedSecretBytes))
        
        let cipherData = Data(bytes: result.cipher, count: Int(result.length))
        let blockData = Data(bytes: result.block, count: 12)
        let authTagData = Data(bytes: result.authTag, count: 16)
        
        let cipherKotlinByteArray = KotlinByteArray(size: Int32(cipherData.count))
        for (index, byte) in cipherData.enumerated() {
            cipherKotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        
        let blockKotlinByteArray = KotlinByteArray(size: Int32(blockData.count))
        for (index, byte) in blockData.enumerated() {
            blockKotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        
        let authTagKotlinByteArray = KotlinByteArray(size: Int32(authTagData.count))
        for (index, byte) in authTagData.enumerated() {
            authTagKotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        
        return ComposeApp.EncapsulationMessageResult(cipher: cipherKotlinByteArray, block: blockKotlinByteArray, authTag: authTagKotlinByteArray)
    }
    
    func decupsChachaMessage(_ cipher: Data, block: Data, authTag: Data, sharedSecret: Data) -> String? {
        guard let decryptedData = WolfsslModule.decrypt(withCipher: cipher, block: block, authTagData: authTag, sharedSecret: sharedSecret) else {
            return nil
        }
        return String(data: decryptedData, encoding: .utf8)
    }

   
    func encupsChachaFile(_ filePath: String, _ cipherFilePath: String, sharedSecret: Data) -> ComposeApp.EncapsulationFileResult? {
        // Преобразование строки пути в URL
        let srcURL = URL(fileURLWithPath: filePath)
        let destURL = URL(fileURLWithPath: cipherFilePath)
        
        let srcPath = srcURL.path
        let destPath = destURL.path

        print("Source file path: \(srcPath)")
        print("Destination file path: \(destPath)")
        
        let fileManager = FileManager.default

        // Проверка существования и доступности файла
        guard fileManager.fileExists(atPath: srcPath) else {
            print("Source file does not exist: \(srcPath)")
            return nil
        }

        do {
            // Убедитесь, что файл загружен локально, если он из iCloud
            let resourceValues = try srcURL.resourceValues(forKeys: [.ubiquitousItemDownloadingStatusKey, .ubiquitousItemIsDownloadingKey])
            
            if let isDownloading = resourceValues.ubiquitousItemIsDownloading, isDownloading {
                print("File is currently downloading: \(srcPath)")
                return nil
            }

            if let downloadingStatus = resourceValues.ubiquitousItemDownloadingStatus, downloadingStatus != .current {
                try fileManager.startDownloadingUbiquitousItem(at: srcURL)
            }

            // Проверка, что файл действительно загружен
            let checkResourceValues = try srcURL.resourceValues(forKeys: [.ubiquitousItemDownloadingStatusKey])
            if let downloadingStatus = checkResourceValues.ubiquitousItemDownloadingStatus, downloadingStatus != .current {
                print("File is not fully downloaded yet: \(srcPath)")
                return nil
            }
        } catch {
            print("Error accessing file: \(error)")
            return nil
        }

        let sharedSecretNSData = sharedSecret as NSData
        
        // Вызов функции из Objective-C
        guard let resultPointer = WolfsslModule.encryptFile(srcPath, destPath: destPath, sharedSecret: sharedSecretNSData as Data) else {
            print("Encryption failed")
            return nil
        }
        
        let result = resultPointer.pointee

        // Преобразование block и authTag в Data
        let blockData = Data(bytes: result.block, count: 12)
        let authTagData = Data(bytes: result.authTag, count: 16)
        
        // Конвертация Data в KotlinByteArray
        let blockKotlinByteArray = KotlinByteArray.fromNSData(blockData)
        let authTagKotlinByteArray = KotlinByteArray.fromNSData(authTagData)
        
        // Освобождение памяти, выделенной в Objective-C коде
        free(result.block)
        free(result.authTag)
        free(resultPointer)
        
        return ComposeApp.EncapsulationFileResult(block: blockKotlinByteArray, authTag: authTagKotlinByteArray)
    }

    
    func decupsChachaFile(cipherFilePath: String, jEncryptedFilePath: String, block: KotlinByteArray, authTag: KotlinByteArray, sharedSecret: KotlinByteArray) -> String? {
        let srcURL = URL(fileURLWithPath: cipherFilePath)
        let destURL = URL(fileURLWithPath: jEncryptedFilePath)

        let srcPath = srcURL.path
        let destPath = destURL.path
        let blockData = block.toNSData()
        let authTagData = authTag.toNSData()
        let sharedSecretData = sharedSecret.toNSData()

        // Вызов функции из Objective-C
        guard let result = WolfsslModule.decupsChachaFile(withSrcPath: srcPath, destPath: destPath, block: blockData, authTag: authTagData, sharedSecret: sharedSecretData) else {
            return nil
        }

        return result
    }
    
    

}

@objcMembers
class IOChecker: NSObject, ComposeApp.CipherInterface {
    func getSharedSecretAndCipherText(publicKey: KotlinByteArray) -> ComposeApp.SharedSecretResult? {
        let wrapper = EncryptionWrapperIOS()
        return wrapper.getSharedSecretAndCipherText(publicKey.toNSData())
    }
    
    func encupsChachaMessage(message: String, sharedSecret: KotlinByteArray) -> ComposeApp.EncapsulationMessageResult {
        let wrapper = EncryptionWrapperIOS()
        return wrapper.encupsChachaMessage(message, sharedSecret: sharedSecret.toNSData())
    }
    
    func decupsChachaMessage(
        cipher: KotlinByteArray,
        block: KotlinByteArray,
        authTag: KotlinByteArray,
        sharedSecret: KotlinByteArray
    ) -> String? {
        let wrapper = EncryptionWrapperIOS()
        return wrapper.decupsChachaMessage(cipher.toNSData(), block: block.toNSData(), authTag: authTag.toNSData(), sharedSecret: sharedSecret.toNSData())
    }
    
    
    func encupsChachaFile(
        filePath: String,
        cipherFilePath: String,
        sharedSecret: KotlinByteArray
    ) -> ComposeApp.EncapsulationFileResult {
        let wrapper = EncryptionWrapperIOS()
        guard let result = wrapper.encupsChachaFile(filePath, cipherFilePath, sharedSecret: sharedSecret.toNSData()) else {
            fatalError("Encryption failed")
        }
        return result
    }

    func decupsChachaFile(
        cipherFilePath: String,
        jEncryptedFilePath: String,
        block: KotlinByteArray,
        authTag: KotlinByteArray,
        sharedSecret: KotlinByteArray
    ) -> String? {
        let wrapper = EncryptionWrapperIOS()
        return wrapper.decupsChachaFile(cipherFilePath: cipherFilePath, jEncryptedFilePath: jEncryptedFilePath, block: KotlinByteArray.fromNSData(block.toNSData()), authTag: KotlinByteArray.fromNSData(authTag.toNSData()), sharedSecret: KotlinByteArray.fromNSData(sharedSecret.toNSData()))
    }
    
}

extension KotlinByteArray {
    func toNSData() -> Data {
        var byteArray = [UInt8](repeating: 0, count: Int(self.size))
        for i in 0..<self.size {
            byteArray[Int(i)] = UInt8(bitPattern: self.get(index: i))
        }
        return Data(byteArray)
    }

    static func fromNSData(_ data: Data) -> KotlinByteArray {
        let byteArray = KotlinByteArray(size: Int32(data.count))
        for (index, byte) in data.enumerated() {
            byteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
        }
        return byteArray
    }
}
