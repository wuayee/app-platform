/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/17
 * Notes:       :
 */
#ifndef FIT_SUBSCRIPTION_GARBAGE_COLLECT_BY_CYCLE_H
#define FIT_SUBSCRIPTION_GARBAGE_COLLECT_BY_CYCLE_H
#include <registry_server_memory/subscriber/include/fit_subscription_garbage_collect.h>
#include <fit/internal/util/thread/fit_timer.h>
#include <fit/stl/mutex.hpp>
namespace Fit {
namespace Registry {
class FitSubscriptionGarbageCollectByCycle : public FitSubscriptionGarbageCollect {
public:
    FitSubscriptionGarbageCollectByCycle(FitSubscriptionMemoryRepositoryPtr subscriptionRepository,
        FitRegistryMemoryRepositoryPtr registryRepository, std::shared_ptr<Fit::timer> timer);
    ~FitSubscriptionGarbageCollectByCycle() override;
    int32_t Init() override;
    int32_t UnInit() override;
    int32_t AddDyingListenerIds(const Fit::unordered_set<Fit::string>& ids) override;
    int32_t RemoveDyingListenerIds(const Fit::unordered_set<Fit::string>& ids) override;
    int32_t UpdateDyingListenerIds(const ListenerSet& listeners) override;
private:
    Fit::unordered_set<Fit::string> GetOffline(const ListenerSet& listeners);
private:
    fit_subscription_repository_ptr subscriptionRepository_ {nullptr};
    FitRegistryMemoryRepositoryPtr registryRepository_ {nullptr};
    std::shared_ptr<Fit::timer> timer_ {nullptr};
    uint32_t subscriptionDeleteHandle_ {0};
    Fit::unordered_set<Fit::string> dyingListenerIds_ {};
    Fit::mutex dyingListenerIdsMutex_ {};
};
}
}
#endif