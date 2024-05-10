/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HEARTBEAT_ONLINEHEARTBEAT_H
#define COM_HUAWEI_FIT_HEARTBEAT_ONLINEHEARTBEAT_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
namespace fit {
namespace heartbeat {
struct __onlineHeartbeat {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::heartbeat::BeatInfo *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode onlineHeartbeat(
 * const ::fit::heartbeat::BeatInfo *beatInfo,
 * bool **result)
 */
class onlineHeartbeat
    : public ::Fit::Framework::ProxyClient<FitCode(__onlineHeartbeat::InType, __onlineHeartbeat::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "bf2e1029e8844fe9afb698affb95d39c";
    onlineHeartbeat()
        : ::Fit::Framework::ProxyClient<FitCode(__onlineHeartbeat::InType, __onlineHeartbeat::OutType)>(GENERIC_ID)
    {}
    ~onlineHeartbeat() {}
};
}
}

#endif
