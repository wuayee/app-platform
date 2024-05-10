/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_SETLOCALADDRESSES_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_SETLOCALADDRESSES_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace sdk {
namespace system {
struct __setLocalAddresses {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::vector<::fit::registry::Address> *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};

/**
 * 设置本地所有的地址列表。
 *
 * @param addresses 表示本地所有的地址列表。
 * @return 设置成功，返回 {@code true}，否则，返回 {@code false}。
 */
class setLocalAddresses
    : public ::Fit::Framework::ProxyClient<FitCode(__setLocalAddresses::InType, __setLocalAddresses::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "aad2ec78078047d6992fa68f9d91844d";
    setLocalAddresses()
        : ::Fit::Framework::ProxyClient<FitCode(__setLocalAddresses::InType, __setLocalAddresses::OutType)>(GENERIC_ID)
    {}
    ~setLocalAddresses() = default;
};
}
}
}

#endif
