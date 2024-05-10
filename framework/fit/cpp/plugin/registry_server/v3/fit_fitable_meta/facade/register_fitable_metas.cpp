/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:27:05
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_register_fitable_metas/1.0.0/cplusplus/register_fitable_metas.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
/**
 * 注册fitable元数据
 * @param fitableMetas
 */
FitCode registerFitableMetas(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> *fitableMetas)
{
    if (fitableMetas == nullptr) {
        FIT_LOG_ERROR("Fitable metas is nullptr.");
        return FIT_ERR_PARAM;
    }

    Fit::vector<Fit::RegistryInfo::FitableMeta> fitableMetasInner;
    for (const auto& fitableMeta : *fitableMetas) {
        Fit::RegistryInfo::FitableMeta fitableMetaInner;
        fitableMetaInner.fitable.genericableId = fitableMeta.fitable->genericableId;
        fitableMetaInner.fitable.genericableVersion = fitableMeta.fitable->genericableVersion;
        fitableMetaInner.fitable.fitableId = fitableMeta.fitable->fitableId;
        fitableMetaInner.fitable.fitableVersion = fitableMeta.fitable->fitableVersion;
        for (const auto& format : fitableMeta.formats) {
            fitableMetaInner.formats.emplace_back(static_cast<Fit::fit_format_type>(format));
        }
        fitableMetaInner.application.name = fitableMeta.application->name;
        fitableMetaInner.application.nameVersion = fitableMeta.application->nameVersion;
        fitableMetaInner.aliases = fitableMeta.aliases;
        fitableMetaInner.tags = fitableMeta.tags;
        fitableMetaInner.extensions = fitableMeta.extensions;
        fitableMetaInner.environment = fitableMeta.environment;
        fitableMetasInner.emplace_back(fitableMetaInner);
    }

    return Fit::Registry::fit_registry_mgr::instance()->get_fitable_meta_service()->Save(fitableMetasInner);
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::registerFitableMetas)
        .SetGenericId(fit::hakuna::kernel::registry::shared::registerFitableMetas::GENERIC_ID)
        .SetFitableId("8b3324c0b9194109a193b77599af71c5");
}