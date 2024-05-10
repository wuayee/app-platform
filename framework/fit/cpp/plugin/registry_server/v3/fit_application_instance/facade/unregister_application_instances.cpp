/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:03:11
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_application_instances/1.0.0/cplusplus/unregister_application_instances.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
/**
 * 反注册应用实例信息
 * @param applications
 * @param workerId
 */
FitCode UnregisterApplicationInstances(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *applications,
    const Fit::string *workerId)
{
    // applicationInstances暂时不真正被消费，单worker只支持单应用
    if (applications == nullptr || workerId == nullptr) {
        FIT_LOG_ERROR("Param is error.");
        return FIT_ERR_PARAM;
    }
    Fit::vector<Fit::RegistryInfo::Application> applicationsInner;
    for (const auto& application : *applications) {
        Fit::RegistryInfo::Application app;
        app.name = application.name;
        app.nameVersion = application.nameVersion;
        applicationsInner.emplace_back(std::move(app));
    }

    return Fit::Registry::fit_registry_mgr::instance()->
        get_application_instance_service()->Remove(applicationsInner, *workerId);
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::UnregisterApplicationInstances)
        .SetGenericId(fit::hakuna::kernel::registry::server::unregisterApplicationInstances::GENERIC_ID)
        .SetFitableId("0ce06191039a4c4fb61ff91574b228fb");
}