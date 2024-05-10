/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:28:20
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_query_fitable_metas/1.0.0/cplusplus/query_fitable_metas.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
using namespace ::fit::hakuna::kernel::registry::shared;
using namespace ::fit::hakuna::kernel::shared;
/**
 * 根据genericableId查询元数据信息
 *
 * @param genericableIds
 * @return FitableMeta
 */
FitCode QueryFitableMetas(ContextObj ctx,
    const Fit::vector<Fit::string> *genericableIds, const Fit::string *environment,
    Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> **result)
{
    if (genericableIds == nullptr || environment == nullptr) {
        FIT_LOG_ERROR("Param is nullptr.");
        return FIT_ERR_PARAM;
    }
    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetasInner = Fit::Registry::fit_registry_mgr::instance()
        ->get_fitable_meta_service()->Query(*genericableIds, *environment);
    Fit::vector<FitableMeta>* fitableMetas = Fit::Context::NewObj<Fit::vector<FitableMeta>>(ctx);
    for (const auto& fitableMetaInner : fitableMetasInner) {
        FitableMeta fitableMeta;
        fitableMeta.fitable = Fit::Context::NewObj<Fitable>(ctx);
        fitableMeta.fitable->fitableId = fitableMetaInner.fitable.fitableId;
        fitableMeta.fitable->fitableVersion = fitableMetaInner.fitable.fitableVersion;
        fitableMeta.fitable->genericableId = fitableMetaInner.fitable.genericableId;
        fitableMeta.fitable->genericableVersion = fitableMetaInner.fitable.genericableVersion;
        for (const auto& format : fitableMetaInner.formats) {
            fitableMeta.formats.emplace_back(static_cast<int32_t>(format));
        }
        fitableMeta.application = Fit::Context::NewObj<Application>(ctx);
        fitableMeta.application->name = fitableMetaInner.application.name;
        fitableMeta.application->nameVersion = fitableMetaInner.application.nameVersion;
        fitableMeta.aliases = fitableMetaInner.aliases;
        fitableMeta.tags = fitableMetaInner.tags;
        fitableMeta.extensions = fitableMetaInner.extensions;
        fitableMeta.environment = fitableMetaInner.environment;
        fitableMetas->emplace_back(fitableMeta);
    }
    *result = fitableMetas;
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::QueryFitableMetas)
        .SetGenericId(fit::hakuna::kernel::registry::shared::queryFitableMetas::GENERIC_ID)
        .SetFitableId("2e1cdd0d241b455891266fa86afd5d3c");
}