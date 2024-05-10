/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_SETREGISTRYMATCHEDADDRESS_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_SETREGISTRYMATCHEDADDRESS_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
namespace fit {
namespace sdk {
namespace system {
struct __setRegistryMatchedAddress {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::registry::Address *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};

/**
 * 设置本地和注册中心通信协议匹配的地址。
 *
 * @param address 表示本地和注册中心通信协议匹配的地址。
 * @return 设置成功，返回 {@code true}，否则，返回 {@code false}。
 */
class setRegistryMatchedAddress : public ::Fit::Framework::ProxyClient<FitCode(__setRegistryMatchedAddress::InType,
    __setRegistryMatchedAddress::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "ec06e225a22047a1bb329385d63da086";
    setRegistryMatchedAddress()
        : ::Fit::Framework::ProxyClient<FitCode(__setRegistryMatchedAddress::InType,
        __setRegistryMatchedAddress::OutType)>(GENERIC_ID)
    {}
    ~setRegistryMatchedAddress() = default;
};
}
}
}

#endif
