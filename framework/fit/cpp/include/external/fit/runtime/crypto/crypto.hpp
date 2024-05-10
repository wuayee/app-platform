/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : crypto
 * Author       : songyongtan
 * Create       : 2023-08-15
 * Notes:       :
 */

#ifndef FIT_CRYPTO_HPP
#define FIT_CRYPTO_HPP

#include <fit/stl/string.hpp>
#include <fit/fit_code.h>

namespace Fit {
class Crypto {
public:
    virtual ~Crypto() = default;
    /**
     * @brief the crypto's name
     *
     * @return const char*
     */
    virtual const char* GetName() const = 0;
    /**
     * @brief decrypt the content
     *
     * @param data encrypted content
     * @param size encrypted content size
     * @param result decrypted result
     * @return FitCode
     */
    virtual FitCode Decrypt(const char* data, uint32_t size, string& result) const = 0;
};
}

#endif