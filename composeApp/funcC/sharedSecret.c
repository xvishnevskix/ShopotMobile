#include <stdio.h>
#include <stdlib.h>
#include <jni.h>
#include <oqs/oqs.h>
#include <android/log.h>

typedef struct {
    unsigned char *ciphertext;
    unsigned char *shared_secret;
} EncapsulationResult;

EncapsulationResult encapsulate_with_public_key(unsigned char *public_key) {
    EncapsulationResult result = {NULL, NULL};

    if (public_key == NULL) {
        printf("public_key не может быть NULL\n");
        return result;
    }

    unsigned char *shared_secret = calloc(OQS_KEM_kyber_768_length_shared_secret, 1);
    if (shared_secret == NULL) {
        printf("ошибка в shared_secret\n");
        return result;
    }

    unsigned char *ciphertext = calloc(OQS_KEM_kyber_768_length_ciphertext, sizeof(unsigned char));
    if (ciphertext == NULL) {
        printf("ошибка в ciphertext\n");
        free(shared_secret); // Освобождение shared_secret при ошибке в ciphertext
        return result;
    }

    if (OQS_KEM_kyber_768_encaps(ciphertext, shared_secret, public_key) != OQS_SUCCESS) {
        perror("Ошибка при создании шифртекста");
        free(shared_secret);
        free(ciphertext);
        return result;
    }

    result.ciphertext = ciphertext;
    result.shared_secret = shared_secret;
    return result;
}

JNIEXPORT jobject JNICALL Java_org_videotrade_shopot_cipher_SharedSecretModule_sharedSecretC(JNIEnv *env, jobject obj, jbyteArray publicKeyJava) {
    // Получение данных из массива
    jbyte *buffer = (*env)->GetByteArrayElements(env, publicKeyJava, NULL);
    if (buffer == NULL) {
        // Обработка ошибки, если массив не удалось получить
        return NULL; // Возвращение NULL может быть заменено на выброс исключения в JNI
    }

    jsize length = (*env)->GetArrayLength(env, publicKeyJava);
    // Здесь можно добавить проверку длины ключа, если необходимо

    EncapsulationResult result = encapsulate_with_public_key((unsigned char *) buffer);

    // Освобождение памяти, выделенной для buffer
    (*env)->ReleaseByteArrayElements(env, publicKeyJava, buffer, 0);

    if (result.ciphertext == NULL || result.shared_secret == NULL) {
        // Обработка случая, когда шифрование не удалось
        return NULL; // Аналогично, можно использовать выброс исключения в JNI
    }

    // Создание и заполнение jbyteArray для ciphertext и shared_secret
    jbyteArray ciphertextJava = (*env)->NewByteArray(env, OQS_KEM_kyber_768_length_ciphertext);
    (*env)->SetByteArrayRegion(env, ciphertextJava, 0, OQS_KEM_kyber_768_length_ciphertext, (jbyte *) result.ciphertext);

    jbyteArray sharedSecretJava = (*env)->NewByteArray(env, OQS_KEM_kyber_768_length_shared_secret);
    (*env)->SetByteArrayRegion(env, sharedSecretJava, 0, OQS_KEM_kyber_768_length_shared_secret, (jbyte *) result.shared_secret);

    // Находим класс SharedSecretResult
    jclass resultClass = (*env)->FindClass(env,
                                           "org/videotrade/shopot/multiplatform/SharedSecretResult");
    if (resultClass == NULL) {
        // Обработка ошибки, если класс не найден
        return NULL;
    }

    // Находим конструктор класса SharedSecretResult
    jmethodID constructor = (*env)->GetMethodID(env, resultClass, "<init>", "([B[B)V");
    if (constructor == NULL) {
        // Обработка ошибки, если конструктор не найден
        return NULL;
    }

    // Создание объекта SharedSecretResult
    jobject sharedSecretResult = (*env)->NewObject(env, resultClass, constructor, ciphertextJava,
                                                   sharedSecretJava);

    // Освобождение памяти, выделенной в C
    free(result.ciphertext);
    free(result.shared_secret);

    return sharedSecretResult;
}
