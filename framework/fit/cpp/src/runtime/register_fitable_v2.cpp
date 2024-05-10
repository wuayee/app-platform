/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : Provide registry fitable implement for v2.
 * Author       : w00561424
 * Date:        : 2023/09/18
 */
#include <register_fitable_v2.h>
#include <fit/internal/fit_system_property_utils.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_fitables/1.0.0/cplusplus/registerFitables.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_fitables/1.0.0/cplusplus/unregisterFitables.hpp>
namespace Fit {
RegisterFitableV2::RegisterFitableV2(Fit::Framework::Formatter::FormatterServicePtr formatterService,
    std::shared_ptr<CommonConfig> commonConfig, Configuration::ConfigurationServicePtr configurationService)
{
    formatterService_ = std::move(formatterService);
    commonConfig_ = std::move(commonConfig);
    configurationService_ = std::move(configurationService);
}
FitCode RegisterFitableV2::RegisterFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables,
    int32_t expire)
{
    auto serverAddresses = FitSystemPropertyUtils::GetExternalAddresses();
    if (serverAddresses.empty()) {
        FIT_LOG_WARN("No local address. Skip to register fitables.");
        return FIT_OK;
    }

    ::fit::hakuna::kernel::registry::server::registerFitables registerFitablesInvoker;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas;
    Build(fitables, registerFitablesInvoker.ctx_, fitableMetas);

    ::fit::hakuna::kernel::registry::shared::Worker worker;
    worker.addresses = GetLocalAddresses(serverAddresses);
    worker.environment = commonConfig_->GetWorkerEnvironment();
    worker.id = commonConfig_->GetWorkerId();
    worker.expire = expire;
    worker.extensions = commonConfig_->GetWorkerExtensions();

    FIT_LOG_DEBUG("Register fitables to registry server. [app=%s, version=%s, fitables=%lu]",
        application_.name.c_str(), application_.nameVersion.c_str(), fitableMetas.size());
    return registerFitablesInvoker(&fitableMetas, &worker, &application_);
}

FitCode RegisterFitableV2::UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables)
{
    ::fit::hakuna::kernel::registry::server::unregisterFitables unregisterFitablesInvoker;
    auto fitableInfos = Convert(fitables);
    Fit::string workerId = commonConfig_->GetWorkerId();
    return unregisterFitablesInvoker(&fitableInfos, &workerId);
}

FitCode RegisterFitableV2::CheckFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables)
{
    return RegisterFitService(fitables);
}
}