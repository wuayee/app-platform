/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2021/09/08
 * Notes:       :
 */
#ifndef FITABLE_REGISTRY_FITABLE_NODE_SYNC_H
#define FITABLE_REGISTRY_FITABLE_NODE_SYNC_H
#include <fit/internal/registry/fit_registry_entities.h>
#include <memory>
class FitableRegistryFitableNodeSync {
public:
    virtual ~FitableRegistryFitableNodeSync() = default;
    virtual bool Start() = 0;
    virtual bool Stop() = 0;
    virtual int32_t Add(const db_service_set& serviceSet) = 0;
    virtual int32_t Remove(const db_service_set& serviceSet) = 0;
};

using FitableRegistryFitableNodeSyncPtr = std::shared_ptr<FitableRegistryFitableNodeSync>;
class FitableRegistryFitableNodeSyncPtrFacotry final {
public:
    static FitableRegistryFitableNodeSyncPtr Create();
};
#endif