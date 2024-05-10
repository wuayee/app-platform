/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Create       : 2023-04-15
 * Notes:       :
 */

#include <fit/stl/set.hpp>
#include <fit/fit_log.h>
#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_running_fitables/1.0.0/cplusplus/query_running_fitables.hpp>
#include <registry_server_memory/common/registry_common_converter.hpp>
#include "core/fit_registry_mgr.h"
#include "registry_server_memory/common/util.h"

namespace {
using namespace ::Fit;
using namespace ::Fit::Registry;
using namespace ::fit::hakuna::kernel;
using namespace ::fit::hakuna::kernel::registry::server;
using namespace ::fit::hakuna::kernel::registry;

void TryFillApplicationMeta(Fit::vector<RunningFitable>& result)
{
    for (auto& item : result) {
        Fit::Registry::TryFillApplicationMeta(*item.meta->application);
    }
}

set<string> UniqueEnvironments(const vector<RegistryInfo::Worker>& workers)
{
    set<string> environments;
    for (auto& worker : workers) {
        environments.emplace(worker.environment);
    }
    return environments;
}
/**
 * 查询当前注册中心正在运行的fitable
 *
 * @param req
 * @return
 */
FitCode QueryRunningFitables(ContextObj ctx, const vector<QueryRunningFitablesParam>* req,
                             vector<RunningFitable>** result)
{
    if (req == nullptr || result == nullptr) {
        FIT_LOG_ERROR("Null req or result.");
        return FIT_ERR_PARAM;
    }

    auto runningFitables = Context::NewObj<vector<RunningFitable>>(ctx);
    if (runningFitables == nullptr) {
        FIT_LOG_ERROR("Failed to new result.");
        return FIT_ERR_CTX_BAD_ALLOC;
    }
    for (const auto& genericable : *req) {
        auto instances =
            fit_registry_mgr::instance()->get_registry_service().GetFitableInstances(genericable.genericableId);
        for (auto& instance : instances) {
            RunningFitable runningFitable;
            runningFitable.meta = Context::NewObj<::fit::hakuna::kernel::registry::shared::FitableMeta>(ctx);
            if (runningFitable.meta == nullptr) {
                FIT_LOG_ERROR("Failed to new fitableMeta.");
                return FIT_ERR_CTX_BAD_ALLOC;
            }
            runningFitable.meta->fitable = Context::NewObj<::fit::hakuna::kernel::shared::Fitable>(ctx);
            if (runningFitable.meta->fitable == nullptr) {
                FIT_LOG_ERROR("Failed to new fitable.");
                return FIT_ERR_CTX_BAD_ALLOC;
            }
            runningFitable.meta->application = Context::NewObj<registry::shared::Application>(ctx);
            if (runningFitable.meta->application == nullptr) {
                FIT_LOG_ERROR("Failed to new application.");
                return FIT_ERR_CTX_BAD_ALLOC;
            }
            *runningFitable.meta->application = RegistryCommonConverter::Convert(instance.fitableMeta.application);
            *runningFitable.meta->fitable = RegistryCommonConverter::Convert(instance.fitableMeta.fitable);
            FIT_LOG_DEBUG("Fitable=(%s:%s), workerSize=%ld.", runningFitable.meta->fitable->genericableId.c_str(),
                runningFitable.meta->fitable->fitableId.c_str(), instance.workers.size());
            runningFitable.meta->aliases = instance.fitableMeta.aliases;
            runningFitable.meta->tags = instance.fitableMeta.tags;
            runningFitable.meta->extensions = instance.fitableMeta.extensions;
            // 元数据
            set<string> environments = UniqueEnvironments(instance.workers);
            for (auto& env : environments) {
                FIT_LOG_DEBUG(" -- environment=%s.", env.c_str());
            }
            runningFitable.environments.reserve(environments.size());
            runningFitable.environments.assign(environments.begin(), environments.end());
            runningFitables->emplace_back(runningFitable);
        }
    }
    TryFillApplicationMeta(*runningFitables);
    *result = runningFitables;
    return FIT_OK;
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(QueryRunningFitables)
        .SetGenericId(fit::hakuna::kernel::registry::server::queryRunningFitables::GENERIC_ID)
        .SetFitableId("33b1f9b8f1cc49d19719a6536c96e854");
}
} // LCOV_EXCL_LINE