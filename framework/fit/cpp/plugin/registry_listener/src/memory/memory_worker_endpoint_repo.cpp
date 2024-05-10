/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for memory worker endpoint repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/15
 */

#include <memory/memory_worker_endpoint_repo.hpp>

#include <domain/worker.hpp>

#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryWorkerEndpointRepo::MemoryWorkerEndpointRepo(const WorkerPtr& worker)
    : worker_(worker)
{
}

WorkerPtr MemoryWorkerEndpointRepo::GetWorker() const
{
    return worker_.lock();
}

RegistryListenerPtr MemoryWorkerEndpointRepo::GetRegistryListener() const
{
    WorkerPtr worker = GetWorker();
    return (worker == nullptr) ? nullptr : (worker->GetRegistryListener());
}

WorkerEndpointPtr MemoryWorkerEndpointRepo::Get(const string& host, uint16_t port, int32_t protocol, bool createNew)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&host, &port, &protocol](const WorkerEndpointPtr& existing) -> int32_t {
        return existing->Compare(host, port, protocol);
    };
    int32_t index = VectorUtils::BinarySearch<WorkerEndpointPtr>(endpoints_, compare);
    WorkerEndpointPtr endpoint;
    if (index > -1) {
        endpoint = endpoints_[index];
    } else if (createNew) {
        endpoint = std::make_shared<WorkerEndpoint>(shared_from_this(), host, port, protocol);
        VectorUtils::Insert(endpoints_, -1 - index, endpoint);
    } else {
        endpoint = nullptr;
    }
    return endpoint;
}

WorkerEndpointPtr MemoryWorkerEndpointRepo::Remove(const string& host, uint16_t port, int32_t protocol)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&host, &port, &protocol](const WorkerEndpointPtr& existing) -> int32_t {
        return existing->Compare(host, port, protocol);
    };
    int32_t index = VectorUtils::BinarySearch<WorkerEndpointPtr>(endpoints_, compare);
    WorkerEndpointPtr endpoint;
    if (index > -1) {
        endpoint = Util::VectorUtils::Remove(endpoints_, index);
    } else {
        endpoint = nullptr;
    }
    return endpoint;
}

uint32_t MemoryWorkerEndpointRepo::Count() const
{
    lock_guard<mutex> guard {mutex_};
    return endpoints_.size();
}

vector<WorkerEndpointPtr> MemoryWorkerEndpointRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return endpoints_;
}
