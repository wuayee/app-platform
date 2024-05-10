/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_SDK_SYSTEM_GET_EXTERNAL_ADDRESSES_H
#define COM_HUAWEI_FIT_SDK_SYSTEM_GET_EXTERNAL_ADDRESSES_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace sdk {
namespace system {
struct __GetExternalAddresses {
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::registry::Address>**>;
};

/**
 * 获取本地所有的对外地址列表。
 *
 * @return 表示本地所有的对外地址列表。
 */

class GetExternalAddresses : public ::Fit::Framework::ProxyClient<FitCode(__GetExternalAddresses::OutType)> {
public:
    static constexpr const char* GENERIC_ID = "f6d9b651ed84417f8e4b27df333555ce";
    GetExternalAddresses() : ::Fit::Framework::ProxyClient<FitCode(__GetExternalAddresses::OutType)>(GENERIC_ID) {}
    ~GetExternalAddresses() = default;
    FitCode operator()(Fit::vector<::fit::registry::Address>** result)
    {
        return ::Fit::Framework::ProxyClient<FitCode(__GetExternalAddresses::OutType)>::operator()(result);
    }
};
}
}
}

#endif
