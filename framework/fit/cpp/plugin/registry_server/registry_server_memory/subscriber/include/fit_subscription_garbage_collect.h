/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2023/08/17
 * Notes:       :
 */
#ifndef FIT_SUBSCRIPTION_GARBAGE_COLLECT_H
#define FIT_SUBSCRIPTION_GARBAGE_COLLECT_H
#include <fit/internal/registry/fit_registry_entities.h>
#include <fit/stl/unordered_set.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/memory.hpp>
#include <fit/internal/registry/repository/fit_subscription_memory_repository.h>
#include <fit/internal/registry/repository/fit_registry_memory_repository.h>
namespace Fit {
namespace Registry {
class FitSubscriptionGarbageCollect {
public:
    virtual ~FitSubscriptionGarbageCollect() = default;
    virtual int32_t Init() = 0;
    virtual int32_t UnInit() = 0;
    virtual int32_t AddDyingListenerIds(const Fit::unordered_set<Fit::string>& ids) = 0;
    virtual int32_t RemoveDyingListenerIds(const Fit::unordered_set<Fit::string>& ids) = 0;
    virtual int32_t UpdateDyingListenerIds(const ListenerSet& listeners) = 0;
};

using FitSubscriptionGarbageCollectPtr = Fit::shared_ptr<FitSubscriptionGarbageCollect>;

class FitSubscriptionGarbageCollectFactory {
public:
    static FitSubscriptionGarbageCollectPtr Create(FitSubscriptionMemoryRepositoryPtr subscriptionRepository,
        FitRegistryMemoryRepositoryPtr registryRepository);
};
}
}
#endif