/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-10-17 11:39:36
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECK_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_CHECK_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Check_element/1.0.0/cplusplus/CheckElement.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __check {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::CheckElement> *>;
};

/**
 * check GENERIC_ID
 * @param checkElements
 */
class check : public ::Fit::Framework::ProxyClient<FitCode(__check::InType)> {
public:
    static constexpr const char *GENERIC_ID = "dd43f8d55b094bc687a0e710c9817cef";
    check() : ::Fit::Framework::ProxyClient<FitCode(__check::InType)>(GENERIC_ID) {}
    explicit check(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__check::InType)>(GENERIC_ID, ctx) {}
    ~check() = default;
};
}
}
}
}
}

#endif
