/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SETREGISTRYADDRESS_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_SETREGISTRYADDRESS_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __setRegistryAddress {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::registry::Address *>;
};

/**
 * 设置注册中心地址
 *
 * @param address
 * @return
 */

/**
 * FitCode setRegistryAddress(
 * const ::fit::registry::Address *address)
 */
class setRegistryAddress : public ::Fit::Framework::ProxyClient<FitCode(__setRegistryAddress::InType)> {
public:
    static constexpr const char *GENERIC_ID = "5dcd7a4dc1f548f4ad4ecb0a845639d2";
    setRegistryAddress() : ::Fit::Framework::ProxyClient<FitCode(__setRegistryAddress::InType)>(GENERIC_ID) {}
    ~setRegistryAddress() {}
};
}
}
}
}
}

#endif
