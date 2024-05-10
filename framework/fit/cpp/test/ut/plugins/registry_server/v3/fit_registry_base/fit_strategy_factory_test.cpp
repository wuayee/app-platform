/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fit registry strategy factory.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <registry_server/v3/fit_registry_base/include/fit_strategy_factory.h>
#include <fit/stl/memory.hpp>
#include <fit/stl/string.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>

using namespace testing;
using namespace Fit::Registry;
class FitStrategyFactoryTest : public testing::Test {
public:
    void SetUp() override
    {
    }
    void TearDown() override
    {
    }
};

TEST_F(FitStrategyFactoryTest, should_return_null_when_call_get_strategy_given_error_type)
{
    // given
    Fit::string type = "error_type";
    Fit::string expectedType = "invalid_type";

    // when
    auto strategy = FitStrategyFactory::GetStrategy(type);

    // then
    EXPECT_EQ(strategy, nullptr);
}

TEST_F(FitStrategyFactoryTest, should_return_application_strategy_when_call_get_strategy_given_application_type)
{
    // given
    Fit::string type = "application";
    Fit::string expectedType = "application";

    // when
    auto strategy = FitStrategyFactory::GetStrategy(type);

    // then
    EXPECT_EQ(strategy->Type(), expectedType);
}

TEST_F(FitStrategyFactoryTest, should_return_app_instance_strategy_when_call_get_strategy_given_app_instance_type)
{
    // given
    Fit::string type = "application_instance";
    Fit::string expectedType = "application_instance";

    // when
    auto strategy = FitStrategyFactory::GetStrategy(type);

    // then
    EXPECT_EQ(strategy->Type(), expectedType);
}