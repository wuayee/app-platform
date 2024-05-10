/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#ifndef COM_HUAWEI_FIT_HEARTBEAT_LEAVE_H
#define COM_HUAWEI_FIT_HEARTBEAT_LEAVE_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace heartbeat {
struct __leave {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::heartbeat::BeatInfo> *,
        const ::fit::registry::Address *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode leave(
 *     const Fit::vector<::fit::heartbeat::BeatInfo> *beatInfo,
 *     const ::fit::registry::Address *address,
 *     bool **result)
 */
class leave : public ::Fit::Framework::ProxyClient<FitCode(__leave::InType, __leave::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "67e6370725df427ebab9a6a6f1ada60c";
    leave() : ::Fit::Framework::ProxyClient<FitCode(__leave::InType, __leave::OutType)>(GENERIC_ID) {}
};
}
}

#endif