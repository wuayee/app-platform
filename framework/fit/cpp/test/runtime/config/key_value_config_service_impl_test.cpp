/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 *
 * Description  : Test
 * Author       : 王攀博 00561424
 * Date         : 2024/03/01
 */

#include <runtime/config/key_value_config_service_impl.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Configuration;
class KeyValueConfigServiceImplTest : public testing::Test {
public:
    void SetUp() override
    {
        key_ = "test_key";
        value_ = "test_value";
    }
    void TearDown() override {}

    Fit::string key_;
    Fit::string value_;
};

TEST_F(KeyValueConfigServiceImplTest, should_return_value_when_set_and_get_given_key_value)
{
    // given
    KeyValueConfigServiceImpl keyValueConfig(nullptr);
    Fit::string expectedValue = value_;
    int32_t expectedRet = FIT_OK;

    // when
    keyValueConfig.Set(key_, value_);
    Fit::string valueResult;
    int32_t ret = keyValueConfig.Get(key_, valueResult);

    // then
    EXPECT_EQ(valueResult, expectedValue);
    EXPECT_EQ(ret, expectedRet);
}

TEST_F(KeyValueConfigServiceImplTest, should_return_value_when_subscribe_and_notify_given_key_value)
{
    // given
    Fit::string valueOut;
    std::function<void(Fit::string, Fit::string)> func = [&valueOut](Fit::string key, Fit::string value) {
        valueOut = value;
    };

    KeyValueConfigServiceImpl keyValueConfig(nullptr);
    // when
    Fit::string valueResult;
    keyValueConfig.Subscribe(key_, func);
    keyValueConfig.Set(key_, value_);
    // then
    EXPECT_EQ(valueOut, value_);
}