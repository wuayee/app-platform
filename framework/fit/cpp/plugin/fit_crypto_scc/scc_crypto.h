/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : provide scc crypto.
 * Author       : w00561424
 * Date:        : 2024/03/18
 */
#ifndef SCC_CRYPTO_H
#define SCC_CRYPTO_H
#include <external/fit/runtime/crypto/crypto.hpp>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
#include "scc_client.h"
namespace Fit {
class SccCrypto : public Crypto {
public:
    SccCrypto(string name, shared_ptr<SccClient> client) : name_(std::move(name)), client_(std::move(client))
    {
    }
    const char* GetName() const override
    {
        return name_.c_str();
    }
    FitCode Decrypt(const char* data, uint32_t size, string& result) const override
    {
        if (client_ == nullptr) {
            FIT_LOG_ERROR("Client is null.");
            return FIT_ERR_FAIL;
        }
        string src = string(data, size);
        return client_->Decrypt(src, result);
    }
private:
    string name_ {};
    shared_ptr<SccClient> client_ {};
};
}
#endif
