/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2024-01-08
 * Notes:       :
 */

#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_worker_detail/1.0.0/cplusplus/query_worker_detail.hpp>

#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include "core/fit_registry_mgr.h"
#include "registry_server_memory/common/util.h"

namespace {
using namespace ::Fit;
using namespace ::Fit::Registry;
using namespace ::fit::hakuna::kernel;
using namespace ::fit::hakuna::kernel::registry::server;
using namespace ::fit::hakuna::kernel::registry;

FitCode QueryWorkerDetailImpl(ContextObj ctx, const string* workerId, WorkerDetail** result)
{
    if (workerId == nullptr) {
        FIT_LOG_ERROR("Null workerId.");
        return FIT_ERR_PARAM;
    }

    Fit::RegistryInfo::WorkerDetail detail {};
    auto ret = fit_registry_mgr::instance()->get_registry_service().QueryWorkerDetail(*workerId, detail);
    if (ret != FIT_OK) {
        FIT_LOG_INFO("Can not find the worker, (id=%s, ret=%x).", workerId->c_str(), ret);
        return FIT_OK;
    }
    auto tmp = Context::NewObj<WorkerDetail>(ctx);
    if (tmp == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    RegistryCommonConverter::ConvertToWorker(detail.worker, detail.addresses, tmp->worker);

    tmp->fitables.reserve(detail.fitables.size());
    for (auto& src : detail.fitables) {
        tmp->fitables.emplace_back();
        tmp->fitables.back().fitable = Context::NewObj<::fit::hakuna::kernel::shared::Fitable>(ctx);
        if (tmp->fitables.back().fitable == nullptr) {
            FIT_LOG_ERROR("Failed to new fitable.");
            return FIT_ERR_CTX_BAD_ALLOC;
        }
        *tmp->fitables.back().fitable = RegistryCommonConverter::Convert(src.fitable);
        tmp->fitables.back().formats.reserve(src.formats.size());
        for (const auto& it : src.formats) {
            tmp->fitables.back().formats.push_back(static_cast<int32_t>(it));
        }
        tmp->fitables.back().tags = src.tags;
        tmp->fitables.back().extensions = src.extensions;
        tmp->fitables.back().aliases = src.aliases;
    }
    tmp->app = RegistryCommonConverter::ConvertApplicationMeta(detail.app);
    TryFillApplicationMeta(tmp->app);
    *result = tmp;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(QueryWorkerDetailImpl)
        .SetGenericId(fit::hakuna::kernel::registry::server::QueryWorkerDetail::GENERIC_ID)
        .SetFitableId("queryWorkerDetail");
}
} // LCOV_EXCL_LINE