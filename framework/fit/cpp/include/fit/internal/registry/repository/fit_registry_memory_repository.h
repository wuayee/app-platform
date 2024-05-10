/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Create       : 2020-09-14
 * Notes:       :
 */
#ifndef FIT_REGISTRY_MEMORY_REPOSITORY_H
#define FIT_REGISTRY_MEMORY_REPOSITORY_H
#include <fit/internal/registry/repository/fit_fitable_memory_repository.h>
#include "fit_registry_repository.h"
#include "fitable_registry_fitable_node_sync.h"
class FitRegistryMemoryRepository : public FitRegistryServiceRepository {
public:
    virtual ~FitRegistryMemoryRepository()= default;
    // �������ڵ�ͬ������,ֻ�����ڴ�
    virtual int32_t SyncSave(const db_service_info_t &service) = 0;
    virtual int32_t SyncRemove(const db_service_info_t &service) = 0;
    virtual int32_t SyncSave(const db_service_set &services) = 0;
    virtual int32_t SyncRemove(const db_service_set &services) = 0;
    virtual db_service_set QueryService(const fit_fitable_key_t &key, const Fit::fit_address &address) = 0;
    virtual bool InitTimeoutCallback(std::function<void(const db_service_set &)> callback) = 0;
};
using FitRegistryMemoryRepositoryPtr = std::shared_ptr<FitRegistryMemoryRepository>;
class FitRegistryMemoryRepositoryFactory final {
public:
    static FitRegistryMemoryRepositoryPtr Create(
        FitRegistryServiceRepositoryPtr serviceRepository,
        FitableRegistryFitableNodeSyncPtr fitableNodeSyncPtr,
        FitRegistryServiceRepositoryPtr syncServiceRepository,
        FitFitableMemoryRepositoryPtr serviceMemoryRepository);
};
#endif
