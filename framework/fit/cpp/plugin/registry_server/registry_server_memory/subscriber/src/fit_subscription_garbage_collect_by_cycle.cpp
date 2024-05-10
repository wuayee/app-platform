/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/17
 * Notes:       :
 */
#include <registry_server_memory/subscriber/include/fit_subscription_garbage_collect_by_cycle.h>
#include <genericable/com_huawei_fit_hakuna_kernel_registry_server_notify_fitables/1.0.0/cplusplus/notifyFitables.hpp>
#include <fit/fit_code.h>
#include <fit/fit_log.h>
namespace Fit {
namespace Registry {
constexpr const size_t SECOND_TO_MILLI_SECOND = 1000;
constexpr const size_t CHECK_SUBSCRIPTION_INTERVAL = 5 * 60; // 检测订阅信息listener是否离线周期3min
constexpr const char* NOTIFY_FITABLE_ID = "7847dac3feac4e549e85670341146b8a";

FitSubscriptionGarbageCollectByCycle::FitSubscriptionGarbageCollectByCycle(
    FitSubscriptionMemoryRepositoryPtr subscriptionRepository, FitRegistryMemoryRepositoryPtr registryRepository,
    std::shared_ptr<Fit::timer> timer)
    : subscriptionRepository_(std::move(subscriptionRepository)), registryRepository_(std::move(registryRepository)),
      timer_(std::move(timer))
{
}
FitSubscriptionGarbageCollectByCycle::~FitSubscriptionGarbageCollectByCycle()
{
    UnInit();
}

Fit::unordered_set<Fit::string> FitSubscriptionGarbageCollectByCycle::GetOffline(const ListenerSet& listeners)
{
    if (!registryRepository_) {
        FIT_LOG_ERROR("Service repo is null.");
        return {};
    }
    fit_fitable_key_t key;
    key.generic_id = fit::hakuna::kernel::registry::server::notifyFitables::GENERIC_ID;
    key.generic_version = "1.0.0";

    Fit::unordered_set<Fit::string> offlineListenerIds {};
    for (const auto& listener : listeners) {
        key.fitable_id = listener.fitable_id;
        if (registryRepository_->QueryService(key, listener.address).empty()) {
            offlineListenerIds.insert(listener.address.id);
        }
    }
    return offlineListenerIds;
}

int32_t FitSubscriptionGarbageCollectByCycle::Init()
{
    if (timer_ == nullptr) {
        FIT_LOG_ERROR("Timer is null.");
        return FIT_ERR_FAIL;
    }
    subscriptionDeleteHandle_ = timer_->set_interval(
        CHECK_SUBSCRIPTION_INTERVAL * SECOND_TO_MILLI_SECOND,
        [this]() {
            UpdateDyingListenerIds(subscriptionRepository_->query_all_listeners());
        });
    return FIT_OK;
}
int32_t FitSubscriptionGarbageCollectByCycle::UnInit()
{
    if (timer_ == nullptr) {
        FIT_LOG_ERROR("Timer is null.");
        return FIT_ERR_FAIL;
    }
    timer_->remove(subscriptionDeleteHandle_);
    return FIT_OK;
}
int32_t FitSubscriptionGarbageCollectByCycle::AddDyingListenerIds(
    const Fit::unordered_set<Fit::string>& listenersIds)
{
    Fit::unique_lock<Fit::mutex> lock(dyingListenerIdsMutex_);
    for (const auto& id : listenersIds) {
        dyingListenerIds_.insert(id);
    }
    return FIT_OK;
}
int32_t FitSubscriptionGarbageCollectByCycle::RemoveDyingListenerIds(
    const Fit::unordered_set<Fit::string>& listenersIds)
{
    Fit::unique_lock<Fit::mutex> lock(dyingListenerIdsMutex_);
    for (const auto& id : listenersIds) {
        dyingListenerIds_.erase(id);
    }
    return FIT_OK;
}
int32_t FitSubscriptionGarbageCollectByCycle::UpdateDyingListenerIds(
    const ListenerSet& listeners)
{
    // 分为三部分
    // 1. dyingListenerIds_中存在，offlineListenerIds不存在，listener已上线，从dyingListenerIds_中删除
    // 2. dyingListenerIds_中存在，offlineListenerIds存在，listener未上线，
    // 从dyingListenerIds_中删除，并超时删除订阅信息
    // 3. dyingListenerIds_中不存在，offlineListenerIds存在，listener新下线，加入dyingListenerIds_中
    Fit::unordered_set<Fit::string> diedListenerIds {};
    Fit::unordered_set<Fit::string> offlineListenerIds = GetOffline(listeners);
    {
        Fit::unique_lock<Fit::mutex> lock(dyingListenerIdsMutex_);
        for (auto it = dyingListenerIds_.begin(); it != dyingListenerIds_.end(); ++it) {
            // 离线超时，删除订阅信息
            if (offlineListenerIds.count(*it) != 0) {
                FIT_LOG_INFO("Clear listener. (listener=%s).", it->c_str());
                diedListenerIds.insert(*it); // 第2种情况
                offlineListenerIds.erase(*it); // 第3中情况，剩余的是新offline的listener
            }
        }
        dyingListenerIds_.swap(offlineListenerIds);
    }
    // 第2中情况从内存删除删除订阅信息
    subscriptionRepository_->remove_subscription_entry(diedListenerIds);
    return FIT_OK;
}

FitSubscriptionGarbageCollectPtr FitSubscriptionGarbageCollectFactory::Create(
    FitSubscriptionMemoryRepositoryPtr subscriptionRepository,
    FitRegistryMemoryRepositoryPtr registryRepository)
{
    return Fit::make_shared<FitSubscriptionGarbageCollectByCycle>(
        subscriptionRepository, registryRepository, Fit::timer_instance());
}
}
}