/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:27:45
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_unregister_fitable_metas/1.0.0/cplusplus/unregister_fitable_metas.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
/**
 * 按应用反注册元数据信息
 * @param applications
 */
FitCode UnregisterFitableMetas(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *applications,
    const Fit::string *environment)
{
    if (applications == nullptr || environment == nullptr) {
        FIT_LOG_ERROR("Param is nullptr.");
        return FIT_ERR_PARAM;
    }

    Fit::vector<Fit::RegistryInfo::Application> applicationsInner;
    for (const auto& application : *applications) {
        Fit::RegistryInfo::Application applicationInner;
        applicationInner.name = application.name;
        applicationInner.nameVersion = application.nameVersion;
        applicationsInner.emplace_back(applicationInner);
    }

    return Fit::Registry::fit_registry_mgr::instance()
        ->get_fitable_meta_service()->Remove(applicationsInner, *environment);
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::UnregisterFitableMetas)
        .SetGenericId(fit::hakuna::kernel::registry::shared::unregisterFitableMetas::GENERIC_ID)
        .SetFitableId("5a8d7868c329492193faaf94f21c6057");
}