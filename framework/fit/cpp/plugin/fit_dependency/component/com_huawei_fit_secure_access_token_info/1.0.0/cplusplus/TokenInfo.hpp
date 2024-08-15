/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_TOKEN_INFO_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_TOKEN_INFO_G_H
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/bits.hpp>
#include <fit/memory/fit_base.hpp>

namespace fit {
namespace secure {
namespace access {
struct TokenInfo : public FitBase {
    Fit::string token;
    int64_t timeout {}; // 超时时间，单位秒
    Fit::string type; // 分为 refresh_token 和 access_token
    Fit::string status; // normal 和 invalid 两个状态

    bool HasToken() const noexcept
    {
        return hasFields_[_FieldIndex::token];
    }

    bool HasTimeout() const noexcept
    {
        return hasFields_[_FieldIndex::timeout];
    }

    bool HasType() const noexcept
    {
        return hasFields_[_FieldIndex::type];
    }

    bool HasStatus() const noexcept
    {
        return hasFields_[_FieldIndex::status];
    }

    const Fit::string &GetToken() const
    {
        if (!HasToken()) {
            FIT_THROW_INVALID_ARGUMENT("no token setted");
        }
        return token;
    }

    int64_t GetTimeout() const
    {
        if (!HasTimeout()) {
            FIT_THROW_INVALID_ARGUMENT("no token timeout setted");
        }
        return timeout;
    }

    const Fit::string &GetType() const
    {
        if (!HasType()) {
            FIT_THROW_INVALID_ARGUMENT("no token type setted");
        }
        return type;
    }

    const Fit::string &GetStatus() const
    {
        if (!HasStatus()) {
            FIT_THROW_INVALID_ARGUMENT("no token status setted");
        }
        return status;
    }

    void SetToken(const Fit::string& val)
    {
        token = val;
        hasFields_[_FieldIndex::token] = true;
    }

    void SetTimeout(int64_t val)
    {
        timeout = val;
        hasFields_[_FieldIndex::timeout] = true;
    }

    void SetType(const Fit::string& val)
    {
        type = val;
        hasFields_[_FieldIndex::type] = true;
    }

    void SetStatus(const Fit::string& val)
    {
        status = val;
        hasFields_[_FieldIndex::status] = true;
    }

    Fit::string &MutableToken()
    {
        hasFields_[_FieldIndex::token] = true;
        return token;
    }

    int64_t &MutableTimeout()
    {
        hasFields_[_FieldIndex::timeout] = true;
        return timeout;
    }

    Fit::string &MutableType()
    {
        hasFields_[_FieldIndex::type] = true;
        return type;
    }

    Fit::string &MutableStatus()
    {
        hasFields_[_FieldIndex::status] = true;
        return status;
    }

    void ClearToken()
    {
        hasFields_[_FieldIndex::token] = false;
        token.clear();
    }

    void ClearTimeout()
    {
        hasFields_[_FieldIndex::timeout] = false;
        timeout = int64_t {};
    }

    void ClearType()
    {
        hasFields_[_FieldIndex::type] = false;
        type.clear();
    }

    void ClearStatus()
    {
        hasFields_[_FieldIndex::status] = false;
        status.clear();
    }

    void Reset()
    {
        ClearToken();
        ClearTimeout();
        ClearType();
        ClearStatus();
    }
private:
    static constexpr uint32_t FIELD_COUNT = 4;
    ::Fit::Bits<FIELD_COUNT> hasFields_ { true };
    struct _FieldIndex {
        static constexpr uint32_t token = 0;
        static constexpr uint32_t timeout = 1;
        static constexpr uint32_t type = 2;
        static constexpr uint32_t status = 3;
    };
};
}
}
}

#endif