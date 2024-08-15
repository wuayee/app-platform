/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_REFRESH_TOKEN_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_REFRESH_TOKEN_G_H

#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/vector.hpp>
#include <component/com_huawei_fit_secure_access_token_info/1.0.0/cplusplus/TokenInfo.hpp>

namespace fit {
namespace secure {
namespace access {
struct __RefreshToken {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<fit::secure::access::TokenInfo> **>;
};

class RefreshToken : public ::Fit::Framework::ProxyClient<FitCode(__RefreshToken::InType, __RefreshToken::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "81d53be622ef980c4df8fc2cd7b5ce79";
    RefreshToken() : ::Fit::Framework::ProxyClient<FitCode(__RefreshToken::InType, __RefreshToken::OutType)>(GENERIC_ID)
    {}
    explicit RefreshToken(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__RefreshToken::InType, __RefreshToken::OutType)>(GENERIC_ID, ctx) {}
    ~RefreshToken() = default;
};
}
}
}

#endif