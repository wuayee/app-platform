
/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : s00558940
 * Date       : 2020/10/9 11:31
 * Notes:       :
 */
#include <memory>
#include "fit_registry_repository_persistence_v2.h"

FitRegistryServiceRepositoryPtr FitRegistryServiceRepositoryFactoryV2::Create()
{
    return std::make_shared<Fit::FitRegistryRepositoryPersistenceV2>(
        FitWorkerTableOperationFactory::Create(),
        FitAddressTableOperationFactory::Create(),
        FitFitableTableOperationFactory::Create());
}