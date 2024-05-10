/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable endpoint.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/28
 */

#include <fitable_endpoint.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::LoadBalance;
using namespace Fit::Util;

namespace {
string GetApplicationName(const ApplicationInstance& instance)
{
    if (instance.application == nullptr) {
        return "";
    } else {
        return instance.application->name;
    }
}
string GetApplicationVersion(const ApplicationInstance& instance)
{
    if (instance.application == nullptr) {
        return "";
    } else {
        return instance.application->nameVersion;
    }
}

void InsertEndpoint(vector<FitableEndpoint>& endpoints,
    const ::fit::hakuna::kernel::registry::shared::Address* address,
    const ::fit::hakuna::kernel::registry::shared::Worker* worker,
    const ApplicationInstance& application)
{
    for (auto& endpoint : address->endpoints) {
        FitableEndpoint fitableEndpoint {&endpoint, address, worker, &application};
        int32_t index = VectorUtils::BinarySearch<FitableEndpoint>(endpoints,
            [&fitableEndpoint](const FitableEndpoint& existing) -> int32_t {
                return existing.Compare(fitableEndpoint);
            });
        if (index < 0) {
            VectorUtils::Insert(endpoints, -1 - index, fitableEndpoint);
        }
    }
}
}

FitableEndpoint::FitableEndpoint(const Endpoint* endpoint, const Address* address, const Worker* worker,
    const ApplicationInstance* application) : endpoint_(endpoint), address_(address), worker_(worker),
    application_(application)
{
}

ApplicationInstance* FitableEndpoint::CreateApplicationInstance(ContextObj context)
{
    auto instance = ::Fit::Context::NewObj<ApplicationInstance>(context);
    instance->workers.push_back(*worker_);
    instance->formats = application_->formats;
    if (application_->application != nullptr) {
        instance->application = ::Fit::Context::NewObj<Application>(context);
        *(instance->application) = *(application_->application);
    }
    return instance;
}

void FitableEndpoint::Fill(vector<FitableEndpoint>& endpoints, const ApplicationInstance& application)
{
    for (auto& worker : application.workers) {
        for (auto& address : worker.addresses) {
            InsertEndpoint(endpoints, &address, &worker, application);
        }
    }
}

vector<FitableEndpoint> FitableEndpoint::Flat(const ApplicationInstance& application)
{
    vector<FitableEndpoint> endpoints {};
    Fill(endpoints, application);
    return endpoints;
}

vector<FitableEndpoint> FitableEndpoint::Flat(const vector<ApplicationInstance>& applications)
{
    vector<FitableEndpoint> endpoints {};
    for (const auto& application : applications) {
        Fill(endpoints, application);
    }
    return endpoints;
}

int32_t FitableEndpoint::Compare(const FitableEndpoint& another) const
{
    int32_t ret = worker_->id.compare(another.worker_->id);
    if (ret == 0) {
        ret = worker_->environment.compare(another.worker_->environment);
    }
    if (ret == 0) {
        ret = address_->host.compare(another.address_->host);
    }
    if (ret == 0) {
        ret = endpoint_->port - another.endpoint_->port;
    }
    if (ret == 0) {
        ret = endpoint_->protocol - another.endpoint_->protocol;
    }
    return ret;
}

const Endpoint* FitableEndpoint::GetEndpoint() const
{
    return endpoint_;
}

const Address* FitableEndpoint::GetAddress() const
{
    return address_;
}

const Worker* FitableEndpoint::GetWorker() const
{
    return worker_;
}

const ApplicationInstance* FitableEndpoint::GetApplicationInstance() const
{
    return application_;
}

const Application* FitableEndpoint::GetApplication() const
{
    return GetApplicationInstance()->application;
}

vector<ApplicationInstance>* FitableEndpoint::Aggregate(ContextObj context, const vector<FitableEndpoint>& endpoints)
{
    auto* instances = Fit::Context::NewObj<vector<ApplicationInstance>>(context);
    for (auto& endpoint : endpoints) {
        auto instance = GetOrCreate(*instances, context, endpoint.GetApplicationInstance());
        auto worker = GetOrCreate(instance->workers, context, endpoint.GetWorker());
        auto address = GetOrCreate(worker->addresses, context, endpoint.GetAddress());
        GetOrCreate(address->endpoints, context, endpoint.GetEndpoint());
    }
    return instances;
}

ApplicationInstance* FitableEndpoint::GetOrCreate(vector<ApplicationInstance>& instances, ContextObj context,
    const ApplicationInstance* current)
{
    int32_t index = VectorUtils::BinarySearch<ApplicationInstance>(instances,
        [&current](const ApplicationInstance& existing) -> int32_t {
            int32_t ret = GetApplicationName(existing).compare(GetApplicationName(*current));
            if (ret == 0) {
                ret = GetApplicationVersion(existing).compare(GetApplicationVersion(*current));
            }
            return ret;
        });
    if (index < 0) {
        index = -1 - index;
        ApplicationInstance instance {};
        if (current->application != nullptr) {
            instance.application = ::Fit::Context::NewObj<Application>(context);
            *(instance.application) = *(current->application);
        }
        instance.formats = current->formats;
        VectorUtils::Insert(instances, index, std::move(instance));
    }
    return &instances[index];
}

Worker* FitableEndpoint::GetOrCreate(vector<Worker>& workers, ContextObj context, const Worker* current)
{
    int32_t index = VectorUtils::BinarySearch<Worker>(workers,
        [&current](const Worker& existing) -> int32_t {
            int32_t ret = existing.id.compare(current->id);
            if (ret == 0) {
                ret = existing.environment.compare(current->environment);
            }
            return ret;
        });
    if (index < 0) {
        index = -1 - index;
        Worker worker {};
        worker.id = current->id;
        worker.environment = current->environment;
        worker.expire = current->expire;
        worker.extensions = current->extensions;
        VectorUtils::Insert(workers, index, std::move(worker));
    }
    return &workers[index];
}

Address* FitableEndpoint::GetOrCreate(vector<Address>& addresses, ContextObj context, const Address* current)
{
    int32_t index = VectorUtils::BinarySearch<Address>(addresses,
        [&current](const Address& existing) -> int32_t {
            return existing.host.compare(current->host);
        });
    if (index < 0) {
        index = -1 - index;
        Address address {};
        address.host = current->host;
        VectorUtils::Insert(addresses, index, std::move(address));
    }
    return &addresses[index];
}

Endpoint* FitableEndpoint::GetOrCreate(vector<Endpoint>& endpoints, ContextObj context, const Endpoint* current)
{
    int32_t index = VectorUtils::BinarySearch<Endpoint>(endpoints,
        [&current](const Endpoint& existing) -> int32_t {
            int32_t ret = existing.port - current->port;
            if (ret == 0) {
                ret = existing.protocol - current->protocol;
            }
            return ret;
        });
    if (index < 0) {
        index = -1 - index;
        Endpoint endpoint = *current;
        VectorUtils::Insert(endpoints, index, endpoint);
    }
    return &endpoints[index];
}
