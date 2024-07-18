#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <android/log.h>

#define LOG_TAG "WolfsslModule"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jstring JNICALL Java_org_videotrade_shopot_cipher_WolfsslModule_decupsChachaFile(
        JNIEnv *env, jobject obj, jstring jEncryptedFilePath, jstring jDecryptedFilePath,
        jbyteArray jBlock, jbyteArray jAuthTag, jbyteArray jSharedSecret) {

    LOGI("Starting decryption process");

    const char *encryptedFilePath = (*env)->GetStringUTFChars(env, jEncryptedFilePath, NULL);
    if (encryptedFilePath == NULL) {
        LOGE("Failed to get encrypted file path");
        return NULL;
    }
    LOGI("Encrypted file path: %s", encryptedFilePath);

    const char *decryptedFilePath = (*env)->GetStringUTFChars(env, jDecryptedFilePath, NULL);
    if (decryptedFilePath == NULL) {
        LOGE("Failed to get decrypted file path");
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        return NULL;
    }
    LOGI("Decrypted file path: %s", decryptedFilePath);

    jbyte *blockData = (*env)->GetByteArrayElements(env, jBlock, NULL);
    if (blockData == NULL) {
        LOGE("Failed to get block data");
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    jsize blockSize = (*env)->GetArrayLength(env, jBlock);
    LOGI("Block size: %d", blockSize);

    jbyte *authTagData = (*env)->GetByteArrayElements(env, jAuthTag, NULL);
    if (authTagData == NULL) {
        LOGE("Failed to get auth tag data");
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    jsize authTagSize = (*env)->GetArrayLength(env, jAuthTag);
    LOGI("Auth tag size: %d", authTagSize);

    jbyte *sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);
    if (sharedSecretData == NULL) {
        LOGE("Failed to get shared secret data");
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    jsize sharedSecretSize = (*env)->GetArrayLength(env, jSharedSecret);
    LOGI("Shared secret size: %d", sharedSecretSize);

    // Логирование данных
    LOGI("Block: %d %d %d %d %d %d %d %d %d %d %d %d", blockData[0], blockData[1], blockData[2],
         blockData[3], blockData[4], blockData[5], blockData[6], blockData[7], blockData[8],
         blockData[9], blockData[10], blockData[11]);
    LOGI("AuthTag: %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d %d", authTagData[0], authTagData[1],
         authTagData[2], authTagData[3], authTagData[4], authTagData[5], authTagData[6],
         authTagData[7], authTagData[8], authTagData[9], authTagData[10], authTagData[11],
         authTagData[12], authTagData[13], authTagData[14], authTagData[15]);

    LOGI("Opening encrypted file");
    FILE *encryptedFile = fopen(encryptedFilePath, "rb");
    if (encryptedFile == NULL) {
        LOGE("Failed to open encrypted file: %s", encryptedFilePath);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }

    fseek(encryptedFile, 0, SEEK_END);
    long fileSize = ftell(encryptedFile);
    fseek(encryptedFile, 0, SEEK_SET);
    LOGI("Encrypted file size: %ld", fileSize);

    unsigned char *encryptedData = (unsigned char *) malloc(fileSize);
    if (encryptedData == NULL) {
        LOGE("Failed to allocate memory for encrypted data");
        fclose(encryptedFile);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }

    if (fread(encryptedData, 1, fileSize, encryptedFile) != fileSize) {
        LOGE("Failed to read encrypted file data");
        fclose(encryptedFile);
        free(encryptedData);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    fclose(encryptedFile);

    unsigned char *decryptedData = (unsigned char *) malloc(fileSize - 16);
    if (decryptedData == NULL) {
        LOGE("Failed to allocate memory for decrypted data");
        free(encryptedData);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }

    LOGI("Starting decryption");
    if (wc_ChaCha20Poly1305_Decrypt((const byte *) sharedSecretData, (const byte *) blockData, NULL,
                                    0, encryptedData, fileSize - 16, (const byte *) authTagData,
                                    decryptedData) != 0) {
        LOGE("Decryption failed with wc_ChaCha20Poly1305_Decrypt");
        free(encryptedData);
        free(decryptedData);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    free(encryptedData);

    LOGI("Writing decrypted data to file");
    FILE *decryptedFile = fopen(decryptedFilePath, "wb");
    if (decryptedFile == NULL) {
        LOGE("Failed to open decrypted file for writing: %s", decryptedFilePath);
        free(decryptedData);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }

    if (fwrite(decryptedData, 1, fileSize - 16, decryptedFile) != (fileSize - 16)) {
        LOGE("Failed to write decrypted data to file");
        fclose(decryptedFile);
        free(decryptedData);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        return NULL;
    }
    fclose(decryptedFile);
    free(decryptedData);

    LOGI("Decryption process completed successfully");

    (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
    (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
    (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
    (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
    (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);

    return (*env)->NewStringUTF(env, decryptedFilePath);
}
