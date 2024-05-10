/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/15
 * Notes:       :
 */
#include "fit_subscriber_repository_memory_node_sync.h"

using namespace Fit::Registry;
FitSubscriptionNodeSyncPtr FitSubscriptionNodeSyncPtrFactory::Create()
{
    constexpr const int32_t MIN_SERVICE_PER_TIME = 100; // 满100个推一次
    constexpr const int32_t MAX_INTERVAL_PER_TIME = 1000; // 不满100个，每秒推一次，单位ms
    return std::make_shared<FitSubscriberRepositoryMemoryNodeSync>(MIN_SERVICE_PER_TIME, MAX_INTERVAL_PER_TIME);
}