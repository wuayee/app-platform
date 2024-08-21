/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_SECURE_ACCESS_IS_AUTHORIZED_G_H
#define COM_HUAWEI_FIT_SECURE_ACCESS_IS_AUTHORIZED_G_H

#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace secure {
namespace access {

struct Permission {
    ::fit::hakuna::kernel::shared::Fitable* fitable;
};
struct __IsAuthorized {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *, const fit::secure::access::Permission *>;
};

class IsAuthorized : public ::Fit::Framework::ProxyClient<FitCode(__IsAuthorized::InType)> {
public:
    static constexpr const char *GENERIC_ID = "2d69e74d58e4acbb14baf961a3975185";
    IsAuthorized() : ::Fit::Framework::ProxyClient<FitCode(__IsAuthorized::InType)>(GENERIC_ID)
    {}
    explicit IsAuthorized(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__IsAuthorized::InType)>(GENERIC_ID, ctx) {}
    ~IsAuthorized() = default;
};
}
}
}

#endif