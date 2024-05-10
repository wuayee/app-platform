/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : implement for crypto collector
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#include <fit/runtime/crypto/crypto_collector.hpp>
#include <fit/internal/runtime/crypto/crypto_manager.hpp>

namespace Fit {
bool FIT_PUBLIC_API CryptoRegister(const shared_ptr<Crypto>& val)
{
    return CryptoManager::Instance().Add(val);
}
void FIT_PUBLIC_API CryptoUnregister(const shared_ptr<Crypto>& val)
{
    CryptoManager::Instance().Remove(val);
}
void FIT_PUBLIC_API CryptoUnregister(const char* name)
{
    CryptoManager::Instance().Remove(name);
}
} // LCOV_EXCL_LINE