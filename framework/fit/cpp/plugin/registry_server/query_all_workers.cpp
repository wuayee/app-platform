/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2024-01-08
 * Notes:       :
 */

#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_all_workers/1.0.0/cplusplus/query_all_workers.hpp>

#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include "core/fit_registry_mgr.h"

namespace {
using namespace ::Fit;
using namespace ::Fit::Registry;
using namespace ::fit::hakuna::kernel;
using namespace ::fit::hakuna::kernel::registry::server;
using namespace ::fit::hakuna::kernel::registry;

FitCode QueryAllWorkersImpl(ContextObj ctx, vector<registry::shared::Worker>** result)
{
    auto workers = fit_registry_mgr::instance()->get_registry_service().QueryAllWorkers();
    auto tmp = Context::NewObj<vector<registry::shared::Worker>>(ctx);
    if (tmp == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    tmp->reserve(workers.size());
    for (auto& srcWorker : workers) {
        tmp->emplace_back();
        RegistryCommonConverter::ConvertToWorker(srcWorker.worker, srcWorker.addresses, tmp->back());
    }

    *result = tmp;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(QueryAllWorkersImpl)
        .SetGenericId(fit::hakuna::kernel::registry::server::QueryAllWorkers::GENERIC_ID)
        .SetFitableId("queryAllWorkers");
}
} // LCOV_EXCL_LINE
