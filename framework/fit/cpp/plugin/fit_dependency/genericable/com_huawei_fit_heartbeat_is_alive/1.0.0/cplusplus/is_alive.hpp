/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description:
 * Author: auto generate by FIT IDL
 * Date:
 */

#ifndef COM_HUAWEI_FIT_HEARTBEAT_IS_ALIVE_H
#define COM_HUAWEI_FIT_HEARTBEAT_IS_ALIVE_H

#include <fit/external/framework/proxy_client.hpp>

#include <component/com_huawei_fit_heartbeat_heartbeat_common/1.0.0/cplusplus/heartbeatCommon.hpp>
#include <fit/stl/vector.hpp>

namespace fit {
namespace heartbeat {
struct __IsAlive {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string*, const Fit::string*>;
    using OutType = ::Fit::Framework::ArgumentsOut<bool**>;
};

class IsAlive : public ::Fit::Framework::ProxyClient<FitCode(__IsAlive::InType, __IsAlive::OutType)> {
public:
    static constexpr const char* GENERIC_ID = "b59cb3fcaee442799cafa2b887013e91";
    IsAlive() : ::Fit::Framework::ProxyClient<FitCode(__IsAlive::InType, __IsAlive::OutType)>(GENERIC_ID) {}
    FitCode operator()(const Fit::string* id, const Fit::string* scene, bool** result)
    {
        return ::Fit::Framework::ProxyClient<FitCode(__IsAlive::InType, __IsAlive::OutType)>::operator()(
            id, scene, result);
    }
};
}
}

#endif