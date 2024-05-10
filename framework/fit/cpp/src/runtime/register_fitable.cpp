/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2023/09/14
 * Notes:       :
 */
#include <register_fitable.h>
#include <fit/internal/fit_system_property_utils.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_register_fitable_metas/1.0.0/cplusplus/register_fitable_metas.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_unregister_fitable_metas/1.0.0/cplusplus/unregister_fitable_metas.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_application_instances/1.0.0/cplusplus/register_application_instances.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_unregister_application_instances/1.0.0/cplusplus/unregister_application_instances.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_shared_check/1.0.0/cplusplus/check.hpp>
namespace Fit {
constexpr const char* CHECK_TYPE_APPLICATION = "application";
constexpr const char* CHECK_TYPE_APPLICATION_INSTANCE = "application_instance";

RegisterFitable::RegisterFitable(Fit::Framework::Formatter::FormatterServicePtr formatterService,
    std::shared_ptr<CommonConfig> commonConfig, Configuration::ConfigurationServicePtr configurationService)
{
    formatterService_ = std::move(formatterService);
    commonConfig_ = std::move(commonConfig);
    configurationService_ = std::move(configurationService);
}

FitCode RegisterFitable::RegisterFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables,
    int32_t expire)
{
    auto serverAddresses = FitSystemPropertyUtils::Addresses();
    if (serverAddresses.empty()) {
        FIT_LOG_WARN("No local address. Skip to register fitables.");
        return FIT_OK;
    }

    fit::hakuna::kernel::registry::shared::registerFitableMetas registerFitableMetasInvoker;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas;
    Build(fitables, registerFitableMetasInvoker.ctx_, fitableMetas);

    FIT_LOG_DEBUG("Register fitables to registry server. [app=%s, version=%s, fitables=%lu]",
        application_.name.c_str(), application_.nameVersion.c_str(), fitableMetas.size());

    int32_t registerFitableMetasRet = registerFitableMetasInvoker(&fitableMetas);
    if (registerFitableMetasRet != FIT_ERR_SUCCESS) {
        FIT_LOG_ERROR("Register fitable metas error : %d.", registerFitableMetasRet);
        return registerFitableMetasRet;
    }

    ::fit::hakuna::kernel::registry::shared::Worker worker;
    worker.addresses = GetLocalAddresses(serverAddresses);
    worker.environment = commonConfig_->GetWorkerEnvironment();
    worker.id = commonConfig_->GetWorkerId();
    worker.expire = expire;
    worker.extensions = commonConfig_->GetWorkerExtensions();
    worker.version = ComputeWorkerVersion(worker, application_);

    fit::hakuna::kernel::registry::server::registerApplicationInstances registerApplicationInstancesInvoker;
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> applicationInstances;
    ::fit::hakuna::kernel::registry::shared::ApplicationInstance applicationInstance;
    applicationInstance.workers.emplace_back(worker);
    applicationInstance.application =
        Context::NewObj<::fit::hakuna::kernel::registry::shared::Application>(registerApplicationInstancesInvoker.ctx_);
    *applicationInstance.application = application_;
    applicationInstances.emplace_back(applicationInstance);
    return registerApplicationInstancesInvoker(&applicationInstances);
}

FitCode RegisterFitable::UnregisterFitService(const Framework::Annotation::FitableDetailPtrList &fitables)
{
    Fit::vector<::fit::hakuna::kernel::registry::shared::Application> applications {application_};
    Fit::string workerId = commonConfig_->GetWorkerId();
    fit::hakuna::kernel::registry::server::unregisterApplicationInstances unregisterApplicationInstancesInvoker;
    return unregisterApplicationInstancesInvoker(&applications, &workerId);
}

FitCode RegisterFitable::CheckFitService(const Fit::Framework::Annotation::FitableDetailPtrList &fitables)
{
    auto serverAddresses = FitSystemPropertyUtils::Addresses();
    if (serverAddresses.empty()) {
        FIT_LOG_WARN("No local address. Skip to register fitables.");
        return FIT_OK;
    }
    ::fit::hakuna::kernel::registry::shared::check checkInvoker;
    ::Fit::vector<::fit::hakuna::kernel::registry::shared::FitableMeta> fitableMetas;
    Build(fitables, checkInvoker.ctx_, fitableMetas);

    ::fit::hakuna::kernel::registry::shared::Worker worker;
    worker.addresses = GetLocalAddresses(serverAddresses);
    worker.environment = commonConfig_->GetWorkerEnvironment();
    worker.id = commonConfig_->GetWorkerId();
    worker.version = ComputeWorkerVersion(worker, application_);

    Fit::vector<::fit::hakuna::kernel::registry::shared::CheckElement> elements;
    ::fit::hakuna::kernel::registry::shared::CheckElement fitableMetaElement;
    fitableMetaElement.type = CHECK_TYPE_APPLICATION;
    fitableMetaElement.kvs[application_.nameVersion] = application_.name;
    elements.emplace_back(fitableMetaElement);

    ::fit::hakuna::kernel::registry::shared::CheckElement applicationInstanceElement;
    applicationInstanceElement.type = CHECK_TYPE_APPLICATION_INSTANCE;
    applicationInstanceElement.kvs[worker.version] = worker.id;
    elements.emplace_back(applicationInstanceElement);

    FIT_LOG_DEBUG("Check fitable [app=%s, version=%s], worker info [workerId:%s, version:%s].",
        application_.name.c_str(), application_.nameVersion.c_str(), worker.id.c_str(), worker.version.c_str());
    int32_t ret = checkInvoker(&elements);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Check invoker failed, %d.", ret);
    }
    return ret;
}
}