/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_GETLOCALADDRESSES_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_GETLOCALADDRESSES_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace sdk {
namespace system {
struct __getLocalAddresses {
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::registry::Address> **>;
};

/**
 * 获取本地所有的地址列表。
 *
 * @return 表示本地所有的地址列表。
 */

/**
 * FitCode getLocalAddresses(Fit::vector<::fit::registry::Address> **result)
 */
class getLocalAddresses : public ::Fit::Framework::ProxyClient<FitCode(__getLocalAddresses::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "50b44bc61b674a73882e88fafda7c2f7";
    getLocalAddresses() : ::Fit::Framework::ProxyClient<FitCode(__getLocalAddresses::OutType)>(GENERIC_ID) {}
    ~getLocalAddresses() = default;
};
}
}
}

#endif
