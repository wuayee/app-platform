/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable unavailable endpoint.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#include <domain/fitable_unavailable_endpoint.hpp>

#include <repo/fitable_unavailable_endpoint_repo.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;

FitableUnavailableEndpoint::FitableUnavailableEndpoint(const FitableUnavailableEndpointRepoPtr& repo, string host,
    uint16_t port, uint32_t expiration) : repo_(repo), host_(std::move(host)), port_(port), expiration_(expiration)
{
}

RegistryListenerPtr FitableUnavailableEndpoint::GetRegistryListener() const
{
    FitableUnavailableEndpointRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetRegistryListener());
}

FitablePtr FitableUnavailableEndpoint::GetFitable() const
{
    FitableUnavailableEndpointRepoPtr repo = repo_.lock();
    return (repo == nullptr) ? nullptr : (repo->GetFitable());
}

const Fit::string& FitableUnavailableEndpoint::GetHost() const
{
    return host_;
}

uint16_t FitableUnavailableEndpoint::GetPort() const
{
    return port_;
}

int32_t FitableUnavailableEndpoint::Compare(const FitableUnavailableEndpointPtr& another) const
{
    return Compare(another->GetHost(), another->GetPort());
}

int32_t FitableUnavailableEndpoint::Compare(const string& host, uint16_t port) const
{
    int32_t ret = host_.compare(host);
    if (ret == 0) {
        ret = (int32_t)port_ - (int32_t)port;
    }
    return ret;
}

uint32_t FitableUnavailableEndpoint::GetExpiration() const
{
    return expiration_;
}

bool FitableUnavailableEndpoint::TryExpire()
{
    if (expiration_ > 0) {
        expiration_--;
    }
    return expiration_ < 1;
}

void FitableUnavailableEndpoint::SetExpiration(uint32_t expiration)
{
    expiration_ = expiration;
}

void FitableUnavailableEndpoint::Remove()
{
    FitableUnavailableEndpointRepoPtr repo = repo_.lock();
    if (repo != nullptr) {
        repo->Remove(GetHost(), GetPort());
    }
}
