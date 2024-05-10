/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HEARTBEAT_OFFLINEHEARTBEAT_H
#define COM_HUAWEI_FIT_HEARTBEAT_OFFLINEHEARTBEAT_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
namespace fit {
namespace heartbeat {
struct __offlineHeartbeat {
    using InType = ::Fit::Framework::ArgumentsIn<const ::fit::heartbeat::BeatInfo *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode offlineHeartbeat(
 * const ::fit::heartbeat::BeatInfo *beatInfo,
 * bool **result)
 */
class offlineHeartbeat
    : public ::Fit::Framework::ProxyClient<FitCode(__offlineHeartbeat::InType, __offlineHeartbeat::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "18e47e184fd44c70af63eec3227bee2e";
    offlineHeartbeat()
        : ::Fit::Framework::ProxyClient<FitCode(__offlineHeartbeat::InType, __offlineHeartbeat::OutType)>(GENERIC_ID)
    {}
    ~offlineHeartbeat() {}
};
}
}

#endif
