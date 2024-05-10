/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for worker endpoint.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/09
 */

#include <domain/worker_endpoint.hpp>

#include <domain/worker.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

WorkerEndpoint::WorkerEndpoint(const WorkerEndpointRepoPtr& repo, Fit::string host, uint16_t port, int32_t protocol)
    : repo_(repo), host_(std::move(host)), port_(port), protocol_(protocol)
{
}

RegistryListenerPtr WorkerEndpoint::GetRegistryListener() const
{
    WorkerPtr worker = GetWorker();
    return (worker == nullptr) ? nullptr : (worker->GetRegistryListener());
}

WorkerPtr WorkerEndpoint::GetWorker() const
{
    WorkerEndpointRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetWorker());
}

const Fit::string& WorkerEndpoint::GetHost() const
{
    return host_;
}

uint16_t WorkerEndpoint::GetPort() const
{
    return port_;
}

int32_t WorkerEndpoint::GetProtocol() const
{
    return protocol_;
}

bool WorkerEndpoint::IsEnabled() const
{
    return enabled_;
}

void WorkerEndpoint::Enable()
{
    enabled_ = true;
}

void WorkerEndpoint::Disable()
{
    enabled_ = false;
}

int32_t WorkerEndpoint::Compare(const WorkerEndpointPtr& another) const
{
    return Compare(another->GetHost(), another->GetPort(), another->GetProtocol());
}

int32_t WorkerEndpoint::Compare(const string& host, uint16_t port, int32_t protocol) const
{
    int32_t ret = host_.compare(host);
    if (ret == 0) {
        ret = (int32_t)port_ - (int32_t)port;
    }
    if (ret == 0) {
        ret = protocol_ - protocol;
    }
    return ret;
}

void WorkerEndpoint::Remove()
{
    WorkerEndpointRepoPtr repo = repo_.lock();
    if (repo != nullptr) {
        repo->Remove(host_, port_, protocol_);
    }
}
