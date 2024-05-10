/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : auto
 * Date         : 2023-09-06 17:04:05
 */

#include <fit/external/framework/annotation/fitable_registrar.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_application_instances/1.0.0/cplusplus/query_application_instances.hpp>
#include <core/fit_registry_mgr.h>
#include <fit/fit_log.h>
#include <fit/fit_code.h>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/vector.hpp>
#include <fit/external/util/context/context_api.hpp>
namespace {
using namespace ::fit::hakuna::kernel::registry::shared;
/**
 * 查询应用实例信息
 * @param applications
 * @return
 */
Fit::vector<ApplicationInstance> Convert(
    const Fit::vector<Fit::RegistryInfo::ApplicationInstance>& applicationInstancesInner, ContextObj ctx)
{
    Fit::vector<ApplicationInstance> applicationInstances;
    for (const auto& applicationInstanceInner : applicationInstancesInner) {
        Fit::unordered_map<Fit::string, Fit::unordered_map<Fit::string, Fit::vector<Endpoint>>> workerHostEndpoints;
        for (const auto& addressInner : applicationInstanceInner.addresses) {
            Endpoint endpoint;
            endpoint.port = addressInner.port;
            endpoint.protocol = static_cast<int32_t>(addressInner.protocol);
            workerHostEndpoints[addressInner.workerId][addressInner.host].emplace_back(std::move(endpoint));
        }

        Fit::unordered_map<Fit::string, Fit::vector<Address>> workerIdAddresses;
        for (const auto& addressInner : applicationInstanceInner.addresses) {
            Address address;
            address.host = addressInner.host;
            address.endpoints = std::move(workerHostEndpoints[addressInner.workerId][addressInner.host]);
            workerIdAddresses[addressInner.workerId].emplace_back(std::move(address));
        }

        Fit::vector<Worker> workers;
        for (const auto& workerInner : applicationInstanceInner.workers) {
            Worker worker;
            worker.id = workerInner.workerId;
            worker.expire = workerInner.expire;
            worker.environment = workerInner.environment;
            worker.addresses = std::move(workerIdAddresses[workerInner.workerId]);
            worker.extensions = workerInner.extensions;
            workers.emplace_back(std::move(worker));
        }
        if (workers.empty()) {
            continue;
        }
        Application application;
        application.name = applicationInstanceInner.workers.front().application.name;
        application.nameVersion = applicationInstanceInner.workers.front().application.nameVersion;

        ApplicationInstance applicationInstance;
        applicationInstance.application = Fit::Context::NewObj<Application>(ctx);
        *(applicationInstance.application) = std::move(application);
        applicationInstance.workers = std::move(workers);
        applicationInstances.emplace_back(applicationInstance);
    }
    return applicationInstances;
}
FitCode QueryApplicationInstances(ContextObj ctx,
    const Fit::vector<::fit::hakuna::kernel::registry::shared::Application> *applications,
    Fit::vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> **result)
{
    // applicationInstances暂时不真正被消费，单进程只支持单应用
    if (applications == nullptr) {
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

    auto applicationInstances = Fit::Context::NewObj<Fit::vector<ApplicationInstance>>(ctx);
    auto applicationInstancesInner = Fit::Registry::fit_registry_mgr::instance()->
        get_application_instance_service()->Query(applicationsInner);
    *applicationInstances = Convert(applicationInstancesInner, ctx);
    *result = applicationInstances;
    return FIT_OK;
}
}

FIT_REGISTRATIONS
{
    ::Fit::Framework::Annotation::Fitable(::QueryApplicationInstances)
        .SetGenericId(fit::hakuna::kernel::registry::server::queryApplicationInstances::GENERIC_ID)
        .SetFitableId("1321373e9ac14693be6df7d3e3bf3b94");
}