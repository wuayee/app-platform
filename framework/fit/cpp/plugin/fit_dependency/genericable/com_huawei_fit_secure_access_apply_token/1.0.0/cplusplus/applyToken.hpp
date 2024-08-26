/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_APPLY_TOKEN_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_APPLY_TOKEN_G_H

#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/vector.hpp>
#include <component/com_huawei_fit_secure_access_token_info/1.0.0/cplusplus/TokenInfo.hpp>

namespace fit {
namespace secure {
namespace access {
struct __ApplyToken {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *, const Fit::string *, const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<fit::secure::access::TokenInfo>**>;
};

class ApplyToken : public ::Fit::Framework::ProxyClient<FitCode(__ApplyToken::InType, __ApplyToken::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "matata.registry.secure-access.apply-token";
    ApplyToken() : ::Fit::Framework::ProxyClient<FitCode(__ApplyToken::InType, __ApplyToken::OutType)>(GENERIC_ID) {}
    explicit ApplyToken(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__ApplyToken::InType, __ApplyToken::OutType)>(GENERIC_ID, ctx) {}
    ~ApplyToken() = default;
};
}
}
}

#endif