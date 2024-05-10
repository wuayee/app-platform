/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : crypto manager
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#ifndef FIT_CRYPTO_MANAGER_HPP
#define FIT_CRYPTO_MANAGER_HPP

#include <fit/runtime/crypto/crypto.hpp>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/stl/mutex.hpp>

namespace Fit {
class CryptoManager {
public:
    ~CryptoManager();
    static CryptoManager& Instance();
    bool Add(const shared_ptr<Crypto>& crypto);
    void Remove(const shared_ptr<Crypto>& crypto);
    void Remove(const char* name);
    shared_ptr<Crypto> Get(const string& name);

private:
    CryptoManager() = default;
    CryptoManager(const CryptoManager&) = delete;
    CryptoManager(CryptoManager&&) = delete;
    CryptoManager& operator=(const CryptoManager&) = delete;
    CryptoManager& operator=(CryptoManager&&) = delete;
    mutex mt_;
    map<string, shared_ptr<Crypto>> cryptos_;
};
}

#endif