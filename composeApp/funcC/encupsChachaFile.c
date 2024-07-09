#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <wolfssl/wolfcrypt/random.h>
#include <android/log.h>


JNIEXPORT jobject

JNICALL Java_com_shopot_WolfsslModule_encupsChachaFileC(JNIEnv *env, jobject obj, jstring jSrcPath,
                                                        jstring jDestPath,
                                                        jbyteArray jSharedSecret) {
    const char *srcPath = NULL;
    const char *destPath = NULL;
    jbyte *sharedSecretData = NULL;
    unsigned char *fileData = NULL;
    byte *cipherText = NULL;
    FILE *srcFile = NULL;
    FILE *destFile = NULL;
    jclass cls = NULL;
    jobject resultObject = NULL;


    __android_log_print(ANDROID_LOG_INFO, "log1111111", "Cipher Length: %d");

    // Получение C строк из Java String
    srcPath = (*env)->GetStringUTFChars(env, jSrcPath, 0);
    if (srcPath == NULL) goto cleanup;

    __android_log_print(ANDROID_LOG_INFO, "log1111111", "Cipher Length: %d");


    destPath = (*env)->GetStringUTFChars(env, jDestPath, 0);
    if (destPath == NULL) goto cleanup;

    __android_log_print(ANDROID_LOG_INFO, "log12222222", "Cipher Length: %d");


    // Получение байтов из Java byte array
    sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);
    if (sharedSecretData == NULL) goto cleanup;


    __android_log_print(ANDROID_LOG_INFO, "log321313131", "Trying to open file: %s", srcPath);


    // Открытие исходного файла для чтения
    srcFile = fopen(srcPath, "rb");
    if (srcFile == NULL) goto cleanup;

    srcFile = fopen(srcPath, "rb");
    if (srcFile == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "WolfsslModule", "Failed to open file");
        goto cleanup;
    }


    // Определение размера файла
    fseek(srcFile, 0, SEEK_END);
    long fileSize = ftell(srcFile);
    fseek(srcFile, 0, SEEK_SET);


    __android_log_print(ANDROID_LOG_INFO, "log22222222222", "Cipher Length: %d");



    // Чтение содержимого файла
    fileData = (unsigned char *) malloc(fileSize);
    if (fileData == NULL) goto cleanup;
    if (fread(fileData, 1, fileSize, srcFile) != fileSize) goto cleanup;
    fclose(srcFile);
    srcFile = NULL;

    // Инициализация параметров шифрования
    byte block[12];
    RNG rng;
    if (wc_InitRng(&rng) != 0 || wc_RNG_GenerateBlock(&rng, block, sizeof(block)) != 0)
        goto cleanup;
    wc_FreeRng(&rng);

    byte authTag[16];
    cipherText = (byte *) malloc(fileSize + 16); // Дополнительное место для Auth Tag
    if (cipherText == NULL) goto cleanup;


    __android_log_print(ANDROID_LOG_INFO, "log333333333", "Cipher Length: %d");


    // Шифрование
    if (wc_ChaCha20Poly1305_Encrypt(sharedSecretData, block, NULL, 0, fileData, fileSize,
                                    cipherText, authTag) != 0)
        goto cleanup;

    __android_log_print(ANDROID_LOG_INFO, "log444444444444444444444", "Cipher Length: %d");

    // Запись зашифрованных данных в файл
    destFile = fopen(destPath, "wb");
    if (destFile == NULL) goto cleanup;
    fwrite(cipherText, 1, fileSize, destFile);
    fwrite(authTag, 1, 16, destFile);
    fclose(destFile);
    destFile = NULL;

    __android_log_print(ANDROID_LOG_INFO, "log555555555555555555555", "Cipher Length: %d");

    // Создание jbyteArray для block и authTag
    jbyteArray jBlock = (*env)->NewByteArray(env, sizeof(block));
    (*env)->SetByteArrayRegion(env, jBlock, 0, sizeof(block), (jbyte *) block);

    jbyteArray jAuthTag = (*env)->NewByteArray(env, sizeof(authTag));
    (*env)->SetByteArrayRegion(env, jAuthTag, 0, sizeof(authTag), (jbyte *) authTag);

    // Получение ссылки на класс и конструктор EncryptionResult
    cls = (*env)->FindClass(env, "com/shopot/EncryptionResult");
    if (cls == NULL) goto cleanup;
    jmethodID constructor = (*env)->GetMethodID(env, cls, "<init>", "([B[B)V");
    if (constructor == NULL) goto cleanup;

    // Создание нового объекта EncryptionResult
    resultObject = (*env)->NewObject(env, cls, constructor, jBlock, jAuthTag);

    __android_log_print(ANDROID_LOG_INFO, "log6666666666666666666666", "Cipher Length: %d");


    cleanup:
    if (srcPath != NULL) (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
    if (destPath != NULL) (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
    if (sharedSecretData != NULL)
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
    if (fileData != NULL) free(fileData);
    if (cipherText != NULL) free(cipherText);
    if (srcFile != NULL) fclose(srcFile);
    if (destFile != NULL) fclose(destFile);

    return resultObject;
}


// #include <jni.h>
// #include <stdio.h>
// #include <stdlib.h>
// #include <string.h>

// #include <wolfssl/options.h>
// #include <wolfssl/wolfcrypt/chacha20_poly1305.h>
// #include <wolfssl/wolfcrypt/random.h>
// #include <android/log.h>

// #define BUFFER_SIZE 65536 // Размер блока для поточной обработки

// JNIEXPORT jobject JNICALL Java_com_shopot_WolfsslModule_encupsChachaFileC(JNIEnv *env, jobject obj, jstring jSrcPath, jstring jDestPath, jbyteArray jSharedSecret) {
//     const char *srcPath = NULL;
//     const char *destPath = NULL;
//     jbyte *sharedSecretData = NULL;
//     unsigned char buffer[BUFFER_SIZE];
//     unsigned char encryptedBuffer[BUFFER_SIZE + 16]; // Дополнительное место для Auth Tag
//     FILE *srcFile = NULL;
//     FILE *destFile = NULL;
//     jclass cls = NULL;
//     jobject resultObject = NULL;
//     byte block[12], authTag[16];
//     RNG rng;

//     // Получение C строк из Java String
//     srcPath = (*env)->GetStringUTFChars(env, jSrcPath, 0);
//     destPath = (*env)->GetStringUTFChars(env, jDestPath, 0);

//     // Получение байтов из Java byte array
//     sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);

//     // Инициализация RNG для nonce
//     if (wc_InitRng(&rng) != 0) goto cleanup;

//     // Открытие исходного файла для чтения
//     srcFile = fopen(srcPath, "rb");
//     if (srcFile == NULL) goto cleanup;

//     // Открытие файла назначения для записи
//     destFile = fopen(destPath, "wb");
//     if (destFile == NULL) goto cleanup;

//     long bytesRead;
//     // Чтение, шифрование и запись файла по частям
//     while ((bytesRead = fread(buffer, 1, BUFFER_SIZE, srcFile)) > 0) {
//         // Генерация нового nonce для каждого блока
//         if (wc_RNG_GenerateBlock(&rng, block, sizeof(block)) != 0) goto cleanup;

//         // Шифрование блока
//         if (wc_ChaCha20Poly1305_Encrypt(sharedSecretData, block, NULL, 0, buffer, bytesRead, encryptedBuffer, authTag) != 0) goto cleanup;

//         // Запись зашифрованного блока в файл назначения
//         fwrite(encryptedBuffer, 1, bytesRead, destFile);
//         // Запись Auth Tag в файл назначения
//         fwrite(authTag, 1, sizeof(authTag), destFile);
//     }

//     wc_FreeRng(&rng); // Освобождение ресурсов RNG

//     __android_log_print(ANDROID_LOG_INFO, "log555555555555555555555", "Cipher Length: %d");

//     // Создание jbyteArray для block и authTag
//     jbyteArray jBlock = (*env)->NewByteArray(env, sizeof(block));
//     (*env)->SetByteArrayRegion(env, jBlock, 0, sizeof(block), (jbyte*)block);

//     jbyteArray jAuthTag = (*env)->NewByteArray(env, sizeof(authTag));
//     (*env)->SetByteArrayRegion(env, jAuthTag, 0, sizeof(authTag), (jbyte*)authTag);

//     // Получение ссылки на класс и конструктор EncryptionResult
//     cls = (*env)->FindClass(env, "com/shopot/EncryptionResult");
//     if (cls == NULL) goto cleanup;
//     jmethodID constructor = (*env)->GetMethodID(env, cls, "<init>", "([B[B)V");
//     if (constructor == NULL) goto cleanup;

//     // Создание нового объекта EncryptionResult
//     resultObject = (*env)->NewObject(env, cls, constructor, jBlock, jAuthTag);

//     __android_log_print(ANDROID_LOG_INFO, "log6666666666666666666666", "Cipher Length: %d");

// cleanup:
//     // Освобождение ресурсов и закрытие файлов
//     if (srcPath != NULL) (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
//     if (destPath != NULL) (*env)->ReleaseStringUTFChars(env, jDestPath, destPath);
//     if (sharedSecretData != NULL) (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
//     if (srcFile != NULL) fclose(srcFile);
//     if (destFile != NULL) fclose(destFile);
//     wc_FreeRng(&rng); // Убедитесь, что RNG освобожден в случае ошибки перед выходом

//     return resultObject;
// }
