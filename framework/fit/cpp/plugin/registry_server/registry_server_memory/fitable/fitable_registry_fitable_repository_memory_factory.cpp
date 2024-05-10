/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/08
 * Notes:       :
 */
#include "fitable_registry_fitable_repository_memory.h"
FitRegistryMemoryRepositoryPtr FitRegistryMemoryRepositoryFactory::Create(
    FitRegistryServiceRepositoryPtr serviceRepository,
    FitableRegistryFitableNodeSyncPtr fitableNodeSyncPtr,
    FitRegistryServiceRepositoryPtr syncServiceRepository,
    FitFitableMemoryRepositoryPtr serviceMemoryRepository)
{
    return std::make_shared<Fit::Registry::FitRegistryFitableRepositoryMemory>(
        std::move(serviceRepository), std::move(fitableNodeSyncPtr),
        std::move(syncServiceRepository), std::move(serviceMemoryRepository));
}