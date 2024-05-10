/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_GETREGISTRYMATCHEDADDRESS_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_GETREGISTRYMATCHEDADDRESS_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
namespace fit {
namespace sdk {
namespace system {
struct __getRegistryMatchedAddress {
    using OutType = ::Fit::Framework::ArgumentsOut<::fit::registry::Address **>;
};

/**
 * 获取本地和注册中心通信协议匹配的地址。
 *
 * @return 表示本地和注册中心通信协议匹配的地址。
 */
class getRegistryMatchedAddress : public ::Fit::Framework::ProxyClient<FitCode(__getRegistryMatchedAddress::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "a9e5d506a8b049c7beab14d3a9b40cbf";
    getRegistryMatchedAddress()
        : ::Fit::Framework::ProxyClient<FitCode(__getRegistryMatchedAddress::OutType)>(GENERIC_ID)
    {}
    ~getRegistryMatchedAddress() = default;
};
}
}
}

#endif
