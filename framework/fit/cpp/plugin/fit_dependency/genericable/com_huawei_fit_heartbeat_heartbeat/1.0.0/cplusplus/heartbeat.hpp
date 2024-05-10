/*
* Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
* Description:
* Author: auto generate by FIT IDL
* Date:
*/

#ifndef COM_HUAWEI_FIT_HEARTBEAT_HEARTBEAT_H
#define COM_HUAWEI_FIT_HEARTBEAT_HEARTBEAT_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_registry_registry_common/1.0.0/cplusplus/registryCommon.hpp>
#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace heartbeat {
struct __heartbeat {
    using InType = ::Fit::Framework::ArgumentsIn<
        const Fit::vector<::fit::heartbeat::BeatInfo> *,
        const ::fit::registry::Address *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode heartbeat(
 *     const Fit::vector<::fit::heartbeat::BeatInfo> *beatInfo,
 *     const ::fit::registry::Address *address,
 *     bool **result)
 */
class heartbeat : public ::Fit::Framework::ProxyClient<FitCode(__heartbeat::InType, __heartbeat::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "e12fd1c57fd84f50a673d93d13074082";
    heartbeat() : ::Fit::Framework::ProxyClient<FitCode(__heartbeat::InType, __heartbeat::OutType)>(GENERIC_ID) {}
};
}
}

#endif