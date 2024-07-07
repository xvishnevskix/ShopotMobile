//
//  WolfsslModule.h
//  iosApp
//
//  Created by Aslanbek Abubakarov on 05.07.2024.
//

#ifndef WolfsslModule_h
#define WolfsslModule_h

#import <Foundation/Foundation.h>

@interface WolfsslModule : NSObject

typedef struct {
    int length;
    unsigned char *cipher;
    unsigned char *block;
    unsigned char *authTag;
} EncapsulationMessageResult;

EncapsulationMessageResult
encapsulate_with_chacha(unsigned char *message, unsigned char *shared_secret);

- (NSArray *)byteArrayToWritableArray:(unsigned char *)bytes length:(int)length;

+ (NSData *)decryptWithCipher:(NSData *)cipher block:(NSData *)block authTagData:(NSData *)authTagData sharedSecret:(NSData *)sharedSecret;

@end

#endif /* WolfsslModule_h */
