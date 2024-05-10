/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  : 过滤从注册中心获取到的目标应用实例列表
 * Author       : 李鑫 l00498867
 * Date         : 2021-12-20 10:07:03
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_LOADBALANCE_FILTERV3_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_LOADBALANCE_FILTERV3_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_shared_Fitable/1.0.0/cplusplus/Fitable.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace loadbalance {
struct __filterV3 {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::hakuna::kernel::shared::Fitable *,
        const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *>;
    using OutType =
        ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **>;
};

/**
 * 过滤从注册中心获取到的目标应用实例列表。
 *
 * @param fitable 表示fitable的信息。
 * @param instances 表示待过滤的应用实例列表。
 * @return 过滤后的应用实例。
 */
class filterV3 : public ::Fit::Framework::ProxyClient<FitCode(__filterV3::InType, __filterV3::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "5adf2d6cc7fa4f47ba1dd2576f660730";
    filterV3() : ::Fit::Framework::ProxyClient<FitCode(__filterV3::InType, __filterV3::OutType)>(GENERIC_ID) {}
    explicit filterV3(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__filterV3::InType, __filterV3::OutType)>(GENERIC_ID, ctx)
    {}
    ~filterV3() = default;
};
}
}
}
}

#endif