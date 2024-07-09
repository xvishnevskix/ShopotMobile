#include <jni.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <stdlib.h>
#include <android/log.h>

JNIEXPORT jbyteArray

JNICALL
Java_org_videotrade_shopot_cipher_WolfsslModule_decupsChachaMessage(JNIEnv *env, jobject obj,
                                                                    jbyteArray cipher,
                                                                    jbyteArray block,
                                                                    jbyteArray authTagData,
                                                                    jbyteArray shared_secret) {

    __android_log_print(ANDROID_LOG_INFO, "decrypt", "Starting decryption process");

    if (cipher == NULL || block == NULL || authTagData == NULL || shared_secret == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "decrypt", "One or more null array arguments");
        return NULL;
    }

    // Преобразование jbyteArray в unsigned char*
    jbyte *shared_secret_bytes = (*env)->GetByteArrayElements(env, shared_secret, NULL);
    jbyte *block_bytes = (*env)->GetByteArrayElements(env, block, NULL);
    jbyte *cipher_bytes = (*env)->GetByteArrayElements(env, cipher, NULL);
    jbyte *authTagData_bytes = (*env)->GetByteArrayElements(env, authTagData, NULL);

    int cipherLength = (*env)->GetArrayLength(env, cipher);

    __android_log_print(ANDROID_LOG_INFO, "decrypt", "Cipher Length: %d", cipherLength);

    unsigned char *authTag = calloc(16, 1);
    for (size_t i = 0; i < 16; i++) {
        authTag[i] = authTagData_bytes[i];
    }

    unsigned char *inAAD = "";
    char *mess = (char *) calloc(cipherLength + 1, 1);

    int ch = wc_ChaCha20Poly1305_Decrypt((unsigned char *) shared_secret_bytes,
                                         (unsigned char *) block_bytes, inAAD, 0,
                                         (unsigned char *) cipher_bytes, cipherLength, authTag,
                                         (unsigned char *) mess);

    if (ch != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "decrypt", "Decryption failed with code: %d", ch);
        free(mess);
        return NULL;
    }

    jbyteArray result = (*env)->NewByteArray(env, cipherLength);
    (*env)->SetByteArrayRegion(env, result, 0, cipherLength, (jbyte *) mess);

    // Освобождаем ресурсы
    free(mess);
    (*env)->ReleaseByteArrayElements(env, shared_secret, shared_secret_bytes, 0);
    (*env)->ReleaseByteArrayElements(env, block, block_bytes, 0);
    (*env)->ReleaseByteArrayElements(env, cipher, cipher_bytes, 0);
    (*env)->ReleaseByteArrayElements(env, authTagData, authTagData_bytes, 0);

    __android_log_print(ANDROID_LOG_INFO, "decrypt", "Decryption successful");

    return result;
}
