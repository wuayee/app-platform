/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fit application instance test.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <registry_server/v3/fit_registry_base/include/fit_strategy_application_instance.h>
#include <mock/fit_application_instance_service_mock.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <fit/fit_code.h>
#include <fit/stl/memory.hpp>

using namespace Fit;
using namespace Fit::Registry;
class FitStrategyApplicationInstanceTest : public ::testing::Test {
public:
    void SetUp() override
    {
        applicationInstanceService_ = Fit::make_shared<FitApplicationInstanceServiceMock>();
        registryStrategy_ = Fit::make_shared<FitStrategyApplicationInstance>(applicationInstanceService_);
    }

    void TearDown() override
    {
    }
public:
    FitBaseStrategyPtr registryStrategy_ {nullptr};
    Fit::shared_ptr<FitApplicationInstanceServiceMock> applicationInstanceService_ {nullptr};
};

TEST_F(FitStrategyApplicationInstanceTest, should_return_application_intance_when_call_type_given_empty)
{
    // given
    Fit::string expectedType = "application_instance";

    // when
    Fit::string ret = registryStrategy_->Type();

    // then
    EXPECT_EQ(ret, expectedType);
}

TEST_F(FitStrategyApplicationInstanceTest, should_return_error_when_call_check_given_null_service)
{
    // given
    int32_t expectedRet = FIT_ERR_FAIL;
    auto registryStrategy = Fit::make_shared<FitStrategyApplicationInstance>(nullptr);
    Fit::map<Fit::string, Fit::string> kvs {};

    // when
    int32_t ret = registryStrategy->Check(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitStrategyApplicationInstanceTest, should_return_error_when_call_check_given_kvs_empty)
{
    // given
    int32_t expectedRet = FIT_ERR_FAIL;
    Fit::map<Fit::string, Fit::string> kvs {};

    // when
    int32_t ret = registryStrategy_->Check(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitStrategyApplicationInstanceTest, should_return_not_exist_when_call_check_given_mock_not_exist)
{
    // given
    int32_t expectedRet = FIT_ERR_NOT_EXIST;
    Fit::map<Fit::string, Fit::string> kvs {
        {"test_worker_version", "test_worker_id"}
    };
    EXPECT_CALL(*applicationInstanceService_, Check(testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(FIT_ERR_NOT_EXIST));

    // when
    int32_t ret = registryStrategy_->Check(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(FitStrategyApplicationInstanceTest, should_return_ok_when_call_check_given_mock_ok)
{
    // given
    int32_t expectedRet = FIT_OK;
    Fit::map<Fit::string, Fit::string> kvs {
        {"test_worker_version", "test_worker_id"}
    };
    EXPECT_CALL(*applicationInstanceService_, Check(testing::_, testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(FIT_OK));

    // when
    int32_t ret = registryStrategy_->Check(kvs);

    // then
    EXPECT_EQ(ret, expectedRet);
}