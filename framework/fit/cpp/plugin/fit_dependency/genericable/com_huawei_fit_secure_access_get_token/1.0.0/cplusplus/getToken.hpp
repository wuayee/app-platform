/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_GET_TOKEN_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_GET_TOKEN_G_H

#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace secure {
namespace access {
struct __GetToken {
    using InType = ::Fit::Framework::ArgumentsIn<const bool*>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::string**>;
};

class GetToken : public ::Fit::Framework::ProxyClient<FitCode(__GetToken::InType, __GetToken::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "d1cf7d396a9d4c999d08f0e2205fb71b";
    GetToken() : ::Fit::Framework::ProxyClient<FitCode(__GetToken::InType, __GetToken::OutType)>(GENERIC_ID)
    {}
    explicit GetToken(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__GetToken::InType, __GetToken::OutType)>(GENERIC_ID, ctx) {}
    ~GetToken() = default;
};
}
}
}

#endif