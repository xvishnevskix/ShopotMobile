import Foundation
import ComposeApp

@objc class EncryptionResultWrapper: NSObject {
    let ciphertext: Data
    let sharedSecret: Data

    init(ciphertext: Data, sharedSecret: Data) {
        self.ciphertext = ciphertext
        self.sharedSecret = sharedSecret
    }
}

class EncryptionWrapperIOS {
    func encapsulate(_ publicKey: Data) -> EncryptionResultWrapper? {
        print("EncryptionWrapperIOS: Starting encapsulate with publicKey: \(publicKey.base64EncodedString())")
        
        let publicKeyBytes = [UInt8](publicKey)
        print("EncryptionWrapperIOS: Converted publicKey to bytes")

        var encapsulationResult = encapsulate_with_public_key(UnsafeMutablePointer(mutating: publicKeyBytes))
        print("EncryptionWrapperIOS: Called encapsulate_with_public_key")

        guard let ciphertext = encapsulationResult.ciphertext, let sharedSecret = encapsulationResult.shared_secret else {
            print("EncryptionWrapperIOS: Encapsulation failed, ciphertext or sharedSecret is nil")
            return nil
        }

        let ciphertextData = Data(bytes: ciphertext, count: Int(OQS_KEM_kyber_768_length_ciphertext))
        let sharedSecretData = Data(bytes: sharedSecret, count: Int(OQS_KEM_kyber_768_length_shared_secret))

        free(encapsulationResult.ciphertext)
        free(encapsulationResult.shared_secret)
        
        print("EncryptionWrapperIOS: Successfully encapsulated, returning results")

        return EncryptionResultWrapper(ciphertext: ciphertextData, sharedSecret: sharedSecretData)
    }
}

class IOChecker: NSObject, EncryptionWrapperChecker {
    func encapsulate(publicKey: String) -> String? {
        print("IOChecker: Starting encapsulate with publicKey: \(publicKey)")

        guard let publicKeyData = decodeJsonPublicKey(jsonString: publicKey) else {
            print("IOChecker: Failed to decode publicKey from JSON")
            return nil
        }

        print("IOChecker: Successfully decoded publicKey to Data")

        let wrapper = EncryptionWrapperIOS()
        guard let result = wrapper.encapsulate(publicKeyData) else {
            print("IOChecker: Failed to encapsulate with EncryptionWrapperIOS")
            return nil
        }

        let ciphertextBase64 = result.ciphertext.base64EncodedString()
        let sharedSecretBase64 = result.sharedSecret.base64EncodedString()
        
        print("IOChecker: Successfully encapsulated, ciphertextBase64: \(ciphertextBase64), sharedSecretBase64: \(sharedSecretBase64)")
        
        return "Ciphertext:\(ciphertextBase64),SharedSecret:\(sharedSecretBase64)"
    }
    
    private func decodeJsonPublicKey(jsonString: String) -> Data? {
        print("IOChecker: Decoding JSON publicKey")
        guard let jsonData = jsonString.data(using: .utf8) else {
            print("IOChecker: Failed to convert JSON string to Data")
            return nil
        }
        
        do {
            if let jsonObject = try JSONSerialization.jsonObject(with: jsonData, options: []) as? [String: Any],
               let byteArray = jsonObject["bytes"] as? [Int] {
                let data = Data(byteArray.map { UInt8($0) })
                print("IOChecker: Successfully decoded JSON to Data")
                return data
            } else {
                print("IOChecker: JSON structure is invalid")
                return nil
            }
        } catch {
            print("IOChecker: Failed to decode JSON with error: \(error)")
            return nil
        }
    }
}

extension KotlinByteArray {
    func toBase64String() -> String {
        print("KotlinByteArray: Converting to Base64 string")
        
        var byteArray = [UInt8](repeating: 0, count: Int(self.size))
        for i in 0..<self.size {
            byteArray[Int(i)] = UInt8(bitPattern: self.get(index: i))
        }
        
        let base64String = Data(byteArray).base64EncodedString()
        print("KotlinByteArray: Successfully converted to Base64 string: \(base64String)")
        
        return base64String
    }
}
