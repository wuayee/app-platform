/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023-10-30 14:53:24
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_SYSTEM_SHARED_GET_APPLICATION_EXTENSIONS_G_H
#define COM_HUAWEI_FIT_HAKUNA_SYSTEM_SHARED_GET_APPLICATION_EXTENSIONS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace system {
namespace shared {
struct __getApplicationExtensions {
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::map<Fit::string, Fit::string> **>;
};

/**
 * get application extensions
 * @return
 */
class getApplicationExtensions : public ::Fit::Framework::ProxyClient<FitCode(__getApplicationExtensions::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "2170e258548a420a98eb800269ed8dc6";
    getApplicationExtensions() : ::Fit::Framework::ProxyClient<FitCode(__getApplicationExtensions::OutType)>(
        GENERIC_ID) {}
    explicit getApplicationExtensions(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__getApplicationExtensions::OutType)>(GENERIC_ID, ctx) {}
    ~getApplicationExtensions() = default;
};
}
}
}
}

#endif
