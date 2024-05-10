/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fit registry context ut.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <registry_server/v3/fit_registry_base/include/fit_registry_context.h>
#include <mock/fit_base_strategy_mock.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
using namespace ::testing;
using namespace Fit;
using namespace Fit::Registry;

class FitRegistryContextTest : public ::testing::Test {
public:
    void SetUp() override
    {
        registryStrategy_ = Fit::make_shared<FitBaseStrategyMock>();
    }

    void TearDown() override
    {
    }
public:
    Fit::shared_ptr<FitBaseStrategyMock> registryStrategy_ {nullptr};
};

TEST_F(FitRegistryContextTest, should_error_when_do_check_given_strategy_null)
{
    // given
    FitRegistryContext context {};
    Fit::map<Fit::string, Fit::string> kvs {
        {"applcation_verison", "application_name"}
    };
    int32_t expectedRet = FIT_ERR_FAIL;

    // when
    context.SetStrategy(nullptr);
    auto ret = context.DoCheck(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitRegistryContextTest, should_ok_when_do_check_given_mock_fit_ok)
{
    // given
    FitRegistryContext context {};
    Fit::map<Fit::string, Fit::string> kvs {
        {"applcation_verison", "application_name"}
    };
    int32_t expectedRet = FIT_OK;
    EXPECT_CALL(*registryStrategy_, Check(::testing::_))
        .Times(::testing::AtLeast(1))
        .WillOnce(::testing ::Return(FIT_OK));

    // when
    context.SetStrategy(registryStrategy_);
    auto ret = context.DoCheck(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}