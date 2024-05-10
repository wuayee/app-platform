/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:32:40
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_NOTIFY_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_NOTIFY_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __notifyApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *>;
};

/**
 * 通知应用实例变更
 * @param applicationInstance
 */
class notifyApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(__notifyApplicationInstances::InType)> {
public:
    static constexpr const char *GENERIC_ID = "8babfe384fc5452da32676b74ab65986";
    notifyApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(__notifyApplicationInstances::InType)>(
        GENERIC_ID) {}
    explicit notifyApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__notifyApplicationInstances::InType)>(GENERIC_ID, ctx) {}
    ~notifyApplicationInstances() = default;
};
}
}
}
}
}

#endif