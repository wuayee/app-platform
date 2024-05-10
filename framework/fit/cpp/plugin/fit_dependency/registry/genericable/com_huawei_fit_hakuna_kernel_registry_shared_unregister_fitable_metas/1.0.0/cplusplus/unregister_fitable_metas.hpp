/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:41:19
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_UNREGISTER_FITABLE_METAS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SHARED_UNREGISTER_FITABLE_METAS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace shared {
struct __unregisterFitableMetas {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *,
        const Fit::string *>;
};

/**
 * 按应用反注册元数据信息
 * @param applications
 * @param environment
 */
class unregisterFitableMetas : public ::Fit::Framework::ProxyClient<FitCode(__unregisterFitableMetas::InType)> {
public:
    static constexpr const char *GENERIC_ID = "c1761c47a228457fb0256664f7351d3c";
    unregisterFitableMetas() : ::Fit::Framework::ProxyClient<FitCode(__unregisterFitableMetas::InType)>(GENERIC_ID) {}
    explicit unregisterFitableMetas(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__unregisterFitableMetas::InType)>(GENERIC_ID, ctx) {}
    ~unregisterFitableMetas() = default;
};
}
}
}
}
}

#endif
