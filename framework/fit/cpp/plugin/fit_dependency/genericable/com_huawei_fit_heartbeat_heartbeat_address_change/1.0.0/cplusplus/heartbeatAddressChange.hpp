/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HEARTBEAT_HEARTBEATADDRESSCHANGE_H
#define COM_HUAWEI_FIT_HEARTBEAT_HEARTBEATADDRESSCHANGE_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace heartbeat {
struct __heartbeatAddressChange {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::vector<::fit::heartbeat::HeartbeatEvent> *>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool **>;
};


/**
 * FitCode heartbeatAddressChange(
 * const Fit::vector<::fit::heartbeat::HeartbeatEvent> *eventList,
 * bool **result)
 */
class heartbeatAddressChange : public ::Fit::Framework::ProxyClient<FitCode(__heartbeatAddressChange::InType,
    __heartbeatAddressChange::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "df5904820d364204a928c3a3cbaa5512";
    heartbeatAddressChange()
        : ::Fit::Framework::ProxyClient<FitCode(__heartbeatAddressChange::InType, __heartbeatAddressChange::OutType)>(
        GENERIC_ID)
    {}
    ~heartbeatAddressChange() {}
};
}
}

#endif
