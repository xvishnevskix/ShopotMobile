#include <jni.h>
#include <wolfssl/options.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <stdlib.h>
#include <android/log.h>


typedef struct {
    unsigned char *cipher;
} Opensss;


JNIEXPORT jbyteArray

JNICALL
Java_com_shopot_WolfsslModule_decrypt(JNIEnv *env, jobject obj, jbyteArray cipher, jbyteArray block,
                                      jbyteArray authTagData, jbyteArray shared_secret) {


    __android_log_print(ANDROID_LOG_INFO, "cipher", "Cipher Length: %d", cipher);


    if (cipher == NULL || block == NULL || authTagData == NULL || shared_secret == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "ddddddddddddddddddddddddddddds",
                            "One or more null array arguments");
        return NULL;
    }


    // Преобразование jbyteArray в unsigned char*
    jbyte *shared_secret_bytes = (*env)->GetByteArrayElements(env, shared_secret, NULL);
    jbyte *block_bytes = (*env)->GetByteArrayElements(env, block, NULL);
    jbyte *cipher_bytes = (*env)->GetByteArrayElements(env, cipher, NULL);
    jbyte *authTagData_bytes = (*env)->GetByteArrayElements(env, authTagData, NULL);


    int cipherLength = (*env)->GetArrayLength(env, cipher);


    __android_log_print(ANDROID_LOG_INFO, "cipherLength", "Cipher Length: %d", cipherLength);


    unsigned char *authTag = calloc(16, 1);
    for (size_t i = 0; i < 16; i++) {
        authTag[i] = authTagData_bytes[i];
    }


    unsigned char *inAAD = "";
    char *mess = (char *) calloc(cipherLength + 1, 1);



//     __android_log_print(ANDROID_LOG_INFO, "привет1", "Cipher Length: %d", shared_secret_bytes);
//     __android_log_print(ANDROID_LOG_INFO, "привет2", "Cipher Length: %d", block_bytes);
// __android_log_print(ANDROID_LOG_INFO, "привет3", "Cipher Length: %d", inAAD);
// __android_log_print(ANDROID_LOG_INFO, "привет4", "Cipher Length: %d",cipher_bytes);
// __android_log_print(ANDROID_LOG_INFO, "привет5", "Cipher Length: %d", cipherLength);
// __android_log_print(ANDROID_LOG_INFO, "привет6", "Cipher Length: %d", authTag);
// __android_log_print(ANDROID_LOG_INFO, "привет7", "Cipher Length: %d", mess);





    int ch = wc_ChaCha20Poly1305_Decrypt(shared_secret_bytes, block_bytes, inAAD, 0, cipher_bytes,
                                         cipherLength, authTag, mess);


    if (ch != 0) {


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


    __android_log_print(ANDROID_LOG_INFO, "messFive", "Return value of wc_InitRng: %d");


    return result;
}