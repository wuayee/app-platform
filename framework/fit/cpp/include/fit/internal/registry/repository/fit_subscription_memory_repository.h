/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/15
 * Notes:       :
 */
#ifndef FIT_SUBSCRIPTION_MEMORY_REPOSITORY_H
#define FIT_SUBSCRIPTION_MEMORY_REPOSITORY_H
#include "fit_subscription_repository.h"
#include "fit_subscription_node_sync.h"
class FitSubscriptionMemoryRepository : public fit_subscription_repository {
public:
    virtual ~FitSubscriptionMemoryRepository() = default;
    virtual int32_t SyncInsertSubscriptionEntry(
        const fit_fitable_key_t &key, const listener_t &listener) = 0;
    virtual int32_t SyncRemoveSubscriptionEntry(
        const fit_fitable_key_t &key, const listener_t &listener) = 0;
};
using FitSubscriptionMemoryRepositoryPtr = std::shared_ptr<FitSubscriptionMemoryRepository>;
class FitSubscriptionMemoryRepositoryFactory final {
public:
    static FitSubscriptionMemoryRepositoryPtr Create(fit_subscription_repository_ptr subscriptionRepository,
        FitSubscriptionNodeSyncPtr fitSubscriptionNodeSyncPtr);
};
#endif