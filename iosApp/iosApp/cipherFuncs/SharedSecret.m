#include "SharedSecret.h"
#include <stdlib.h>
#include <stdio.h>

#define OQS_KEM_KYBER_768_LENGTH_CIPHERTEXT 1088 // Укажите правильное значение, если оно другое
#define OQS_KEM_KYBER_768_LENGTH_SHARED_SECRET 32 // Укажите правильное значение, если оно другое

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
        free(shared_secret);
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
