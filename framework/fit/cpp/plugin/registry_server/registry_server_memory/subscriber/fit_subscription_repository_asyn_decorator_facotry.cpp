/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : w00561424
 * Date         : 2020-09-15
 * Notes:       :
 */
#include "fit_subscription_repository_asyn_decorator.h"
FitSubscriptionRepositoryDecoratorPtr FitSubscriptionRepositoryDecoratorFactory::Create(
    fit_subscription_repository_ptr subscriptionRepository)
{
    return std::make_shared<Fit::Registry::FitSubscriptionRepositoryAsynDecorator>(subscriptionRepository);
}