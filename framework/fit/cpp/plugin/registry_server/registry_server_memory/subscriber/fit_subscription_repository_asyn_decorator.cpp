/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2020-09-15
 * Notes:       :
 */
#include "fit_subscription_repository_asyn_decorator.h"
#include <fit/fit_log.h>

namespace Fit {
namespace Registry {
FitSubscriptionRepositoryAsynDecorator::FitSubscriptionRepositoryAsynDecorator(
    fit_subscription_repository_ptr subscriptionRepo)
    : FitSubscriptionRepositoryDecorator(subscriptionRepo)
{
}

FitSubscriptionRepositoryAsynDecorator::~FitSubscriptionRepositoryAsynDecorator()
{
    Stop();
}
bool FitSubscriptionRepositoryAsynDecorator::Start()
{
    Stop();
    exit_.store(false);
    operatorSet_[static_cast<int32_t>(AsyncSubscriptionInfo::SubscriptionState::STATE_SAVE)]
        = [this](const SubscriptionInfo &subscriptionInfo) {
            InsertSubscriptionInfo(subscriptionInfo);
        };
    operatorSet_[static_cast<int32_t>(AsyncSubscriptionInfo::SubscriptionState::STATE_REMOVE)]
        = [this](const SubscriptionInfo &subscriptionInfo) {
            RemoveSubscriptionInfo(subscriptionInfo);
        };
    worker_ = std::thread([this]() {
        while (true) {
            Fit::unique_lock<Fit::mutex> lock(asyncSubscriptionInfoSetMutex_);
            asyncSubscriptionInfoSetCondition_.wait(lock, [this]() {
                return !asyncSubscriptionInfoSet_.empty() || exit_.load();
            });
            if (exit_.load()) {
                return;
            }
            auto asyncSubscription = std::move(asyncSubscriptionInfoSet_.front());
            asyncSubscriptionInfoSet_.erase(asyncSubscriptionInfoSet_.begin());
            lock.unlock();

            auto operatorIt = operatorSet_.find(static_cast<int32_t>(asyncSubscription.state));
            if (operatorIt == operatorSet_.end()) {
                continue;
            }
            // 订阅无批量处理接口，单个处理
            for (const auto& subscriptionInfo : asyncSubscription.subscriptionInfoSet) {
                operatorIt->second(subscriptionInfo);
            }
        }
    });
    FIT_LOG_INFO("Subscriber async repo start.");
    return true;
}
bool FitSubscriptionRepositoryAsynDecorator::Stop()
{
    exit_.store(true);
    asyncSubscriptionInfoSetCondition_.notify_one();
    if (worker_.joinable()) {
        worker_.join();
    }
    FIT_LOG_INFO("Subscriber async repo stop.");
    return true;
}

int32_t FitSubscriptionRepositoryAsynDecorator::insert_subscription_entry(
    const fit_fitable_key_t &key, const listener_t &listener)
{
    SubscriptionInfo info {};
    info.key = key;
    info.listener = listener;
    AddSyncList(info, AsyncSubscriptionInfo::SubscriptionState::STATE_SAVE);
    return REGISTRY_SUCCESS;
}

int32_t FitSubscriptionRepositoryAsynDecorator::remove_subscription_entry(
    const fit_fitable_key_t &key, const listener_t &listener)
{
    SubscriptionInfo info {};
    info.key = key;
    info.listener = listener;
    AddSyncList(info, AsyncSubscriptionInfo::SubscriptionState::STATE_REMOVE);
    return REGISTRY_SUCCESS;
}

db_subscription_set FitSubscriptionRepositoryAsynDecorator::query_subscription_set(
    const fit_fitable_key_t &key)
{
    return FitSubscriptionRepositoryDecorator::query_subscription_set(key);
}

listener_set FitSubscriptionRepositoryAsynDecorator::query_listener_set(
    const fit_fitable_key_t &key)
{
    return FitSubscriptionRepositoryDecorator::query_listener_set(key);
}

int32_t FitSubscriptionRepositoryAsynDecorator::query_subscription_entry(
    const fit_fitable_key_t &key, const listener_t &listener,
    db_subscription_entry_t &resultSubscriptionEntry) const
{
    return FitSubscriptionRepositoryDecorator::query_subscription_entry(key, listener, resultSubscriptionEntry);
}

int32_t FitSubscriptionRepositoryAsynDecorator::InsertSubscriptionInfo(
    const SubscriptionInfo &subscriptionInfo)
{
    int times = RETRY_OPERATE_DB_TIMES;
    int32_t ret = REGISTRY_SUCCESS;
    while (times > 0) {
        ret = FitSubscriptionRepositoryDecorator::insert_subscription_entry(
            subscriptionInfo.key, subscriptionInfo.listener);
        if (ret == REGISTRY_SUCCESS) {
            break;
        }
        FIT_LOG_DEBUG("InsertSubscriptionInfo cur time is %d.", times);
        times--;
        std::this_thread::sleep_for(std::chrono::seconds(1));
    }
    return FitSubscriptionRepositoryDecorator::insert_subscription_entry(
        subscriptionInfo.key, subscriptionInfo.listener);
}
int32_t FitSubscriptionRepositoryAsynDecorator::RemoveSubscriptionInfo(
    const SubscriptionInfo &subscriptionInfo)
{
    return FitSubscriptionRepositoryDecorator::remove_subscription_entry(
        subscriptionInfo.key, subscriptionInfo.listener);
}
bool FitSubscriptionRepositoryAsynDecorator::AddSyncList(
    const SubscriptionInfo &subscriptionInfo, AsyncSubscriptionInfo::SubscriptionState state)
{
    Fit::unique_lock<Fit::mutex> lock(asyncSubscriptionInfoSetMutex_);
    if (!asyncSubscriptionInfoSet_.empty() && asyncSubscriptionInfoSet_.back().state == state) {
        asyncSubscriptionInfoSet_.back().subscriptionInfoSet.push_back(subscriptionInfo);
    } else {
        AsyncSubscriptionInfo asyncSubscriptionInfo {};
        asyncSubscriptionInfo.state = state;
        asyncSubscriptionInfo.subscriptionInfoSet.emplace_back(subscriptionInfo);
        asyncSubscriptionInfoSet_.emplace_back(asyncSubscriptionInfo);
    }
    asyncSubscriptionInfoSetCondition_.notify_one();
    lock.unlock();
    return true;
}
}
} // LCOV_EXCL_BR_LINE