/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : provide fit strategy fitable meta test.
 * Author       : w00561424
 * Date:        : 2023/10/18
 */
#include <registry_server/v3/fit_registry_base/include/fit_strategy_application.h>
#include <fit/stl/memory.hpp>
#include <fit/fit_code.h>
#include <mock/fit_fitable_meta_service_mock.hpp>
#include <gtest/gtest.h>
#include <gmock/gmock.h>
using namespace ::testing;
using namespace Fit::Registry;
class FitStrategyFitableApplicationTest : public ::testing::Test {
public:
    void SetUp() override
    {
        fitableMetaService_ = Fit::make_shared<FitFitableMetaServiceMock>();
        strategy_ = Fit::make_shared<FitStrategyApplication>(fitableMetaService_);
    }

    void TearDown() override
    {
    }
public:
    FitBaseStrategyPtr strategy_ {nullptr};
    Fit::shared_ptr<FitFitableMetaServiceMock> fitableMetaService_ {nullptr};
};

TEST_F(FitStrategyFitableApplicationTest, should_return_application_when_call_type_given_empty)
{
    // given
    Fit::string expectedType = "application";

    // when
    Fit::string type = strategy_->Type();

    // then
    EXPECT_EQ(type, expectedType);
}

TEST_F(FitStrategyFitableApplicationTest, should_return_error_when_call_check_given_null_service)
{
    // given
    int32_t expectRet = FIT_ERR_FAIL;
    Fit::map<Fit::string, Fit::string> kvs {};
    auto strategy = Fit::make_shared<FitStrategyApplication>(nullptr);

    // when
    int32_t ret = strategy->Check(kvs);

    // then
    EXPECT_EQ(ret, expectRet);
}

TEST_F(FitStrategyFitableApplicationTest, should_return_ok_when_call_check_given_mock_exist)
{
    // given
    int32_t expectRet = FIT_OK;
    Fit::map<Fit::string, Fit::string> kvs {
        {"test_version", "test_app"}
    };

    EXPECT_CALL(*fitableMetaService_, IsApplicationExist(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(true));
    // when
    int32_t ret = strategy_->Check(kvs);

    // then
    EXPECT_EQ(ret, expectRet);
}

TEST_F(FitStrategyFitableApplicationTest, should_return_not_exist_when_call_check_given_mock_not_exist)
{
    // given
    int32_t expectRet = FIT_ERR_NOT_EXIST;
    Fit::map<Fit::string, Fit::string> kvs {
        {"test_version", "test_app"}
    };

    EXPECT_CALL(*fitableMetaService_, IsApplicationExist(testing::_))
        .Times(testing::AtLeast(1))
        .WillOnce(testing::Return(false));
    // when
    int32_t ret = strategy_->Check(kvs);

    // then
    EXPECT_EQ(ret, expectRet);
}