#import "SharedSecret.h"
#include <oqs/oqs.h>

// Используйте макрос для определения длины, если они уже определены в oqs.h
#define OQS_KEM_KYBER_768_LENGTH_CIPHERTEXT 1088 // Укажите правильное значение, если оно другое
#define OQS_KEM_KYBER_768_LENGTH_SHARED_SECRET 32 // Укажите правильное значение, если оно другое

EncapsulationResult encapsulate_with_public_key(unsigned char *public_key) {
    EncapsulationResult result = {NULL, NULL};

    if (public_key == NULL) {
        printf("public_key не может быть NULL\n");
        return result;
    }

    unsigned char *shared_secret = calloc(OQS_KEM_KYBER_768_LENGTH_SHARED_SECRET, 1);
    if (shared_secret == NULL) {
        printf("ошибка в shared_secret\n");
        return result;
    }

    unsigned char *ciphertext = calloc(OQS_KEM_KYBER_768_LENGTH_CIPHERTEXT, sizeof(unsigned char));
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

@implementation SharedSecretModule

+ (NSArray

<NSString *> *)getSharedSecret:(NSString *)publicKeyBase64 {
    NSData *publicKeyData = [[NSData alloc] initWithBase64EncodedString:publicKeyBase64 options:0];
    if (!publicKeyData) {
        return nil;
    }

    EncapsulationResult result = encapsulate_with_public_key(
            (unsigned char *) [publicKeyData bytes]);

    if (result.ciphertext != NULL && result.shared_secret != NULL) {
        NSData *ciphertextData = [NSData dataWithBytes:result.ciphertext length:OQS_KEM_KYBER_768_LENGTH_CIPHERTEXT];
        NSData *sharedSecretData = [NSData dataWithBytes:result.shared_secret length:OQS_KEM_KYBER_768_LENGTH_SHARED_SECRET];

        NSString *ciphertextBase64 = [ciphertextData base64EncodedStringWithOptions:0];
        NSString *sharedSecretBase64 = [sharedSecretData base64EncodedStringWithOptions:0];

        free(result.ciphertext);
        free(result.shared_secret);

        return @[ciphertextBase64, sharedSecretBase64];
    } else {
        return nil;
    }
}

@end
