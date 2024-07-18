#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <android/log.h>
#include <jni.h>

#define LOG_TAG "WolfsslModule"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define BUFFER_SIZE 8192 // 8 KB buffer size

JNIEXPORT jstring JNICALL Java_org_videotrade_shopot_cipher_WolfsslModule_decupsChachaFile(
        JNIEnv *env, jobject obj, jstring jSrcPath, jstring jDestPath, jbyteArray jBlock,
        jbyteArray jAuthTag, jbyteArray jSharedSecret) {

    const char *srcPath = NULL;
    const char *destPath = NULL;
    jbyte *sharedSecretData = NULL;
    jbyte *blockData = NULL;
    jbyte *authTagData = NULL;
    FILE *srcFile = NULL;
    FILE *destFile = NULL;

    // Получение C строк из Java String
    srcPath = (*env)->GetStringUTFChars(env, jSrcPath, 0);
    if (srcPath == NULL) {
        LOGE("Failed to get source path.");
        return NULL;
    }

    destPath = (*env)->GetStringUTFChars(env, jDestPath, 0);
    if (destPath == NULL) {
        LOGE("Failed to get destination path.");
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        return NULL;
    }

    // Получение байтов из Java byte array
    sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);
    if (sharedSecretData == NULL) {
        LOGE("Failed to get shared secret.");
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
        return NULL;
    }

    blockData = (*env)->GetByteArrayElements(env, jBlock, NULL);
    if (blockData == NULL) {
        LOGE("Failed to get block.");
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        return NULL;
    }

    authTagData = (*env)->GetByteArrayElements(env, jAuthTag, NULL);
    if (authTagData == NULL) {
        LOGE("Failed to get auth tag.");
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        return NULL;
    }

    LOGI("Trying to open source file: %s", srcPath);

    // Открытие исходного файла для чтения
    srcFile = fopen(srcPath, "rb");
    if (srcFile == NULL) {
        LOGE("Failed to open source file: %s", srcPath);
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        return NULL;
    }

    // Открытие файла для записи расшифрованных данных
    destFile = fopen(destPath, "wb");
    if (destFile == NULL) {
        LOGE("Failed to open destination file: %s", destPath);
        fclose(srcFile);
        (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
        (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        return NULL;
    }

    unsigned char cipherBuffer[BUFFER_SIZE + 16];
    unsigned char plainBuffer[BUFFER_SIZE]; // Данные без тега аутентификации
    size_t bytesRead;
    byte readAuthTag[16];

    // Чтение и расшифровка файла блоками
    while ((bytesRead = fread(cipherBuffer, 1, BUFFER_SIZE + 16, srcFile)) > 0) {
        if (bytesRead <= 16) {
            LOGE("Invalid data read.");
            fclose(srcFile);
            fclose(destFile);
            (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
            (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
            (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
            (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
            (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
            return NULL;
        }
        memcpy(readAuthTag, cipherBuffer + bytesRead - 16, 16);
        if (wc_ChaCha20Poly1305_Decrypt((const byte *) sharedSecretData, (const byte *) blockData,
                                        NULL, 0, cipherBuffer, bytesRead - 16,
                                        readAuthTag, plainBuffer) != 0) {
            LOGE("Decryption failed.");
            fclose(srcFile);
            fclose(destFile);
            (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
            (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
            (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
            (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
            (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
            return NULL;
        }
        fwrite(plainBuffer, 1, bytesRead - 16, destFile);
    }

    LOGI("Decryption successful.");

    // Очистка ресурсов
    fclose(srcFile);
    fclose(destFile);
    (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
    (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
    (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
    (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
    (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);

    return (*env)->NewStringUTF(env, destPath);
}
