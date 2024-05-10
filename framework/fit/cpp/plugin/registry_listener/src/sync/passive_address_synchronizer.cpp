/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for passive address synchronizer.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/16
 */

#include <sync/passive_address_synchronizer.hpp>

#include <util/singleton_utils.hpp>

#include <fit/fit_log.h>

using namespace Fit;
using namespace Fit::Registry::Listener;

PassiveAddressSynchronizer::PassiveAddressSynchronizer(RegistryListenerPtr registryListener)
    : AddressSynchronizerBase(std::move(registryListener))
{
    GetRegistryListener()->ObserveFitablesSubscribed(
        std::bind(&PassiveAddressSynchronizer::OnFitablesSubscribed, this, std::placeholders::_1));
    GetRegistryListener()->ObserveFitablesUnsubscribed(
        std::bind(&PassiveAddressSynchronizer::OnFitablesUnsubscribed, this, std::placeholders::_1));
}

void PassiveAddressSynchronizer::Start()
{
    if (started_) {
        FIT_LOG_DEBUG("The passive address synchronizer has already been started.");
        return;
    }
    started_ = true;
    GetRegistryListener()->GetSpi()->SubscribeFitablesChanged(GetCallback());
    vector<FitableInfo> fitableInfos = GetRegistryListener()->ListFitables();
    OnFitablesSubscribed(fitableInfos);
}

void PassiveAddressSynchronizer::Stop()
{
    GetRegistryListener()->GetSpi()->UnsubscribeFitablesChanged(GetCallback());
}

void PassiveAddressSynchronizer::Synchronize(const vector<FitableInstance>& fitableInstances)
{
    AcceptChanges(fitableInstances);
}

void PassiveAddressSynchronizer::OnFitablesSubscribed(const vector<FitableInfo>& fitableInfos)
{
    if (fitableInfos.empty()) {
        FIT_LOG_DEBUG("Skip to subscribe fitables from registry server because no fitable required.");
        return;
    }
    FitableInstanceListGuard guard = GetRegistryListener()->GetSpi()->SubscribeFitables(fitableInfos);
    if (guard.GetResultCode() == FIT_OK) {
        FIT_LOG_DEBUG("Successful to subscribe fitables from registry server. [count=%lu]", fitableInfos.size());
        Synchronize(guard.Get());
    } else {
        FIT_LOG_WARN("Failed to subscribe fitables from registry listener. [error=%x]", guard.GetResultCode());
    }
}

void PassiveAddressSynchronizer::OnFitablesUnsubscribed(const vector<FitableInfo>& fitableInfos)
{
    auto ret = GetRegistryListener()->GetSpi()->UnsubscribeFitables(fitableInfos);
    if (ret == FIT_OK) {
        FIT_LOG_DEBUG("Successful to unsubscribe fitables from registry server. [count=%lu]", fitableInfos.size());
    } else {
        FIT_LOG_WARN("Failed to unsubscribe fitables from registry listener. [error=%x]", ret);
    }
}

const FitablesChangedCallbackPtr& PassiveAddressSynchronizer::GetCallback()
{
    return SingletonUtils::Get<FitablesChangedCallback>(callback_, mutex_,
        [&]() -> FitablesChangedCallbackPtr {
            return FitablesChangedCallback::Create([&](const vector<FitableInstance>& instances) -> FitCode {
                FIT_LOG_DEBUG("Fitables changes received. [count=%lu]", instances.size());
                Synchronize(instances);
                return FIT_OK;
            });
        });
}
