/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for unavailable endpoints synchronizer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/22
 */

#include <sync/unavailable_endpoints_synchronizer.hpp>

#include <fit/fit_log.h>
#include <fit/internal/util/vector_utils.hpp>

using namespace Fit;
using namespace Fit::Registry::Listener;
using namespace Fit::Util;
namespace {
void InsertUnavailableEndpoint(const FitablePtr& fitable, vector<FitableUnavailableEndpointPtr>& all)
{
    auto unavailableEndpoints = fitable->GetUnavailableEndpoints()->List();
    all.reserve(all.size() + unavailableEndpoints.size());
    for (auto& unavailableEndpoint : unavailableEndpoints) {
        auto compare = [&unavailableEndpoint](const FitableUnavailableEndpointPtr& existing) -> int32_t {
            return existing->Compare(unavailableEndpoint);
        };
        int32_t index = VectorUtils::BinarySearch<FitableUnavailableEndpointPtr>(all, compare);
        if (index < 0) {
            VectorUtils::Insert(all, -1 - index, std::move(unavailableEndpoint));
        }
    }
}
}
UnavailableEndpointsSynchronizer::UnavailableEndpointsSynchronizer(RegistryListenerPtr registryListener)
    : AddressSynchronizerBase(std::move(registryListener))
{
}

void UnavailableEndpointsSynchronizer::Start()
{
    if (task_ == nullptr) {
        FIT_LOG_DEBUG("Schedule task to synchronize unavailable endpoints for fitables.");
        task_ = Task::Create([&]() -> void {
            Synchronize();
        });
        GetRegistryListener()->ScheduleTask(task_, 1);
    }
}

void UnavailableEndpointsSynchronizer::Stop()
{
    if (task_ != nullptr) {
        GetRegistryListener()->UnscheduleTask(task_);
        task_ = nullptr;
        FIT_LOG_DEBUG("Unschedule task to synchronize unavailable endpoints for fitables.");
    }
}

void UnavailableEndpointsSynchronizer::Synchronize()
{
    auto unavailableEndpoints = ListUnavailableEndpoints();
    RemoveUnexpired(unavailableEndpoints);
    if (unavailableEndpoints.empty()) {
        return;
    }
    FIT_LOG_DEBUG("Start to synchronize unavailable fitable addresses. [count=%lu]", unavailableEndpoints.size());
    vector<FitablePtr> fitables = CollectFitables(unavailableEndpoints);
    vector<FitableInfo> fitableInfos = ToFitableInfos(fitables);
    auto guard = GetRegistryListener()->GetSpi()->QueryFitableInstances(fitableInfos);
    if (guard.GetResultCode() == FIT_OK) {
        AcceptChanges(guard.Get());
        for (auto& unavailableEndpoint : unavailableEndpoints) {
            unavailableEndpoint->Remove();
        }
    }
}

vector<FitableUnavailableEndpointPtr> UnavailableEndpointsSynchronizer::ListUnavailableEndpoints() const
{
    vector<FitableUnavailableEndpointPtr> all {};
    auto genericables = GetRegistryListener()->GetGenericables()->List();
    for (auto& genericable : genericables) {
        auto fitables = genericable->GetFitables()->List();
        for (auto& fitable : fitables) {
            InsertUnavailableEndpoint(fitable, all);
        }
    }
    return all;
}

vector<FitablePtr> UnavailableEndpointsSynchronizer::CollectFitables(
    const Fit::vector<FitableUnavailableEndpointPtr>& endpoints)
{
    vector<FitablePtr> fitables {};
    for (auto& unavailableEndpoint : endpoints) {
        FitablePtr fitable = unavailableEndpoint->GetFitable();
        if (fitable == nullptr) {
        } else {
            auto compare = [&fitable](const FitablePtr& existing) -> int32_t {
                return existing->Compare(fitable);
            };
            int32_t index = VectorUtils::BinarySearch<FitablePtr>(fitables, compare);
            if (index < 0) {
                VectorUtils::Insert(fitables, -1 - index, std::move(fitable));
            }
        }
    }
    return fitables;
}

vector<FitableInfo> UnavailableEndpointsSynchronizer::ToFitableInfos(const vector<FitablePtr>& fitables)
{
    vector<FitableInfo> infos = {};
    infos.reserve(fitables.size());
    for (const auto& fitable : fitables) {
        GenericablePtr genericable = fitable->GetGenericable();
        if (genericable != nullptr) {
            FitableInfo info {};
            info.genericableId = genericable->GetId();
            info.genericableVersion = genericable->GetVersion();
            info.fitableId = fitable->GetId();
            info.fitableVersion = fitable->GetVersion();
            infos.push_back(info);
        }
    }
    return infos;
}

void UnavailableEndpointsSynchronizer::RemoveUnexpired(vector<FitableUnavailableEndpointPtr>& unavailableEndpoints)
{
    for (auto iter = unavailableEndpoints.begin(); iter != unavailableEndpoints.end();) {
        if (!(*iter)->TryExpire()) {
            iter = unavailableEndpoints.erase(iter);
        } else {
            iter++;
        }
    }
}
