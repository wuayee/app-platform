/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for load balance endpoint supplier.
 * Author       : liangjishi 00298979
 * Date         : 2022/01/07
 */

#include "load_balance_endpoint_supplier.hpp"
#include <fit/fit_log.h>
#include <fit/external/util/context/context_api.hpp>
#include <fit/internal/util/vector_utils.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_filter_v3/1.0.0/cplusplus/filterV3.hpp>
#include <genericable/com_huawei_fit_hakuna_kernel_loadbalance_load_balance_v2/1.0.0/cplusplus/loadBalanceV2.hpp>

using namespace Fit;
using namespace Fit::Util;

using ::fit::hakuna::kernel::registry::shared::Address;
using ::fit::hakuna::kernel::registry::shared::Worker;
using ::fit::hakuna::kernel::registry::shared::Endpoint;
using ::fit::hakuna::kernel::registry::shared::ApplicationInstance;
using ::fit::hakuna::kernel::registry::shared::Application;

namespace {
FitableEndpointPtr Flat(ApplicationInstance* applicationInstance)
{
    FitableEndpointPtr result {nullptr};
    for (auto& worker : applicationInstance->workers) {
        for (auto& address : worker.addresses) {
            for (auto& endpoint : address.endpoints) {
                result = FitableEndpoint::Custom()
                             .SetWorkerId(worker.id)
                             .SetEnvironment(worker.environment)
                             .SetHost(address.host)
                             .SetPort(endpoint.port)
                             .SetProtocol(endpoint.protocol)
                             .SetFormats(move(applicationInstance->formats))
                             .SetContext({{worker.id, worker.extensions},
                                 {applicationInstance->application->name, applicationInstance->application->nameVersion,
                                     applicationInstance->application->extensions}})
                             .Build();
                break;
            }
        }
    }
    return result;
}
Worker& UpdateAndGetWorker(ApplicationInstance& instance, const Framework::Address& addressIn)
{
    int32_t index = VectorUtils::BinarySearch<Worker>(instance.workers,
        [&addressIn](const Worker& existing) -> int32_t {
            auto ret = existing.id.compare(addressIn.workerId);
            if (ret == 0) {
                ret = existing.environment.compare(addressIn.environment);
            }
            return ret;
        });
    if (index < 0) {
        index = -1 - index;
        Worker worker;
        worker.id = addressIn.workerId;
        worker.environment = addressIn.environment;
        worker.extensions = addressIn.extensions;
        VectorUtils::Insert(instance.workers, index, std::move(worker));
    }
    return instance.workers[index];
}
Address& UpdateAndGetAddress(Worker& worker, const Framework::Address& addressIn)
{
    int32_t index = VectorUtils::BinarySearch<Address>(worker.addresses,
        [&addressIn](const Address& existing) -> int32_t {
            return existing.host.compare(addressIn.host);
        });
    if (index < 0) {
        index = -1 - index;
        Address address;
        address.host = addressIn.host;
        VectorUtils::Insert(worker.addresses, index, std::move(address));
    }
    return worker.addresses[index];
}

Endpoint& UpdateAndGetEndpoint(Address& address, const Framework::Address& addressIn)
{
    int32_t index = VectorUtils::BinarySearch<Endpoint>(address.endpoints,
        [&addressIn](const Endpoint& existing) -> int32_t {
        auto ret = existing.port - addressIn.port;
        if (ret == 0) {
            ret = existing.protocol - addressIn.protocol;
        }
        return ret;
    });
    if (index < 0) {
        index = -1 - index;
        Endpoint endpoint;
        endpoint.port = addressIn.port;
        endpoint.protocol = addressIn.protocol;
        VectorUtils::Insert(address.endpoints, index, endpoint);
    }
    return address.endpoints[index];
}

vector<ApplicationInstance> Aggregate(ContextObj ctx, const vector<Framework::ServiceAddress>& addresses)
{
    vector<ApplicationInstance> instances {};
    map<string, ApplicationInstance> appInstanceIndex;
    for (auto& value : addresses) {
        auto& appRef = value.serviceMeta.application;
        auto& appInstance = appInstanceIndex[appRef.name + appRef.version];
        if (appInstance.application == nullptr) {
            appInstance.application = Context::NewObj<Application>(ctx);
            appInstance.application->name = appRef.name;
            appInstance.application->nameVersion = appRef.name;
            appInstance.application->extensions = appRef.extensions;
            appInstance.formats = value.address.formats;
        }
        Worker& worker = UpdateAndGetWorker(appInstance, value.address);
        Address& address = UpdateAndGetAddress(worker, value.address);
        UpdateAndGetEndpoint(address, value.address);
    }
    instances.reserve(appInstanceIndex.size());
    for (auto& instance : appInstanceIndex) {
        instances.emplace_back(move(instance.second));
    }
    return instances;
}

class RegistrySharedFitableEndpointAdapter : public FitableEndpointBase {
public:
    explicit RegistrySharedFitableEndpointAdapter(const ApplicationInstance& application,
        const Worker& worker, const Address& address, const Endpoint& endpoint);
    ~RegistrySharedFitableEndpointAdapter() override = default;
    const ::Fit::string& GetWorkerId() const override;
    const ::Fit::string& GetEnvironment() const override;
    const ::Fit::string& GetHost() const override;
    uint16_t GetPort() const override;
    int32_t GetProtocol() const override;
    const vector<int32_t>& GetFormats() const override;
    const Context& GetContext() const override;

private:
    const ApplicationInstance& application_;
    const Worker& worker_;
    const Address& address_;
    const Endpoint& endpoint_;
    const Context context_;
};

void RemoveUnusedAddressesFromWorker(
    Fit::vector<ApplicationInstance>::iterator& instance,
    Fit::vector<::fit::hakuna::kernel::registry::shared::Worker>::iterator& worker,
    const FitableEndpointPredicate& predicate)
{
    for (auto address = worker->addresses.begin(); address != worker->addresses.end();) {
        for (auto endpoint = address->endpoints.begin(); endpoint != address->endpoints.end();) {
            RegistrySharedFitableEndpointAdapter adapter {*instance, *worker, *address, *endpoint};
            if (predicate.Test(adapter)) {
                endpoint++;
            } else {
                endpoint = address->endpoints.erase(endpoint);
            }
        }
        if (address->endpoints.empty()) {
            address = worker->addresses.erase(address);
        } else {
            address++;
        }
    }
}
void RemoveUnusedAddresses(vector<ApplicationInstance>& instances, const FitableEndpointPredicate& predicate)
{
    for (auto instance = instances.begin(); instance != instances.end();) {
        for (auto worker = instance->workers.begin(); worker != instance->workers.end();) {
            RemoveUnusedAddressesFromWorker(instance, worker, predicate);
            if (worker->addresses.empty()) {
                worker = instance->workers.erase(worker);
            } else {
                worker++;
            }
        }
        if (instance->workers.empty()) {
            instance = instances.erase(instance);
        } else {
            instance++;
        }
    }
}
}

RegistrySharedFitableEndpointAdapter::RegistrySharedFitableEndpointAdapter(
    const ApplicationInstance& application, const Worker& worker, const Address& address, const Endpoint& endpoint)
    : application_(application), worker_(worker), address_(address), endpoint_(endpoint),
      context_(Context {Framework::Worker {worker.id, worker.extensions}, Framework::Application {
          application.application->name, application.application->nameVersion, application.application->extensions}})
{
}

const ::Fit::string& RegistrySharedFitableEndpointAdapter::GetWorkerId() const
{
    return worker_.id;
}

const ::Fit::string& RegistrySharedFitableEndpointAdapter::GetEnvironment() const
{
    return worker_.environment;
}

const ::Fit::string& RegistrySharedFitableEndpointAdapter::GetHost() const
{
    return address_.host;
}

uint16_t RegistrySharedFitableEndpointAdapter::GetPort() const
{
    return endpoint_.port;
}

int32_t RegistrySharedFitableEndpointAdapter::GetProtocol() const
{
    return endpoint_.protocol;
}

const vector<int32_t>& RegistrySharedFitableEndpointAdapter::GetFormats() const
{
    return application_.formats;
}
const FitableEndpoint::Context& RegistrySharedFitableEndpointAdapter::GetContext() const
{
    return context_;
}

LoadBalanceEndpointSupplier::LoadBalanceEndpointSupplier(FitableCoordinatePtr coordinate, FitConfigPtr config,
    Fit::BrokerFitableDiscoveryPtr discovery,
    std::unique_ptr<FitableEndpointPredicate> predicate) : coordinate_(std::move(coordinate)),
    config_(std::move(config)), discovery_(move(discovery)), predicate_(std::move(predicate))
{
}

bool LoadBalanceEndpointSupplier::Filter(::fit::hakuna::kernel::loadbalance::filterV3 &filter,
    const ::fit::hakuna::kernel::shared::Fitable& fitable,
    ::fit::hakuna::kernel::registry::shared::FitableInstance* fitableInstance,
    vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance>*& filtered) const
{
    auto ret = filter(&fitable, &fitableInstance->applicationInstances, &filtered);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to filter addresses by load balancer.");
        return false;
    }
    if (filtered->empty()) {
        FIT_LOG_ERROR("No available endpoint of fitable left after filter.");
        return false;
    }
    if (predicate_ != nullptr) {
        RemoveUnusedAddresses(*filtered, *predicate_);
    }
    if (filtered->empty()) {
        FIT_LOG_ERROR("No available endpoint of fitable left after predicate.");
        return false;
    }
    return true;
}

FitableEndpointPtr LoadBalanceEndpointSupplier::Get() const
{
    Framework::Fitable fitable;
    fitable.genericId = coordinate_->GetGenericableId();
    fitable.genericVersion = coordinate_->GetGenericableVersion();
    fitable.fitableId = coordinate_->GetFitableId();
    fitable.fitableVersion = coordinate_->GetFitableVersion();
    auto addresses = discovery_->GetFitableAddresses(*config_, fitable);
    if (addresses.empty()) {
        return nullptr;
    }
    ::fit::hakuna::kernel::loadbalance::filterV3 filter;
    ::fit::hakuna::kernel::registry::shared::FitableInstance fitableInstance;
    fitableInstance.applicationInstances = Aggregate(filter.GetContext(), addresses);

    ::fit::hakuna::kernel::shared::Fitable apiFitable;
    apiFitable.genericableId = coordinate_->GetGenericableId();
    apiFitable.genericableVersion = coordinate_->GetGenericableVersion();
    apiFitable.fitableId = coordinate_->GetFitableId();
    apiFitable.fitableVersion = coordinate_->GetFitableVersion();

    vector<::fit::hakuna::kernel::registry::shared::ApplicationInstance> *filtered {nullptr};
    if (!Filter(filter, apiFitable, &fitableInstance, filtered)) {
        return nullptr;
    }

    ::fit::hakuna::kernel::loadbalance::loadBalanceV2 balance;
    ApplicationInstance* applicationInstance {nullptr};
    auto ret = balance(&apiFitable, nullptr, filtered, &applicationInstance);
    if (ret != FIT_OK) {
        FIT_LOG_ERROR("Failed to balance load of endpoints. [error=%x]", ret);
        return nullptr;
    }
    return Flat(applicationInstance);
}
