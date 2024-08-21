/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date:        : 2024/05/20
 */
#ifndef SIGNATURE_H
#define SIGNATURE_H
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <include/repo/auth_key_repo.h>
namespace Fit {
class Signature {
public:
    virtual ~Signature() = default;
    virtual string Sign(const string& ak, const string& timestamp) = 0;
    virtual bool Verify(const string& ak, const string& timestamp, const string& signature) = 0;
};

using SignaturePtr = Fit::shared_ptr<Signature>;
class SignatureFactory {
public:
    static SignaturePtr Create(AuthKeyRepoPtr authKeyRepo);
};
}
#endif