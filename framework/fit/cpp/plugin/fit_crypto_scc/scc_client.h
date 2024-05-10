/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide scc client.
 * Author       : w00561424
 * Date:        : 2024/03/18
 */
#ifndef SCC_CLIENT_H
#define SCC_CLIENT_H
#include <fit/internal/runtime/crypto/encryption_client.h>
#include <fit/stl/memory.hpp>
namespace Fit {
class SccClient : public EncryptionClient {
public:
    explicit SccClient(const string& configFile) : configFile_(configFile)
    {
    }
    ~SccClient() = default;
    int32_t Init() override;
    int32_t Encrypt(const Fit::string& src, Fit::string& dst) override;
    int32_t Decrypt(const Fit::string& src, Fit::string& dst) override;
    static Fit::shared_ptr<SccClient> Create(const string& configFile);
private:
    string configFile_ {};
};
}
#endif