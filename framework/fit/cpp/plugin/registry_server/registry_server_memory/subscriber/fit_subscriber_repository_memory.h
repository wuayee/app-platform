/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/09
 * Notes:       :
 */
#ifndef FIT_SUBSCRIBER_REPOSITORY_MEMORY_H
#define FIT_SUBSCRIBER_REPOSITORY_MEMORY_H
#include <fit/stl/mutex.hpp>
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/internal/registry/repository/fit_subscription_memory_repository.h>
#include <fit/internal/util/thread/fit_timer.h>

namespace Fit {
namespace Registry {
class FitSubscriberRepositoryMemory : public FitSubscriptionMemoryRepository {
public:
    explicit FitSubscriberRepositoryMemory(fit_subscription_repository_ptr subscriptionRepository,
        FitSubscriptionNodeSyncPtr fitSubscriptionNodeSyncPtr);
    ~FitSubscriberRepositoryMemory() override;
    bool Start() override;
    bool Stop() override;
    int32_t insert_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener) override;

    int32_t remove_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener) override;

    db_subscription_set query_subscription_set(const fit_fitable_key_t &key) override;

    db_subscription_set query_all_subscriptions() const override;

    listener_set query_listener_set(const fit_fitable_key_t &key) override;

    int32_t query_subscription_entry(const fit_fitable_key_t &key, const listener_t &listener,
        db_subscription_entry_t &resultSubscriptionEntry) const override;

    int32_t SyncInsertSubscriptionEntry(const fit_fitable_key_t &key, const listener_t &listener) override;
    int32_t SyncRemoveSubscriptionEntry(const fit_fitable_key_t &key, const listener_t &listener) override;

    Fit::unordered_set<Fit::string> query_all_listener_ids() const override;
    ListenerSet query_all_listeners() const override;
    int32_t remove_subscription_entry(const Fit::unordered_set<Fit::string>& listenerIds) override;
private:
    bool IsListenerExist(const fit_fitable_key_t &key, const listener_t &listener);
    db_subscription_set query_subscriptions_by_ids(const Fit::unordered_set<Fit::string>& listenerIds);
private:
    void InitMemory();
    void CreateSyncSubscriptionWithDbTask();
    void SyncSubscriptionInfoWithDb();
private:
    void UpdateSyncCount(const fit_fitable_key_t &key, const listener_t &listener, uint64_t syncCount);
    void MarkDBToMemoryOrder(db_subscription_set& subscriptionSet, uint64_t syncCount);
    db_subscription_set GetSubscriptionExcludeDB(uint64_t syncCount);
private:
    mutable Fit::mutex fitableListenerMapMutex_;
    FitableListenerMap fitableListenerMap_;
    fit_subscription_repository_ptr subscriptionRepository_ {nullptr};
    FitSubscriptionNodeSyncPtr fitSubscriptionNodeSyncPtr_ {nullptr};
    std::shared_ptr<Fit::timer> timer_;
    Fit::timer::timer_handle_t syncSubscriptionInfoFromDbTaskHandle_ {Fit::timer::INVALID_TASK_ID};
    Fit::timer::timer_handle_t initHandle_ {Fit::timer::INVALID_TASK_ID};
    std::atomic<uint64_t> syncCount_ {DEFAULT_SYNC_COUNT};
};
}
}
#endif