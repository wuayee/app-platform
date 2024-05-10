/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:01:38
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_register_application_instances/1.0.0/cplusplus/register_application_instances.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
namespace {
void GetAddress(const Fit::vector<::fit::hakuna::kernel::registry::shared::Address>& addressesIn,
    const Fit::string& workerId, Fit::vector<Fit::RegistryInfo::Address>& addressesOut)
{
    for (const auto& address : addressesIn) {
        for (const auto& endpoint : address.endpoints) {
            Fit::RegistryInfo::Address addressInner;
            addressInner.workerId = workerId;
            addressInner.host = address.host;
            addressInner.port = endpoint.port;
            addressInner.protocol = static_cast<Fit::fit_protocol_type>(endpoint.protocol);
            addressesOut.emplace_back(std::move(addressInner));
        }
    }
}
/**
 * 注册应用实例信息
 *
 * @param applicationInstances
 */
FitCode RegisterApplicationInstances(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *applicationInstances)
{
    if (applicationInstances == nullptr) {
        FIT_LOG_ERROR("Application is null.");
        return FIT_ERR_PARAM;
    }
    Fit::vector<Fit::RegistryInfo::ApplicationInstance> applicationInstancesInner;
    applicationInstancesInner.reserve(applicationInstances->size());
    for (const auto& applicationInstance : *applicationInstances) {
        Fit::RegistryInfo::ApplicationInstance applicationInstanceInner;
        Fit::RegistryInfo::Application app;
        app.name = applicationInstance.application->name;
        app.nameVersion = applicationInstance.application->nameVersion;

        Fit::vector<Fit::RegistryInfo::Worker> workers;
        Fit::vector<Fit::RegistryInfo::Address> addresses;
        for (const auto& worker : applicationInstance.workers) {
            Fit::RegistryInfo::Worker workerInner;
            workerInner.workerId = worker.id;
            workerInner.application = app;
            workerInner.expire = worker.expire;
            workerInner.environment = worker.environment;
            workerInner.version = worker.version;
            workers.emplace_back(std::move(workerInner));

            GetAddress(worker.addresses, worker.id, addresses);
        }
        applicationInstanceInner.workers = std::move(workers);
        applicationInstanceInner.addresses= std::move(addresses);
        applicationInstancesInner.emplace_back(applicationInstanceInner);
    }
    return Fit::Registry::fit_registry_mgr::instance()->
        get_application_instance_service()->Save(applicationInstancesInner);
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::RegisterApplicationInstances)
        .SetGenericId(fit::hakuna::kernel::registry::server::registerApplicationInstances::GENERIC_ID)
        .SetFitableId("bc34b3a11bfc42c0971cf9d472e693f7");
}