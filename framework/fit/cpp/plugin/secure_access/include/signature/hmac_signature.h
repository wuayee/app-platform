/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef HMAC_SIGNATURE_H
#define HMAC_SIGNATURE_H
#include <include/repo/auth_key_repo.h>
#include "signature.h"
namespace Fit {
class HmacSignature : public Signature {
public:
    HmacSignature(AuthKeyRepoPtr authKeyRepo);
    string Sign(const string& ak, const string& timestamp);
    bool Verify(const string& ak, const string& timestamp, const string& signature);
private:
    AuthKeyRepoPtr authKeyRepo_ {};
};
}
#endif