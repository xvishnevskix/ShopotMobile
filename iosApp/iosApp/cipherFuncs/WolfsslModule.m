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

#define BUFFER_SIZE 8192 // 8 KB buffer size

+ (EncapsulationFileResult *)encryptFile:(NSString *)srcPath destPath:(NSString *)destPath sharedSecret:(NSData *)sharedSecret {
    FILE *srcFile = NULL;
    FILE *destFile = NULL;
    unsigned char block[12];
    unsigned char authTag[16];
    RNG rng;
    EncapsulationFileResult *result = malloc(sizeof(EncapsulationFileResult));
    if (result == NULL) {
        NSLog(@"Memory allocation failed for EncapsulationFileResult.");
        return NULL;
    }
    result->block = NULL;
    result->authTag = NULL;

    const char *srcFilePath = [srcPath UTF8String];
    NSLog(@"Opening source file at path: %s", srcFilePath);
    srcFile = fopen(srcFilePath, "rb");
    if (srcFile == NULL) {
        NSLog(@"Failed to open source file: %s", srcFilePath);
        free(result);
        return NULL;
    }

    const char *destFilePath = [destPath UTF8String];
    NSLog(@"Opening destination file at path: %s", destFilePath);
    destFile = fopen(destFilePath, "wb");
    if (destFile == NULL) {
        NSLog(@"Failed to open destination file: %s", destFilePath);
        fclose(srcFile);
        free(result);
        return NULL;
    }

    if (wc_InitRng(&rng) != 0 || wc_RNG_GenerateBlock(&rng, block, sizeof(block)) != 0) {
        NSLog(@"Failed to initialize RNG.");
        fclose(srcFile);
        fclose(destFile);
        free(result);
        return NULL;
    }
    wc_FreeRng(&rng);

    unsigned char buffer[BUFFER_SIZE];
    size_t bytesRead;
    unsigned char cipherBuffer[BUFFER_SIZE]; // Данные без тега аутентификации

    while ((bytesRead = fread(buffer, 1, BUFFER_SIZE, srcFile)) > 0) {
        if (wc_ChaCha20Poly1305_Encrypt([sharedSecret bytes], block, NULL, 0, buffer,
                                        (word32) bytesRead, cipherBuffer, authTag) != 0) {
            NSLog(@"Encryption failed.");
            fclose(srcFile);
            fclose(destFile);
            free(result);
            return NULL;
        }
        fwrite(cipherBuffer, 1, bytesRead, destFile);
        fwrite(authTag, 1, 16, destFile);
    }

    NSLog(@"Encryption successful.");

    result->block = malloc(sizeof(block));
    if (result->block == NULL) {
        NSLog(@"Memory allocation failed for block.");
        fclose(srcFile);
        fclose(destFile);
        free(result);
        return NULL;
    }
    memcpy(result->block, block, sizeof(block));

    result->authTag = malloc(sizeof(authTag));
    if (result->authTag == NULL) {
        NSLog(@"Memory allocation failed for authTag.");
        free(result->block);
        fclose(srcFile);
        fclose(destFile);
        free(result);
        return NULL;
    }
    memcpy(result->authTag, authTag, sizeof(authTag));

    fclose(srcFile);
    fclose(destFile);

    return result;
}


+ (NSString *)decupsChachaFileWithSrcPath:(NSString *)srcPath
                                 destPath:(NSString *)destPath
                                    block:(NSData *)block
                                  authTag:(NSData *)authTag
                             sharedSecret:(NSData *)sharedSecret {

    if (!srcPath || !destPath || !block || !authTag || !sharedSecret) {
        NSLog(@"One or more parameters are nil");
        return nil;
    }

    FILE *srcFile = fopen([srcPath UTF8String], "rb");
    if (srcFile == NULL) {
        NSLog(@"Failed to open source file: %s", [srcPath UTF8String]);
        return nil;
    }

    FILE *destFile = fopen([destPath UTF8String], "wb");
    if (destFile == NULL) {
        NSLog(@"Failed to open destination file: %s", [destPath UTF8String]);
        fclose(srcFile);
        return nil;
    }

    unsigned char cipherBuffer[BUFFER_SIZE + 16];
    unsigned char plainBuffer[BUFFER_SIZE];
    size_t bytesRead;
    unsigned char readAuthTag[16];

    const unsigned char *sharedSecretBytes = [sharedSecret bytes];
    const unsigned char *blockBytes = [block bytes];
    const unsigned char *authTagBytes = [authTag bytes];

    while ((bytesRead = fread(cipherBuffer, 1, BUFFER_SIZE + 16, srcFile)) > 0) {
        if (bytesRead <= 16) {
            NSLog(@"Invalid data read.");
            fclose(srcFile);
            fclose(destFile);
            return nil;
        }
        memcpy(readAuthTag, cipherBuffer + bytesRead - 16, 16);
        if (wc_ChaCha20Poly1305_Decrypt(sharedSecretBytes, blockBytes, NULL, 0, cipherBuffer,
                                        bytesRead - 16, readAuthTag, plainBuffer) != 0) {
            NSLog(@"Decryption failed.");
            fclose(srcFile);
            fclose(destFile);
            return nil;
        }
        fwrite(plainBuffer, 1, bytesRead - 16, destFile);
    }

    NSLog(@"Decryption successful.");

    fclose(srcFile);
    fclose(destFile);

    return destPath;
}


- (NSArray *)byteArrayToWritableArray:(unsigned char *)bytes length:(int)length {
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:length];
    for (int i = 0; i < length; i++) {
        [array addObject:[NSNumber numberWithUnsignedChar:bytes[i]]];
    }
    return array;
}


@end
