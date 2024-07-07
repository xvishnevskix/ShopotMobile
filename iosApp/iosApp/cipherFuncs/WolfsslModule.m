//
//  WolfsslModule.m
//  iosApp
//
//  Created by Aslanbek Abubakarov on 05.07.2024.
//

#import "WolfsslModule.h"
#import <wolfssl/wolfcrypt/chacha.h>
#import <wolfssl/wolfcrypt/random.h>
#import <wolfssl/wolfcrypt/chacha20_poly1305.h>
#import <wolfssl/options.h>
#import <stdlib.h>

@implementation WolfsslModule

EncapsulationMessageResult
encapsulate_with_chacha(unsigned char *message, unsigned char *shared_secret) {
    EncapsulationMessageResult result = {0, NULL, NULL, NULL};

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
    byte *cipher = (byte *) calloc(strlen((char *) message) + 1, 1);
    if (cipher == NULL) {
        free(block);
        wc_FreeRng(&rng);
        printf("Error in cipher");
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
                                         strlen((char *) message), cipher, authTag); // encryption
    if (ch != 0) {
        free(block);
        free(cipher);
        free(authTag);
        wc_FreeRng(&rng);
        return result;
    }

    wc_FreeRng(&rng);

    result.cipher = cipher;
    result.block = block;
    result.authTag = authTag;
    result.length = strlen((char *) message);

    return result;
}

- (NSArray *)byteArrayToWritableArray:(unsigned char *)bytes length:(int)length {
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:length];
    for (int i = 0; i < length; i++) {
        [array addObject:[NSNumber numberWithUnsignedChar:bytes[i]]];
    }
    return array;
}

+ (NSData *)decryptWithCipher:(NSData *)cipher block:(NSData *)block authTagData:(NSData *)authTagData sharedSecret:(NSData *)sharedSecret {
    NSLog(@"decryptWithCipher called");
    if (!cipher || !block || !authTagData || !sharedSecret) {
        NSLog(@"One or more parameters are nil");
        return nil;
    }

    const unsigned char *sharedSecretBytes = [sharedSecret bytes];
    const unsigned char *blockBytes = [block bytes];
    const unsigned char *cipherBytes = [cipher bytes];
    const unsigned char *authTagBytes = [authTagData bytes];

    int cipherLength = (int) [cipher length];
    NSLog(@"Cipher length: %d", cipherLength);

    unsigned char *authTag = calloc(16, sizeof(unsigned char));
    for (int i = 0; i < 16; ++i) {
        authTag[i] = authTagBytes[i];
    }

    unsigned char *inAAD = (unsigned char *) "";
    unsigned char *decryptedMessage = (unsigned char *) calloc(cipherLength + 1,
                                                               sizeof(unsigned char));

    NSLog(@"Cipher Bytes: ");
    for (int i = 0; i < cipherLength; i++) {
        NSLog(@"%02x", cipherBytes[i]);
    }
    NSLog(@"Cipher Length: %d", cipherLength);

    // Вывод в консоль значения authTag
    NSLog(@"Auth Tag: ");
    for (int i = 0; i < 16; i++) {
        NSLog(@"%02x", authTag[i]);
    }

    int result = wc_ChaCha20Poly1305_Decrypt(sharedSecretBytes, blockBytes, inAAD, 0, cipherBytes,
                                             cipherLength, authTag, decryptedMessage);
    NSLog(@"wc_ChaCha20Poly1305_Decrypt result: %d", result);

    NSData *decryptedData = nil;
    if (result == 0) {
        decryptedData = [NSData dataWithBytes:decryptedMessage length:cipherLength];
    } else {
        NSLog(@"Decryption error with result code: %d", result);
    }

    free(decryptedMessage);
    free(authTag);

    return decryptedData;
}

@end
