/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for fitable unavailable endpoint repo.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#include <memory/memory_fitable_unavailable_endpoint_repo.hpp>

#include <domain/fitable.hpp>
#include <domain/genericable.hpp>

#include <fit/internal/util/vector_utils.hpp>
#include <fit/fit_log.h>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;

MemoryFitableUnavailableEndpointRepo::MemoryFitableUnavailableEndpointRepo(const FitablePtr& fitable)
    : fitable_(fitable)
{
}

RegistryListenerPtr MemoryFitableUnavailableEndpointRepo::GetRegistryListener() const
{
    FitablePtr fitable = GetFitable();
    return (fitable == nullptr) ? nullptr : (fitable->GetRegistryListener());
}

FitablePtr MemoryFitableUnavailableEndpointRepo::GetFitable() const
{
    return fitable_.lock();
}

FitableUnavailableEndpointPtr MemoryFitableUnavailableEndpointRepo::Add(
    const string& host, uint16_t port, uint32_t expiration)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&host, &port](const FitableUnavailableEndpointPtr& existing) -> int32_t {
        return existing->Compare(host, port);
    };
    int32_t index = VectorUtils::BinarySearch<FitableUnavailableEndpointPtr>(endpoints_, compare);
    FitableUnavailableEndpointPtr endpoint;
    if (index < 0) {
        FitablePtr fitable = GetFitable();
        GenericablePtr genericable = (fitable == nullptr) ? nullptr : (fitable->GetGenericable());
        if (genericable != nullptr) {
            FIT_LOG_DEBUG("Add unavailable endpoint. [genericable=%s, fitable=%s, host=%s, port=%d]",
                genericable->GetId().c_str(), fitable->GetId().c_str(), host.c_str(), port);
            endpoint = std::make_shared<FitableUnavailableEndpoint>(
                shared_from_this(), host, port, expiration);
            VectorUtils::Insert(endpoints_, -1 - index, endpoint);
        }
    } else {
        endpoint = endpoints_[index];
        endpoint->SetExpiration(expiration);
    }
    return endpoint;
}

FitableUnavailableEndpointPtr MemoryFitableUnavailableEndpointRepo::Remove(const string& host, uint16_t port)
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&host, &port](const FitableUnavailableEndpointPtr& existing) -> int32_t {
        return existing->Compare(host, port);
    };
    int32_t index = VectorUtils::BinarySearch<FitableUnavailableEndpointPtr>(endpoints_, compare);
    FitableUnavailableEndpointPtr endpoint;
    if (index < 0) {
        endpoint = nullptr;
    } else {
        FitablePtr fitable = GetFitable();
        GenericablePtr genericable = (fitable == nullptr) ? nullptr : (fitable->GetGenericable());
        if (genericable != nullptr) {
            FIT_LOG_DEBUG("Remove unavailable endpoint. [genericable=%s, fitable=%s, host=%s, port=%d]",
                genericable->GetId().c_str(), fitable->GetId().c_str(), host.c_str(), port);
        }
        endpoint = VectorUtils::Remove(endpoints_, index);
    }
    return endpoint;
}

bool MemoryFitableUnavailableEndpointRepo::Contains(const string& host, uint16_t port) const
{
    lock_guard<mutex> guard {mutex_};
    auto compare = [&host, &port](const FitableUnavailableEndpointPtr& existing) -> int32_t {
        return existing->Compare(host, port);
    };
    return VectorUtils::BinarySearch<FitableUnavailableEndpointPtr>(endpoints_, compare) > -1;
}

vector<FitableUnavailableEndpointPtr> MemoryFitableUnavailableEndpointRepo::List() const
{
    lock_guard<mutex> guard {mutex_};
    return endpoints_;
}
