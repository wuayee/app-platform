/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#include <fit/internal/runtime/config/config_value_rapidjson.hpp>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

#include <rapidjson/istreamwrapper.h>
#include <rapidjson/writer.h>

using rapidjson::Document;
using rapidjson::IStreamWrapper;
using rapidjson::Writer;
using rapidjson::StringBuffer;

using namespace Fit::Config;

class ConfigValueTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ConfigValueTest, should_return_default_when_get_value_given_default_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.GetType(), ::testing::Eq(Fit::Config::VALUE_TYPE_NULL));

    EXPECT_THAT(configValue.AsString("1"), ::testing::StrEq("1"));
    EXPECT_THAT(configValue.AsBool(false), ::testing::Eq(false));
    EXPECT_THAT(configValue.AsInt(123444), ::testing::Eq(123444));
    EXPECT_THAT(configValue.AsDouble(2.13456), ::testing::Eq(2.13456));
}

TEST_F(ConfigValueTest, should_throw_exception_when_get_value_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.GetType(), ::testing::Eq(Fit::Config::VALUE_TYPE_NULL));

    EXPECT_THROW(configValue.AsString(), std::runtime_error);
    EXPECT_THROW(configValue.AsBool(), std::runtime_error);
    EXPECT_THROW(configValue["n"], std::runtime_error);
    EXPECT_THROW(configValue[1], std::runtime_error);
    EXPECT_THROW(configValue.AsInt(), std::runtime_error);
    EXPECT_THROW(configValue.AsDouble(), std::runtime_error);
    EXPECT_THROW(configValue.Size(), std::runtime_error);
}

TEST_F(ConfigValueTest, should_return_true_when_execute_is_null_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsNull(), ::testing::Eq(true));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_bool_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsBool(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_int_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsInt(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_double_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsDouble(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_string_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsString(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_object_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsObject(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_return_false_when_execute_is_array_given_with_null_value)
{
    Fit::Config::Value configValue;

    EXPECT_THAT(configValue.IsArray(), ::testing::Eq(false));
}

TEST_F(ConfigValueTest, should_throw_exception_when_get_keys_given_with_null_value)
{
    Fit::Config::Value configValue;
    EXPECT_THROW(configValue.GetKeys(), std::runtime_error);
}