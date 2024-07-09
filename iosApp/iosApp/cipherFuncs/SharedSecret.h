#ifndef SHARED_SECRET_H
#define SHARED_SECRET_H

#include <oqs/oqs.h>

typedef struct {
    unsigned char *ciphertext;
    unsigned char *shared_secret;
} EncapsulationResult;

EncapsulationResult encapsulate_with_public_key(unsigned char *public_key);

#endif // SHARED_SECRET_H
