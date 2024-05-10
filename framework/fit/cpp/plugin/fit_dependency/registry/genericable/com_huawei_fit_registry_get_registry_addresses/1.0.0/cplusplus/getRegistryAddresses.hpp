/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_REGISTRY_GETREGISTRYADDRESSES_H
#define COM_HUAWEI_FIT_REGISTRY_GETREGISTRYADDRESSES_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_instance/1.0.0/cplusplus/FitableInstance.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace registry {
struct __getRegistryAddresses {
    using OutType = ::Fit::Framework::ArgumentsOut<::fit::hakuna::kernel::registry::shared::FitableInstance**>;
};

/**
 * 获取注册中心的地址列表
 * @return
 */

/**
 * FitCode getRegistryAddresses(Fit::vector<::fit::registry::Address> **result)
 */
class getRegistryAddresses : public ::Fit::Framework::ProxyClient<FitCode(__getRegistryAddresses::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "49612be00e38424cac2e8ea812936cb4";
    getRegistryAddresses() : ::Fit::Framework::ProxyClient<FitCode(__getRegistryAddresses::OutType)>(GENERIC_ID) {}
    ~getRegistryAddresses() = default;
};
}
}

#endif
