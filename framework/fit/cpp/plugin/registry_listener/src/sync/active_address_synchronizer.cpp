/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for active address synchronizer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#include <sync/active_address_synchronizer.hpp>

#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_query_fitables_addresses/1.0.0/cplusplus/queryFitablesAddresses.hpp>

#include <fit/fit_log.h>

using namespace Fit;
using namespace Fit::Registry::Listener;

ActiveAddressSynchronizer::ActiveAddressSynchronizer(RegistryListenerPtr registryListener, uint32_t interval)
    : AddressSynchronizerBase(std::move(registryListener)), interval_(interval)
{
    GetRegistryListener()->ObserveFitablesSubscribed(
        std::bind(&ActiveAddressSynchronizer::OnFitablesSubscribed, this, std::placeholders::_1));
}

void ActiveAddressSynchronizer::Start()
{
    if (task_ == nullptr) {
        task_ = Task::Create([&]() { Synchronize(); });
        GetRegistryListener()->ScheduleTask(task_, interval_);
    }
}

void ActiveAddressSynchronizer::Stop()
{
    if (task_ != nullptr) {
        GetRegistryListener()->UnscheduleTask(task_);
        task_ = nullptr;
    }
}

void ActiveAddressSynchronizer::Synchronize()
{
    vector<FitableInfo> fitables = GetRegistryListener()->ListFitables();

    FIT_LOG_DEBUG("Start to query fitables addresses. [fitables=%zu]", fitables.size());
    for (auto& fitable : fitables) {
        FIT_LOG_DEBUG("-- fitable: %s:%s", fitable.genericableId.c_str(), fitable.fitableId.c_str());
    }

    auto guard = GetRegistryListener()->GetSpi()->QueryFitableInstances(fitables);
    AcceptChanges(guard.Get());
}

void ActiveAddressSynchronizer::OnFitablesSubscribed(const vector<FitableInfo>& fitableInfos)
{
    auto guard = GetRegistryListener()->GetSpi()->QueryFitableInstances(fitableInfos);
    AcceptChanges(guard.Get());
}
