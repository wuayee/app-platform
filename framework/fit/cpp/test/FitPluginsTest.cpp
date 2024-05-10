/**
 * @file D:\ubuntu\FIT\c++\code\test\fit_main_test.cpp
 * Copyright (c) Huawei Technologies Co., Ltd. 2019-2020. All rights reserved.
 * Description:
 * Author: Zhongbin Yu 00286766
 * Date: 2020-04-08 16:32:07
 *
 * @brief
 */

#include "gtest/gtest.h"
#include "Environment/FitPluginsTestEnvironment.h"
using namespace testing;

int main(int argc, char* argv[])
{
    ::testing::AddGlobalTestEnvironment(new FitPluginsTestEnvironment);
    ::testing::InitGoogleTest(&argc, argv);

    return RUN_ALL_TESTS();
}