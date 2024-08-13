/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_APPLY_TOKEN_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_APPLY_TOKEN_G_H
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/bits.hpp>
#include <fit/memory/fit_base.hpp>

namespace fit {
namespace secure {
namespace access {
struct TokenInfo : public FitBase {
    Fit::string accessToken;
    int64_t timeout {}; // 超时时间，单位秒
    Fit::string refreshToken;

    bool HasAccessToken() const noexcept
    {
        return hasFields_[_FieldIndex::accessToken];
    }

    bool HasTimeout() const noexcept
    {
        return hasFields_[_FieldIndex::timeout];
    }

    bool HasRefreshToken() const noexcept
    {
        return hasFields_[_FieldIndex::refreshToken];
    }

    const Fit::string &GetAccessToken() const
    {
        if (!HasAccessToken()) {
            FIT_THROW_INVALID_ARGUMENT("no access token setted");
        }
        return accessToken;
    }

    int64_t GetTimeout() const
    {
        if (!HasTimeout()) {
            FIT_THROW_INVALID_ARGUMENT("no timeout setted");
        }
        return timeout;
    }

    const Fit::string &GetRefreshToken() const
    {
        if (!HasRefreshToken()) {
            FIT_THROW_INVALID_ARGUMENT("no refresh token setted");
        }
        return refreshToken;
    }

    void SetAccessToken(const Fit::string& val)
    {
        accessToken = val;
        hasFields_[_FieldIndex::accessToken] = true;
    }

    void SetTimeout(int64_t val)
    {
        timeout = val;
        hasFields_[_FieldIndex::timeout] = true;
    }

    void SetRefreshToken(const Fit::string& val)
    {
        refreshToken = val;
        hasFields_[_FieldIndex::refreshToken] = true;
    }

    Fit::string &MutableAccessToken()
    {
        hasFields_[_FieldIndex::accessToken] = true;
        return accessToken;
    }

    int64_t &MutableTimeout()
    {
        hasFields_[_FieldIndex::timeout] = true;
        return timeout;
    }

    Fit::string &MutableRefreshToken()
    {
        hasFields_[_FieldIndex::refreshToken] = true;
        return refreshToken;
    }

    void ClearAccessToken()
    {
        hasFields_[_FieldIndex::accessToken] = false;
        accessToken.clear();
    }

    void ClearTimeout()
    {
        hasFields_[_FieldIndex::timeout] = false;
        timeout = int64_t {};
    }

    void ClearRefreshToken()
    {
        hasFields_[_FieldIndex::refreshToken] = false;
        refreshToken.clear();
    }

    void Reset()
    {
        ClearAccessToken();
        ClearTimeout();
        ClearRefreshToken();
    }

private:
    static constexpr uint32_t FIELD_COUNT = 3;
    ::Fit::Bits<FIELD_COUNT> hasFields_ { true };
    struct _FieldIndex {
        static constexpr uint32_t accessToken = 0;
        static constexpr uint32_t timeout = 1;
        static constexpr uint32_t refreshToken = 2;
    };
};
}
}
}

#endif