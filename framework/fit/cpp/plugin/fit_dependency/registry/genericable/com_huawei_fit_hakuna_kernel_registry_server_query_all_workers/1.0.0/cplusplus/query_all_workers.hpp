/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_ALL_WORKERS_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_ALL_WORKERS_G_H

#include <fit/external/framework/proxy_client.hpp>
#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp"

#include <fit/stl/vector.hpp>

namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct __QueryAllWorkers {
    using InType = ::Fit::Framework::ArgumentsIn<>;
    using OutType = ::Fit::Framework::ArgumentsOut<Fit::vector<fit::hakuna::kernel::registry::shared::Worker>**>;
};

class QueryAllWorkers
    : public ::Fit::Framework::ProxyClient<FitCode(__QueryAllWorkers::InType, __QueryAllWorkers::OutType)> {
public:
    static constexpr const char* GENERIC_ID = "12948a1bf0f54fbbb578718c3e18f961";
    QueryAllWorkers()
        : ::Fit::Framework::ProxyClient<FitCode(__QueryAllWorkers::InType, __QueryAllWorkers::OutType)>(GENERIC_ID)
    {
    }
    FitCode operator()(Fit::vector<fit::hakuna::kernel::registry::shared::Worker>** result)
    {
        return ::Fit::Framework::ProxyClient<FitCode(
            __QueryAllWorkers::InType, __QueryAllWorkers::OutType)>::operator()(result);
    }
};
}
}
}
}
}

#endif