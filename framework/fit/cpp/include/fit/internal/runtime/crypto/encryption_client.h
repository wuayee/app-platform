/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2024/03/18
 * Notes:       :
 */
#ifndef ENCRYPTION_CLIENT_H
#define ENCRYPTION_CLIENT_H
#include <fit/stl/string.hpp>
namespace Fit {
class EncryptionClient {
public:
    virtual ~EncryptionClient() = default;
    virtual int32_t Init() = 0;
    virtual int32_t Encrypt(const Fit::string& src, Fit::string& dst) = 0;
    virtual int32_t Decrypt(const Fit::string& src, Fit::string& dst) = 0;
};
}
#endif