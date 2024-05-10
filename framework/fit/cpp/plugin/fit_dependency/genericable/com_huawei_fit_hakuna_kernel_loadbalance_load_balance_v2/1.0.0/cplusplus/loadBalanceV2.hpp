/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  : 负载均衡接口
 * Author       : 李鑫 l00498867
 * Date         : 2021-12-21 11:28:05
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_LOADBALANCE_LOADBALANCEV2_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_LOADBALANCE_LOADBALANCEV2_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace loadbalance {
struct __loadBalanceV2 {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::hakuna::kernel::shared::Fitable *,
        const ::fit::hakuna::kernel::registry::shared::ApplicationInstance *,
        const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *>;
    using OutType = ::Fit::Framework::ArgumentsOut<::fit::hakuna::kernel::registry::shared::ApplicationInstance **>;
};

/**
 * 负载均衡接口。
 *
 * @param fitable 表示fitable信息。
 * @param sourceInstance 表示调用者的应用实例。
 * @param targetInstances 表示被调用方的应用实例列表。
 * @return 选中的应用实例。
 */
class loadBalanceV2 : public ::Fit::Framework::ProxyClient<FitCode(__loadBalanceV2::InType, __loadBalanceV2::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "ccd9339353aa4802a8f9ae59e1a6ced6";
    loadBalanceV2()
        : ::Fit::Framework::ProxyClient<FitCode(__loadBalanceV2::InType, __loadBalanceV2::OutType)>(GENERIC_ID)
    {}
    explicit loadBalanceV2(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__loadBalanceV2::InType, __loadBalanceV2::OutType)>(GENERIC_ID, ctx)
    {}
    ~loadBalanceV2() = default;
};
}
}
}
}

#endif