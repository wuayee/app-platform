/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : auto idl
 * Date         :
 */

#ifndef COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_WORKER_DETAIL_G_H
#define COM_HUAWEI_FIT_HAKUNA_KERNEL_REGISTRY_SERVER_QUERY_WORKER_DETAIL_G_H

#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Worker/1.0.0/cplusplus/Worker.hpp>
#include <component/com_huawei_fit_hakuna_kernel_registry_shared_Application/1.0.0/cplusplus/Application.hpp>
#include <fit/external/framework/proxy_client.hpp>
#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Fitable_meta/1.0.0/cplusplus/FitableMeta.hpp"


namespace fit {
namespace hakuna {
namespace kernel {
namespace registry {
namespace server {
struct WorkerDetail {
    ::fit::hakuna::kernel::registry::shared::Worker worker;
    ::fit::hakuna::kernel::registry::shared::Application app;
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitables;
};
struct __QueryWorkerDetail {
    using InType = ::Fit::Framework::ArgumentsIn<const Fit::string*>;
    using OutType = ::Fit::Framework::ArgumentsOut<WorkerDetail**>;
};

class QueryWorkerDetail
    : public ::Fit::Framework::ProxyClient<FitCode(__QueryWorkerDetail::InType, __QueryWorkerDetail::OutType)> {
public:
    static constexpr const char* GENERIC_ID = "d1019f2ccf6d485cb4831e970acd79ac";
    QueryWorkerDetail()
        : ::Fit::Framework::ProxyClient<FitCode(__QueryWorkerDetail::InType, __QueryWorkerDetail::OutType)>(GENERIC_ID)
    {
    }
    FitCode operator()(const Fit::string* workerId, WorkerDetail** result)
    {
        return ::Fit::Framework::ProxyClient<FitCode(
            __QueryWorkerDetail::InType, __QueryWorkerDetail::OutType)>::operator()(workerId, result);
    }
};
}
}
}
}
}

#endif