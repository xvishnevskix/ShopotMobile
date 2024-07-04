// #include <node_api.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <wolfssl/wolfcrypt/chacha.h>
#include <wolfssl/wolfcrypt/dh.h>
#include <wolfssl/wolfcrypt/random.h>
#include <wolfssl/wolfcrypt/chacha20_poly1305.h>
#include <jni.h>
#include <android/log.h>


typedef struct {
    int length;
    unsigned char *cipher;
    unsigned char *block;
    unsigned char *authTag;
} EncapsulationResult;


EncapsulationResult encapsulate_with_chacha(unsigned char *message, unsigned char *shared_secret) {
    EncapsulationResult result = {NULL, NULL, NULL};


    RNG rng;
    int ret = wc_InitRng(&rng);
    if (ret != 0) {
        return result; // init of rng failed!
    }
    byte *block = calloc(12, 1);
    if (block == NULL) {
        wc_FreeRng(&rng);
        return result; // memory allocation failed
    }


    ret = wc_RNG_GenerateBlock(&rng, block, 12); // generate IV in block
    if (ret != 0) {
        free(block);
        wc_FreeRng(&rng);
        return result; // generating block failed!
    }

    byte *inAAD = ""; // additional authenticated data
    byte *cipher = (byte *) calloc(strlen(message) + 1, 1);
    if (cipher == NULL) {
        free(block);
        wc_FreeRng(&rng);
        printf("Error in chiper");
        return result; // memory allocation failed
    }

    byte *authTag = calloc(16, 1);
    if (authTag == NULL) {
        free(block);
        free(cipher);
        wc_FreeRng(&rng);
        printf("Error in authTag allocation");
        return result; // memory allocation failed
    }


    int ch = wc_ChaCha20Poly1305_Encrypt(shared_secret, block, inAAD, 0, (byte *) message,
                                         strlen(message), cipher, authTag); // encryption
    if (ch != 0) {
        __android_log_print(ANDROID_LOG_INFO, "Ошибка при 1 энкрипте",
                            "Return value of wc_InitRng: %d");
        // printf("Ошибка при 1 энкрипте");
        free(block);
        free(cipher);
        free(authTag);
        wc_FreeRng(&rng);
        return result;
    }



    // You might want to process the encrypted cipher or the authTag here.

    // Cleanup
    // free(block);
    // free(cipher);
    // free(authTag);
    wc_FreeRng(&rng);

    result.cipher = cipher;
    result.block = block;
    result.authTag = authTag;
    result.length = strlen(message);

    return result;
}

JNIEXPORT jobject

JNICALL Java_com_shopot_WolfsslModule_euncupsChacha(JNIEnv *env, jobject obj, jstring message,
                                                    jbyteArray shared_secretJava) {

    // Преобразовать jstring в const char*
    const char *nativeMessage = (*env)->GetStringUTFChars(env, message, 0);

    // // Преобразовать jbyteArray в unsigned char*
    jbyte *sharedSecretBytes = (*env)->GetByteArrayElements(env, shared_secretJava, NULL);
    jsize sharedSecretLen = (*env)->GetArrayLength(env, shared_secretJava);

    __android_log_print(ANDROID_LOG_INFO, "until12222", "Return value of wc_InitRng: %d");


    EncapsulationResult result = encapsulate_with_chacha(nativeMessage,
                                                         (unsigned char *) sharedSecretBytes);

    // Освободите ресурсы после использования
    (*env)->ReleaseStringUTFChars(env, message, nativeMessage);
    (*env)->ReleaseByteArrayElements(env, shared_secretJava, sharedSecretBytes, 0);


    __android_log_print(ANDROID_LOG_INFO, "until11333", "Return value of wc_InitRng: %d");


    jbyteArray cipherJava = (*env)->NewByteArray(env, result.length);
    (*env)->SetByteArrayRegion(env, cipherJava, 0, result.length, (jbyte *) result.cipher);

    jbyteArray blockJava = (*env)->NewByteArray(env, 12);
    (*env)->SetByteArrayRegion(env, blockJava, 0, 12, (jbyte *) result.block);

    jbyteArray authTagJava = (*env)->NewByteArray(env, 16);
    (*env)->SetByteArrayRegion(env, authTagJava, 0, 16, (jbyte *) result.authTag);


    __android_log_print(ANDROID_LOG_INFO, "unti313131313131231314141",
                        "Return value of wc_InitRng: %d");


//  free(result.cipher);
//     free(result.block);
//     free(result.authTag);

    __android_log_print(ANDROID_LOG_INFO, "unti31313131", "Return value of wc_InitRng: %d");

    jclass resultClass = (*env)->FindClass(env, "com/shopot/EncapsulationResultJava");
    jmethodID constructor = (*env)->GetMethodID(env, resultClass, "<init>", "([B[B[B)V");
    jobject resultObj = (*env)->NewObject(env, resultClass, constructor, cipherJava, blockJava,
                                          authTagJava);

    __android_log_print(ANDROID_LOG_INFO, "after11", "Return value of wc_InitRng: %d");

    return resultObj;


}


// // Создать Java-объект для EncapsulationResult
// jclass resultClass = (*env)->FindClass(env, "com/shopot/WolfsslModule/EncapsulationResultJava");
// jmethodID constructor = (*env)->GetMethodID(env, resultClass, "<init>", "([B[B[B)V");


// // jobject resultJavaObject = (*env)->NewObject(env, resultClass, constructor, cipherJava, blockJava, authTagJava);