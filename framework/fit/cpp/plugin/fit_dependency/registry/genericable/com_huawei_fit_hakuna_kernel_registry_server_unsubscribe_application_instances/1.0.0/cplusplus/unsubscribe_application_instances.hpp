/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:23:48
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_UNSUBSCRIBE_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_UNSUBSCRIBE_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __unsubscribeApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *,
        const Fit::string *,
        const Fit::string *>;
};

/**
 * 反订阅某个worker上的应用
 * @param applications
 * @param workerId
 * @param callbackId
 */
class unsubscribeApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(
    __unsubscribeApplicationInstances::InType)> {
public:
    static constexpr const char *GENERIC_ID = "d85724eabdb44e18adaa774c825a1651";
    unsubscribeApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(
        __unsubscribeApplicationInstances::InType)>(GENERIC_ID) {}
    explicit unsubscribeApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__unsubscribeApplicationInstances::InType)>(GENERIC_ID, ctx) {}
    ~unsubscribeApplicationInstances() = default;
};
}
}
}
}
}

#endif