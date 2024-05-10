/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/17
 * Notes:       :
 */

#include "fitable_discovery_default_impl.hpp"
#include <algorithm>
#include <fit/fit_log.h>
#include <fit/internal/runtime/runtime.hpp>
#include <fit/internal/plugin/plugin_manager.hpp>

#include <genericable/com_huawei_fit_hakuna_kernel_registry_listener_get_fitable_addresses/1.0.0/cplusplus/getFitableAddresses.hpp>

namespace Fit {
namespace Framework {
using Annotation::PopFitableDetailCache;
void FitableDiscoveryDefaultImpl::RegisterLocalFitable(Annotation::FitableDetailPtrList details)
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMtx_);
    for (auto &detail : details) {
        auto iter = localFitables_.find(detail->GetGenericId());
        if (iter == localFitables_.end()) {
            localFitables_.insert(std::make_pair(detail->GetGenericId(), Annotation::FitableDetailPtrList{detail}));
            continue;
        }
        if (std::find_if(iter->second.begin(), iter->second.end(),
            [detail](const Annotation::FitableDetailPtr &item) {
                return item.get() == detail.get();
            }) == iter->second.end()) {
            iter->second.push_back(detail);
        }
    }
}

void FitableDiscoveryDefaultImpl::UnRegisterLocalFitable(const Annotation::FitableDetailPtrList &details)
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMtx_);
    for (auto &detail : details) {
        auto iter = localFitables_.find(detail->GetGenericId());
        if (iter == localFitables_.end()) {
            continue;
        }
        auto removeIter = std::remove_if(iter->second.begin(), iter->second.end(),
            [detail](const Annotation::FitableDetailPtr &item) {
                return item.get() == detail.get();
            });
        iter->second.erase(removeIter, iter->second.end());
        if (iter->second.empty()) {
            localFitables_.erase(iter);
        }
    }
}

Annotation::FitableDetailPtrList FitableDiscoveryDefaultImpl::GetLocalFitable(const Framework::Fitable& fitable)
{
    Annotation::FitableDetailPtrList result {};
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMtx_);
    auto iter = localFitables_.find(fitable.genericId);
    if (iter != localFitables_.end()) {
        std::copy_if(iter->second.begin(), iter->second.end(), std::back_inserter(result),
            [&fitable, this](const Annotation::FitableDetailPtr& item) {
                return fitable.fitableId == item->GetFitableId() && disabledFitables_.find(
                    std::make_pair(fitable.genericId, fitable.fitableId)) == disabledFitables_.end();
            });
    }
    return result;
}

Annotation::FitableDetailPtrList FitableDiscoveryDefaultImpl::GetLocalFitableByGenericId(const Fit::string &genericId)
{
    Annotation::FitableDetailPtrList result {};
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMtx_);
    auto iter = localFitables_.find(genericId);
    if (iter != localFitables_.end()) {
        std::copy_if(iter->second.begin(), iter->second.end(), std::back_inserter(result),
            [this](const Annotation::FitableDetailPtr &item) {
                return disabledFitables_.find(std::make_pair(item->GetGenericId(), item->GetFitableId())) ==
                    disabledFitables_.end();
            });
    }
    return result;
}

Annotation::FitableDetailPtrList FitableDiscoveryDefaultImpl::GetAllLocalFitables()
{
    Annotation::FitableDetailPtrList result {};
    Fit::shared_lock<Fit::shared_mutex> lock(sharedMtx_);
    for (auto &detail : localFitables_) {
        std::copy_if(detail.second.begin(), detail.second.end(), std::back_inserter(result),
            [this](const Annotation::FitableDetailPtr &item) {
                return disabledFitables_.find(std::make_pair(item->GetGenericId(), item->GetFitableId()))
                    == disabledFitables_.end();
            });
    }
    return result;
}

void FitableDiscoveryDefaultImpl::ClearAllLocalFitables()
{
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMtx_);
    localFitables_.clear();
    FIT_LOG_CORE("ClearAllLocalFitables.");
}

Annotation::FitableDetailPtrList FitableDiscoveryDefaultImpl::DisableFitables(
    const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds)
{
    Annotation::FitableDetailPtrList result {};
    Fit::unique_lock<Fit::shared_mutex> lock(sharedMtx_);
    auto iter = localFitables_.find(genericId);
    if (iter == localFitables_.end()) {
        return result;
    }

    for (const auto &fitable : iter->second) {
        if (!fitIds.empty()) {
            auto it = std::find_if(fitIds.begin(), fitIds.end(), [&fitable](const Fit::string &fitId) {
                return fitable->GetFitableId() == fitId;
            });
            if (it == fitIds.end()) {
                continue;
            }
        }
        auto ret = disabledFitables_.insert(std::make_pair(genericId, fitable->GetFitableId()));
        if (ret.second) {
            result.push_back(fitable);
        }
    }
    return result;
}

Annotation::FitableDetailPtrList FitableDiscoveryDefaultImpl::EnableFitables(
    const Fit::string &genericId, const Fit::vector<Fit::string> &fitIds)
{
    Annotation::FitableDetailPtrList result {};
    Fit::unique_lock<shared_mutex> lock(sharedMtx_);
    auto iter = localFitables_.find(genericId);
    if (iter == localFitables_.end()) {
        return result;
    }

    for (const auto &fitable : iter->second) {
        if (!fitIds.empty()) {
            auto it = std::find_if(fitIds.begin(), fitIds.end(), [&genericId, &fitable](const Fit::string &item) {
                return fitable->GetGenericId() == genericId && fitable->GetFitableId() == item;
            });
            if (it == fitIds.end()) {
                continue;
            }
        }
        auto ret = disabledFitables_.erase(std::make_pair(fitable->GetGenericId(), fitable->GetFitableId()));
        if (ret > 0) {
            result.push_back(fitable);
        }
    }
    return result;
}

FitableDiscoveryDefaultImpl::FitableDiscoveryDefaultImpl() = default;

bool FitableDiscoveryDefaultImpl::Start()
{
    auto pluginManager = GetRuntime().GetElementIs<Plugin::PluginManager>();
    if (pluginManager == nullptr) {
        FIT_LOG_ERROR("Need plugin manager.");
        return false;
    }
    pluginManager->ObservePluginsStarted(
        [this](const Plugin::Plugin &plugin) {
            RegisterLocalFitable(plugin.GetFitables());
        });
    pluginManager->ObservePluginsStopped(
        [this](const Plugin::Plugin &plugin) {
            UnRegisterLocalFitable(plugin.GetFitables());
        });

    receiver_.Register = bind(&FitableDiscovery::RegisterLocalFitable, this, std::placeholders::_1);
    receiver_.UnRegister = bind(&FitableDiscovery::UnRegisterLocalFitable, this, std::placeholders::_1);

    oldReceiver_ = FitableDetailFlowTo(&receiver_);
    RegisterLocalFitable(PopFitableDetailCache());
    FIT_LOG_INFO("Fitable discovery is started.");
    return true;
}

bool FitableDiscoveryDefaultImpl::Stop()
{
    FitableDetailFlowTo(oldReceiver_);
    ClearAllLocalFitables();
    return true;
}
}
} // LCOV_EXCL_LINE
