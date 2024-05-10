/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implements for crypto manager
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#include <fit/internal/runtime/crypto/crypto_manager.hpp>
#include <fit/fit_log.h>

namespace Fit {
CryptoManager& CryptoManager::Instance()
{
    static CryptoManager instance;
    return instance;
}

CryptoManager::~CryptoManager()
{
    cryptos_.clear();
}

bool CryptoManager::Add(const shared_ptr<Crypto>& crypto)
{
    lock_guard<mutex> guard(mt_);
    if (cryptos_.count(crypto->GetName()) != 0) {
        FIT_LOG_ERROR("The crypto is already exist, name=%s.", crypto->GetName());
        return false;
    }
    cryptos_[crypto->GetName()] = crypto;
    FIT_LOG_INFO("The crypto is added, name=%s.", crypto->GetName());
    return true;
}

void CryptoManager::Remove(const shared_ptr<Crypto>& crypto)
{
    Remove(crypto->GetName());
}

void CryptoManager::Remove(const char* name)
{
    lock_guard<mutex> guard(mt_);
    auto iter = cryptos_.find(name);
    if (iter == cryptos_.end()) {
        FIT_LOG_WARN("The crypto is not exist, name=%s.", name);
        return;
    }
    cryptos_.erase(iter);
    FIT_LOG_INFO("The crypto is removed, name=%s.", name);
}

shared_ptr<Crypto> CryptoManager::Get(const string& name)
{
    lock_guard<mutex> guard(mt_);
    auto iter = cryptos_.find(name);
    if (iter == cryptos_.end()) {
        FIT_LOG_WARN("The crypto is not exist, name=%s.", name.c_str());
        return nullptr;
    }
    return iter->second;
}
} // LCOV_EXCL_LINE