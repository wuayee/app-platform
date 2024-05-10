/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020-09-15
 * Notes:       :
 */
#ifndef FIT_SUBSCRIPTION_REPOSITORY_ASYN_DECORATOR_H
#define FIT_SUBSCRIPTION_REPOSITORY_ASYN_DECORATOR_H
#include <fit/internal/registry/repository/fit_subscription_repository_decorator.h>
#include <thread>
#include <atomic>
#include <condition_variable>
#include <fit/stl/mutex.hpp>
#include <fit/stl/unordered_map.hpp>
#include <fit/stl/list.hpp>
#include <fit/memory/fit_base.hpp>
namespace Fit {
namespace Registry {
class FitSubscriptionRepositoryAsynDecorator : public FitSubscriptionRepositoryDecorator {
    struct SubscriptionInfo : public FitBase {
        fit_fitable_key_t key;
        listener_t listener;
    };
    struct AsyncSubscriptionInfo : public FitBase {
        enum class SubscriptionState {
            STATE_INVALID = 0,
            STATE_SAVE = 1,
            STATE_REMOVE = 2
        };
        SubscriptionState state;
        Fit::vector<SubscriptionInfo> subscriptionInfoSet;
    };

public:
    explicit FitSubscriptionRepositoryAsynDecorator(fit_subscription_repository_ptr subscriptionRepo);
    ~FitSubscriptionRepositoryAsynDecorator() override;
    bool Start() override;
    bool Stop() override;
    int32_t insert_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener) override;
    int32_t remove_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener) override;
    db_subscription_set query_subscription_set(const fit_fitable_key_t &key) override;
    listener_set query_listener_set(const fit_fitable_key_t &key) override;
    int32_t query_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener,
        db_subscription_entry_t &resultSubscriptionEntry) const override;
private:
    int32_t InsertSubscriptionInfo(const SubscriptionInfo &subscriptionInfo);
    int32_t RemoveSubscriptionInfo(const SubscriptionInfo &subscriptionInfo);
    bool AddSyncList(const SubscriptionInfo &subscriptionInfo, AsyncSubscriptionInfo::SubscriptionState state);
private:
    Fit::mutex asyncSubscriptionInfoSetMutex_;
    std::condition_variable asyncSubscriptionInfoSetCondition_;
    std::atomic<bool> exit_ {false};
    Fit::list<AsyncSubscriptionInfo> asyncSubscriptionInfoSet_;
    std::thread worker_;
    Fit::unordered_map<int32_t, std::function<void(const SubscriptionInfo&)>> operatorSet_;
};
}
}
#endif