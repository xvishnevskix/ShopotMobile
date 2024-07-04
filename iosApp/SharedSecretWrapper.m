#import "SharedSecretWrapper.h"
#import "SharedSecret.h"
#include <oqs/oqs.h>

@implementation SharedSecretWrapper

+ (NSArray

<NSString *> *)getSharedSecret:(NSString *)publicKeyBase64 {
    return [SharedSecretModule getSharedSecret:publicKeyBase64];
}

@end
