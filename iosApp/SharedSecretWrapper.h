#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface SharedSecretWrapper : NSObject

+ (NSArray

<NSString *> *)getSharedSecret:(NSString *)
publicKeyBase64;

@end

NS_ASSUME_NONNULL_END
