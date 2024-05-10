/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  : default implenment for registry listener api
 * Author       : songyongtan
 * Create       : 2022-07-21
 * Notes:       :
 */

#include "default_registry_listener_api.h"

#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_get_fitable_addresses/1.0.0/cplusplus/getFitableAddresses.hpp>
#include <genericable/com_huawei_fit_registry_get_registry_addresses/1.0.0/cplusplus/getRegistryAddresses.hpp>

#include "component/com_huawei_fit_hakuna_kernel_registry_shared_Application_instance/1.0.0/cplusplus/ApplicationInstance.hpp"
#include "fit/fit_log.h"

namespace Fit {
uint32_t DefaultRegistryListenerApi::CalculateApplicationInstanceAddressSize(
    const vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& applicationInstances)
{
    uint32_t result = 0;
    for (auto& instance : applicationInstances) {
        for (auto& worker : instance.workers) {
            for (auto& host : worker.addresses) {
                result += host.endpoints.size();
            }
        }
    }
    return result;
}
Framework::ServiceAddress BuildFrameworkAddress(const Framework::Fitable& fitable,
    const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& instance,
    const ::fit::hakuna::kernel::registry::shared::Worker& worker,
    const ::fit::hakuna::kernel::registry::shared::Address& address,
    const ::fit::hakuna::kernel::registry::shared::Endpoint& endpoint)
{
    Framework::ServiceAddress result {};
    result.serviceMeta.fitable = fitable;
    result.address.workerId = worker.id;
    result.address.host = address.host;
    result.address.port = endpoint.port;
    result.address.protocol = endpoint.protocol;
    result.address.formats = instance.formats;
    result.address.environment = worker.environment;
    result.address.extensions = worker.extensions;
    if (instance.application != nullptr) {
        result.serviceMeta.application.name = instance.application->name;
        result.serviceMeta.application.version = instance.application->nameVersion;
        result.serviceMeta.application.extensions = instance.application->extensions;
    }
    return result;
}

void DefaultRegistryListenerApi::GetAddressesFromApplicationInstance(const Fitable& fitable,
    const vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>& applicationInstances,
    vector<ServiceAddress>& result)
{
    result.reserve(CalculateApplicationInstanceAddressSize(applicationInstances));
    auto flatEndpoints = [&result](const Fitable& fitable,
                             const ::fit::hakuna::kernel::registry::shared::ApplicationInstance& instance,
                             const ::fit::hakuna::kernel::registry::shared::Worker& worker,
                             const ::fit::hakuna::kernel::registry::shared::Address& address) {
        for (auto& endpoint : address.endpoints) {
            result.emplace_back(BuildFrameworkAddress(fitable, instance, worker, address, endpoint));
        }
    };
    for (auto& instance : applicationInstances) {
        for (auto& worker : instance.workers) {
            for (auto& address : worker.addresses) {
                flatEndpoints(fitable, instance, worker, address);
            }
        }
    }
}

FitCode DefaultRegistryListenerApi::GetFitableAddresses(const Fitable& fitable, vector<ServiceAddress>& result)
{
    ::fit::hakuna::kernel::registry::listener::getFitableAddresses getFitableAddresses;
    ::fit::hakuna::kernel::registry::shared::FitableInstance* instance {};
    ::fit::hakuna::kernel::shared::Fitable apiFitable;
    apiFitable.genericableId = fitable.genericId;
    apiFitable.genericableVersion = fitable.genericVersion;
    apiFitable.fitableId = fitable.fitableId;
    apiFitable.fitableVersion = fitable.fitableVersion;
    auto ret = getFitableAddresses(&apiFitable, &instance);
    if (ret != FIT_OK || instance == nullptr) {
        FIT_LOG_ERROR("Failed to get fitable addresses. (ret=%x, fitable=%s:%s).", ret, fitable.genericId.c_str(),
            fitable.fitableId.c_str());
        return ret;
    }
    GetAddressesFromApplicationInstance(fitable, instance->applicationInstances, result);
    return FIT_OK;
}

FitCode DefaultRegistryListenerApi::GetRegistryFitableAddresses(vector<ServiceAddress> &result)
{
    fit::registry::getRegistryAddresses proxy;
    ::fit::hakuna::kernel::registry::shared::FitableInstance *registryAddresses;
    auto ret = proxy(&registryAddresses);
    if (ret != FIT_ERR_SUCCESS || registryAddresses == nullptr) {
        FIT_LOG_ERROR("Failed to get registry addresses. (ret=%x).", ret);
        return ret;
    }
    FIT_LOG_DEBUG(
        "Successful to get registry addresses. (count=%lu).", registryAddresses->applicationInstances.size());
    GetAddressesFromApplicationInstance({}, registryAddresses->applicationInstances, result);

    return FIT_OK;
}
unique_ptr<RegistryListenerApi> RegistryListenerApi::CreateDefault()
{
    return make_unique<DefaultRegistryListenerApi>();
}
}