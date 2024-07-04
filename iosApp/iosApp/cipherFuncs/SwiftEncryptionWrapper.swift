import Foundation
import ComposeApp

@objc class EncryptionResultWrapper: NSObject {
    let ciphertext: NSData
    let sharedSecret: NSData

    init(ciphertext: NSData, sharedSecret: NSData) {
        self.ciphertext = ciphertext
        self.sharedSecret = sharedSecret
    }
}

class EncryptionWrapperIOS {
    func encapsulate(_ publicKey: Data) -> EncryptionResultWrapper? {
        let publicKeyBytes = [UInt8](publicKey)
        var encapsulationResult = encapsulate_with_public_key(UnsafeMutablePointer(mutating: publicKeyBytes))

        guard let ciphertext = encapsulationResult.ciphertext, let sharedSecret = encapsulationResult.shared_secret else {
            return nil
        }

        let ciphertextData = Data(bytes: ciphertext, count: Int(OQS_KEM_kyber_768_length_ciphertext))
        let sharedSecretData = Data(bytes: sharedSecret, count: Int(OQS_KEM_kyber_768_length_shared_secret))

        free(encapsulationResult.ciphertext)
        free(encapsulationResult.shared_secret)

        return EncryptionResultWrapper(ciphertext: ciphertextData as NSData, sharedSecret: sharedSecretData as NSData)
    }
}

class IOChecker: NSObject, EncryptionWrapperChecker {
    func encapsulate(publicKey: String) -> String? {
        guard let publicKeyData = Data(base64Encoded: publicKey) else {
            return nil
        }

        let wrapper = EncryptionWrapperIOS()
        guard let result = wrapper.encapsulate(publicKeyData) else {
            return nil
        }

        let ciphertextBase64 = result.ciphertext.base64EncodedString()
        let sharedSecretBase64 = result.sharedSecret.base64EncodedString()
        
        return "Ciphertext:\(ciphertextBase64),SharedSecret:\(sharedSecretBase64)"
    }
}

extension KotlinByteArray {
    func toBase64String() -> String {
        var byteArray = [UInt8](repeating: 0, count: Int(self.size))
        for i in 0..<self.size {
            byteArray[Int(i)] = UInt8(bitPattern: self.get(index: i))
        }
        return Data(byteArray).base64EncodedString()
    }
}
