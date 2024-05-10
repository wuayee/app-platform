/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/09
 * Notes:       :
 */
#include "fit_subscriber_repository_memory.h"
FitSubscriptionMemoryRepositoryPtr FitSubscriptionMemoryRepositoryFactory::Create(
    fit_subscription_repository_ptr subscriptionRepository,
    FitSubscriptionNodeSyncPtr fitSubscriptionNodeSyncPtr)
{
    return std::make_shared<Fit::Registry::FitSubscriberRepositoryMemory>(subscriptionRepository,
        fitSubscriptionNodeSyncPtr);
}