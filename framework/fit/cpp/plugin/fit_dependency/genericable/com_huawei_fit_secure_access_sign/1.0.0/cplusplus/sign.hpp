/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_SIGN_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_SIGN_G_H

#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace secure {
namespace access {
struct __Sign {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *, const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::string**>;
};

class Sign : public ::Fit::Framework::ProxyClient<FitCode(__Sign::InType, __Sign::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "matata.registry.secure-access.sign";
    Sign() : ::Fit::Framework::ProxyClient<FitCode(__Sign::InType, __Sign::OutType)>(GENERIC_ID) {}
    explicit Sign(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__Sign::InType, __Sign::OutType)>(GENERIC_ID, ctx) {}
    ~Sign() = default;
};
}
}
}

#endif