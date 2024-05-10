/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:22:02
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SUBSCRIBE_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SUBSCRIBE_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __subscribeApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *,
        const Fit::string *,
        const Fit::string *>;
    using OutType =
        ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **>;
};

/**
 * 订阅应用实例信息
 * @param applications
 * @param workerId
 * @param callbackId
 * @return
 */
class subscribeApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(
    __subscribeApplicationInstances::InType, __subscribeApplicationInstances::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "0fc05dcf98304975b030865f51a7a894";
    subscribeApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(
        __subscribeApplicationInstances::InType, __subscribeApplicationInstances::OutType)>(GENERIC_ID) {}
    explicit subscribeApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(
            __subscribeApplicationInstances::InType, __subscribeApplicationInstances::OutType)>(GENERIC_ID, ctx) {}
    ~subscribeApplicationInstances() = default;
};
}
}
}
}
}

#endif