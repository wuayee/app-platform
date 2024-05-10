/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:18:50
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_UNREGISTER_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_UNREGISTER_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __unregisterApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *,
        const Fit::string *>;
};

/**
 * 反注册应用实例信息
 * @param applications
 * @param workerId
 */
class unregisterApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(
    __unregisterApplicationInstances::InType)> {
public:
    static constexpr const char *GENERIC_ID = "648d0b6811024fc99d956008df5368c5";
    unregisterApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(
        __unregisterApplicationInstances::InType)>(GENERIC_ID) {}
    explicit unregisterApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__unregisterApplicationInstances::InType)>(GENERIC_ID, ctx) {}
    ~unregisterApplicationInstances() = default;
};
}
}
}
}
}

#endif