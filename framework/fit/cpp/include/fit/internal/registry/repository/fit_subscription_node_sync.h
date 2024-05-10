/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description:
 * Author: w00561424
 * Date: 2021-09-15 10:52:03
 */
#ifndef FIT_SUBSCRIPTION_NODE_SYNC_H
#define FIT_SUBSCRIPTION_NODE_SYNC_H
#include <fit/internal/registry/fit_registry_entities.h>
class FitSubscriptionNodeSync {
public:
    virtual ~FitSubscriptionNodeSync() = default;
    virtual bool Start() = 0;
    virtual bool Stop() = 0;
    virtual int32_t Add(const fit_fitable_key_t &key, const listener_t &listener) = 0;
    virtual int32_t Remove(const fit_fitable_key_t &key, const listener_t &listener) = 0;
};
using FitSubscriptionNodeSyncPtr = std::shared_ptr<FitSubscriptionNodeSync>;
class FitSubscriptionNodeSyncPtrFactory final {
public:
    static FitSubscriptionNodeSyncPtr Create();
};
#endif