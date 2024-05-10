/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2021/09/11
 * Notes:       :
 */
#include "fit_registry_fitable_repository_asyn_decorator.h"
FitRegistryRepositoryDecoratorPtr FitRegistryRepositoryFactoryWithServiceRepository::Create(
    FitRegistryServiceRepositoryPtr serviceRepository)
{
    return std::make_shared<Fit::Registry::FitRegistryFitableRepositoryAsynDecorator>(serviceRepository);
}