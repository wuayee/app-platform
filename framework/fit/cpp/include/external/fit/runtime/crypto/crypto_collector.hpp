/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : crypto collector
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#ifndef EXTERNAL_FIT_RUNTIME_CRYPTO_CRYPTO_COLLECTOR_HPP
#define EXTERNAL_FIT_RUNTIME_CRYPTO_CRYPTO_COLLECTOR_HPP

#include <algorithm>
#include <fit/fit_define.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/list.hpp>

#include "crypto.hpp"

namespace Fit {
/**
 * @brief register crypto object
 *
 * @param val crypto object
 */
bool FIT_PUBLIC_API CryptoRegister(const shared_ptr<Crypto>& val);
/**
 * @brief unregister crypto with crypto object
 *
 * @param val crypto object
 */
void FIT_PUBLIC_API CryptoUnregister(const shared_ptr<Crypto>& val);
/**
 * @brief unregister crypto with name
 *
 * @param name crypto's name
 */
void FIT_PUBLIC_API CryptoUnregister(const char* name);
}
#endif // EXTERNAL_FIT_RUNTIME_CRYPTO_CRYPTO_COLLECTOR_HPP