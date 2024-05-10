/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : 王攀博 w00561424
 * Date         : 2023-09-06 15:20:10
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_APPLICATION_INSTANCES_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_APPLICATION_INSTANCES_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __queryApplicationInstances {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *>;
    using OutType =
        ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **>;
};

/**
 * 查询应用实例信息
 * @param applications
 * @return
 */
class queryApplicationInstances : public ::Fit::Framework::ProxyClient<FitCode(
    __queryApplicationInstances::InType, __queryApplicationInstances::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "fbc49b115f784676b44e5fbdb664b0cc";
    queryApplicationInstances() : ::Fit::Framework::ProxyClient<FitCode(
        __queryApplicationInstances::InType, __queryApplicationInstances::OutType)>(GENERIC_ID) {}
    explicit queryApplicationInstances(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(
            __queryApplicationInstances::InType, __queryApplicationInstances::OutType)>(GENERIC_ID, ctx) {}
    ~queryApplicationInstances() = default;
};
}
}
}
}
}

#endif