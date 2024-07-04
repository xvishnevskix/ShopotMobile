#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef struct {
    unsigned char *ciphertext;
    unsigned char *shared_secret;
} EncapsulationResult;

EncapsulationResult encapsulate_with_public_key(unsigned char *public_key);

extern const int OQS_KEM_kyber_768_length_ciphertext;
extern const int OQS_KEM_kyber_768_length_shared_secret;

@interface SharedSecretModule : NSObject

+ (NSArray

<NSString *> *)getSharedSecret:(NSString *)
publicKeyBase64;

@end

NS_ASSUME_NONNULL_END
