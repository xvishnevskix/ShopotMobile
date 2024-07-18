#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <wolfssl/wolfcrypt/random.h>
#include <android/log.h>

#define LOG_TAG "WolfsslModule"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define BUFFER_SIZE 8192 // 8 KB buffer size

JNIEXPORT jobject JNICALL Java_org_videotrade_shopot_cipher_WolfsslModule_encupsChachaFile(
        JNIEnv *env, jobject obj, jstring jSrcPath, jstring jDestPath, jbyteArray jSharedSecret) {

    const char *srcPath = NULL;
    const char *destPath = NULL;
    jbyte *sharedSecretData = NULL;
    FILE *srcFile = NULL;
    FILE *destFile = NULL;
    jclass cls = NULL;
    jobject resultObject = NULL;
    byte block[12];
    byte authTag[16];
    RNG rng;

    // Получение C строк из Java String
    srcPath = (*env)->GetStringUTFChars(env, jSrcPath, 0);
    if (srcPath == NULL) {
        LOGE("Failed to get source path.");
        goto cleanup;
    }

    destPath = (*env)->GetStringUTFChars(env, jDestPath, 0);
    if (destPath == NULL) {
        LOGE("Failed to get destination path.");
        goto cleanup;
    }

    // Получение байтов из Java byte array
    sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);
    if (sharedSecretData == NULL) {
        LOGE("Failed to get shared secret.");
        goto cleanup;
    }

    LOGI("Trying to open source file: %s", srcPath);

    // Открытие исходного файла для чтения
    srcFile = fopen(srcPath, "rb");
    if (srcFile == NULL) {
        LOGE("Failed to open source file: %s", srcPath);
        goto cleanup;
    }

    // Открытие файла для записи зашифрованных данных
    destFile = fopen(destPath, "wb");
    if (destFile == NULL) {
        LOGE("Failed to open destination file: %s", destPath);
        goto cleanup;
    }

    // Инициализация параметров шифрования
    if (wc_InitRng(&rng) != 0 || wc_RNG_GenerateBlock(&rng, block, sizeof(block)) != 0) {
        LOGE("Failed to initialize RNG.");
        goto cleanup;
    }
    wc_FreeRng(&rng);

    unsigned char buffer[BUFFER_SIZE];
    size_t bytesRead;
    byte cipherBuffer[BUFFER_SIZE]; // Данные без тега аутентификации

    // Шифрование файла блоками
    while ((bytesRead = fread(buffer, 1, BUFFER_SIZE, srcFile)) > 0) {
        if (wc_ChaCha20Poly1305_Encrypt(sharedSecretData, block, NULL, 0, buffer, bytesRead,
                                        cipherBuffer, authTag) != 0) {
            LOGE("Encryption failed.");
            goto cleanup;
        }
        fwrite(cipherBuffer, 1, bytesRead, destFile);
        fwrite(authTag, 1, 16, destFile);
    }

    LOGI("Encryption successful.");

    // Создание jbyteArray для block и authTag
    jbyteArray jBlock = (*env)->NewByteArray(env, sizeof(block));
    if (jBlock == NULL) {
        LOGE("Failed to create byte array for block.");
        goto cleanup;
    }
    (*env)->SetByteArrayRegion(env, jBlock, 0, sizeof(block), (jbyte *) block);

    jbyteArray jAuthTag = (*env)->NewByteArray(env, sizeof(authTag));
    if (jAuthTag == NULL) {
        LOGE("Failed to create byte array for authTag.");
        goto cleanup;
    }
    (*env)->SetByteArrayRegion(env, jAuthTag, 0, sizeof(authTag), (jbyte *) authTag);



    // Получение ссылки на класс и конструктор EncapsulationFileResult
    cls = (*env)->FindClass(env, "org/videotrade/shopot/multiplatform/EncapsulationFileResult");
    if (cls == NULL) {
        LOGE("Failed to find class EncapsulationFileResult.");
        goto cleanup;
    }
    jmethodID constructor = (*env)->GetMethodID(env, cls, "<init>", "([B[B)V");
    if (constructor == NULL) {
        LOGE("Failed to find constructor of EncapsulationFileResult.");
        goto cleanup;
    }

    // Создание нового объекта EncapsulationFileResult
    resultObject = (*env)->NewObject(env, cls, constructor, jBlock, jAuthTag);
    if (resultObject == NULL) {
        LOGE("Failed to create EncapsulationFileResult object.");
        goto cleanup;
    }

    LOGI("EncapsulationFileResult created successfully.");

    cleanup:
    if (srcPath != NULL) (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
    if (destPath != NULL) (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
    if (sharedSecretData != NULL)
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
    if (srcFile != NULL) fclose(srcFile);
    if (destFile != NULL) fclose(destFile);

    return resultObject;
}
