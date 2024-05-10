/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       :
 * Date         : 2023-11-03
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_SYSTEM_SHARED_GET_WORKER_EXTENSIONS_G_H
#define COM_HUAWEI_FIT_HAKUNA_SYSTEM_SHARED_GET_WORKER_EXTENSIONS_G_H

#include <fit/external/framework/proxy_client.hpp>

#include <fit/stl/map.hpp>
#include <fit/stl/string.hpp>

namespace fit {
namespace hakuna {
namespace system {
namespace shared {
struct __getWorkerExtensions {
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::map<Fit::string, Fit::string> **>;
};

/**
 * get worker extensions
 * @return
 */
class getWorkerExtensions : public ::Fit::Framework::ProxyClient<FitCode(__getWorkerExtensions::OutType)> {
public:
    static constexpr const char *GENERIC_ID = "b8199c5a83d24320ac7d18c36f15ffe9";
    getWorkerExtensions() : ::Fit::Framework::ProxyClient<FitCode(__getWorkerExtensions::OutType)>(GENERIC_ID) {}
    explicit getWorkerExtensions(ContextObj ctx)
        : ::Fit::Framework::ProxyClient<FitCode(__getWorkerExtensions::OutType)>(GENERIC_ID, ctx) {}
    ~getWorkerExtensions() = default;
};
}
}
}
}

#endif
