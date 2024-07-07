#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>

JNIEXPORT jstring

JNICALL Java_com_shopot_WolfsslModule_decupsChachaFileC(
        JNIEnv *env,
        jobject obj,
        jstring jEncryptedFilePath,
        jstring jDecryptedFilePath,
        jbyteArray jBlock,
        jbyteArray jAuthTag,
        jbyteArray jSharedSecret) {

    const char *encryptedFilePath = (*env)->GetStringUTFChars(env, jEncryptedFilePath, NULL);
    if (encryptedFilePath == NULL) return NULL;

    const char *decryptedFilePath = (*env)->GetStringUTFChars(env, jDecryptedFilePath, NULL);
    if (decryptedFilePath == NULL) {
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        return NULL;
    }

    jbyte *blockData = (*env)->GetByteArrayElements(env, jBlock, NULL);
    jbyte *authTagData = (*env)->GetByteArrayElements(env, jAuthTag, NULL);
    jbyte *sharedSecretData = (*env)->GetByteArrayElements(env, jSharedSecret, NULL);

    FILE *encryptedFile = fopen(encryptedFilePath, "rb");
    if (encryptedFile == NULL) {
        // Освободить ресурсы
        (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
        (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
        (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
        (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
        (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);
        return NULL;
    }

    fseek(encryptedFile, 0, SEEK_END);
    long fileSize = ftell(encryptedFile);
    fseek(encryptedFile, 0, SEEK_SET);

    unsigned char *encryptedData = (unsigned char *) malloc(fileSize);
    if (fread(encryptedData, 1, fileSize, encryptedFile) != fileSize) {
        // Обработка ошибки чтения файла
        fclose(encryptedFile);
        free(encryptedData);
        // Освободить ресурсы
        // ...
        return NULL;
    }
    fclose(encryptedFile);

    unsigned char *decryptedData = (unsigned char *) malloc(fileSize - 16);
    if (wc_ChaCha20Poly1305_Decrypt((const byte *) sharedSecretData, (const byte *) blockData, NULL,
                                    0, encryptedData, fileSize - 16, (const byte *) authTagData,
                                    decryptedData) != 0) {
        // Обработка ошибки расшифровки
        // ...
        free(encryptedData);
        free(decryptedData);
        // Освободить ресурсы
        // ...
        return NULL;
    }
    free(encryptedData);

    FILE *decryptedFile = fopen(decryptedFilePath, "wb");
    if (decryptedFile != NULL) {
        if (fwrite(decryptedData, 1, fileSize - 16, decryptedFile) != (fileSize - 16)) {
            // Обработка ошибки записи файла
            // ...
        }
        fclose(decryptedFile);
    }
    free(decryptedData);

    (*env)->ReleaseStringUTFChars(env, jEncryptedFilePath, encryptedFilePath);
    (*env)->ReleaseStringUTFChars(env, jDecryptedFilePath, decryptedFilePath);
    (*env)->ReleaseByteArrayElements(env, jBlock, blockData, 0);
    (*env)->ReleaseByteArrayElements(env, jAuthTag, authTagData, 0);
    (*env)->ReleaseByteArrayElements(env, jSharedSecret, sharedSecretData, 0);

    return (*env)->NewStringUTF(env, decryptedFilePath);
}
