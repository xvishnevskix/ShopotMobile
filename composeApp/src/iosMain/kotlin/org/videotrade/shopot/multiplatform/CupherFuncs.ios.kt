package org.videotrade.shopot.multiplatform

actual fun getSharedSecret(publicKeyBase64: String): List<String> {
    TODO("Not yet implemented")
    
}


actual fun sharedSecret(publicKey: ByteArray): Array<ByteArray> {
    TODO("Not yet implemented")
}

actual fun encupsChachaMessage(
    message: String,
    sharedSecret: ByteArray
): EncapsulationResultJava {
    TODO("Not yet implemented")
}

actual fun decupsChachaMessage(
    cipher: ByteArray,
    block: ByteArray,
    authTag: ByteArray,
    sharedSecret: ByteArray
): String? {
    TODO("Not yet implemented")
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class EncapsulateChecker actual constructor(
    private val checker: EncryptionWrapperChecker
) {
    actual fun encapsulateAvailable(publicKey: String): String? {
        return checker.encapsulate(publicKey)
    }
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class InternetConnectionChecker actual constructor(
    private val checker: ConnectionChecker
) {
    actual fun isInternetAvailable(): Boolean = checker.isConnectedToInternet()
}

