/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:14:36
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_REGISTER_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_REGISTER_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __registerApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *>;
};

/**
 * 注册应用实例信息
 *
 * @param applicationInstances
 */
class registerApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(
    __registerApplicationInstances::InType)> {
public:
    static constexpr const char *GENERIC_ID = "388db940888f4bacbe1edb1c254bef68";
    registerApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(
        __registerApplicationInstances::InType)>(GENERIC_ID) {}
    explicit registerApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__registerApplicationInstances::InType)>(GENERIC_ID, ctx) {}
    ~registerApplicationInstances() = default;
};
}
}
}
}
}

#endif