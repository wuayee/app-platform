/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 * Description  : Plugin
 * Author       : s00558940
 * Create       : 2020/10/13 11:05
 */

#include "fit_subscription_repository_test.h"

fit_subscription_repository_ptr fit_subscription_repository_factory::Create()
{
    return std::make_shared<fit_subscription_repository_test>();
}