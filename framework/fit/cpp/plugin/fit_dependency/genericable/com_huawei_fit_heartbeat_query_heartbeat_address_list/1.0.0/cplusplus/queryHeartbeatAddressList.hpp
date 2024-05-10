/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HEARTBEAT_QUERYHEARTBEATADDRESSLIST_H
#define COM_HUAWEI_FIT_HEARTBEAT_QUERYHEARTBEATADDRESSLIST_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace heartbeat {
struct __queryHeartbeatAddressList {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string *>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<::fit::registry::Address> **>;
};


/**
 * FitCode queryHeartbeatAddressList(
 * const Fit::string *sceneType,
 * Fit::vector<::fit::registry::Address> **result)
 */
class queryHeartbeatAddressList : public ::Fit::Framework::ProxyClient<FitCode(__queryHeartbeatAddressList::InType,
    __queryHeartbeatAddressList::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "2bd87d1847e94811aec5054f374792dc";
    queryHeartbeatAddressList()
        : ::Fit::Framework::ProxyClient<FitCode(__queryHeartbeatAddressList::InType,
        __queryHeartbeatAddressList::OutType)>(GENERIC_ID)
    {}
    ~queryHeartbeatAddressList() {}
};
}
}

#endif
