/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description:
 * Author: y00576281
 * Date: 2021-06-01 17:28:32
 */

#include "fit/external/plugin/plugin_config.hpp"

#include "gtest/gtest.h"

using Fit::Plugin::PluginConfigPtr;

const static Fit::string TEST_FILE = "plugin_config_test.json";
const static Fit::string INVALID_TEST_FILE = "plugin_config_test_invalid.json";

class PluginConfigTest : public ::testing::Test {
public:
    PluginConfigPtr pluginConfig{};
    void SetUp() override {}
    void TearDown() override {}
};

TEST_F(PluginConfigTest, should_succeed_when_create_config_given_valid_config_file)
{
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);
    EXPECT_NE(config, nullptr);
}

TEST_F(PluginConfigTest, should_succeed_when_get_value_by_key_given_valid_config_file)
{
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    EXPECT_EQ(config->Get("testKey").AsString(""), "testVal");
}

TEST_F(PluginConfigTest, should_return_null_when_create_config_given_invalid_config_file)
{
    auto config = Fit::Plugin::CreatePluginConfig("invalid_path");
    EXPECT_EQ(config, nullptr);
}

TEST_F(PluginConfigTest, should_return_empty_when_get_value_by_key_given_invalid_key)
{
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    EXPECT_TRUE(Fit::string(config->Get("invalidKey").AsString("")).empty());
}

TEST_F(PluginConfigTest, should_return_null_when_create_plugin_config_given_invalid_format_config_file)
{
    auto config = Fit::Plugin::CreatePluginConfig(INVALID_TEST_FILE);
    EXPECT_EQ(config, nullptr);
}

TEST_F(PluginConfigTest, should_return_int_when_get_value_by_key_given_int_key)
{
    // given
    Fit::string key = "testIntKey";
    int expectedValue = 6666;

    // when
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    // then
    EXPECT_EQ(config->Get(key).AsInt(), expectedValue);
}

TEST_F(PluginConfigTest, should_return_int_when_get_value_by_key_given_int_key_with_error_value)
{
    // given
    Fit::string key = "testIntKey";
    int expectedValue = 7777;

    // when
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    // then
    EXPECT_NE(config->Get(key).AsInt(), expectedValue);
}

TEST_F(PluginConfigTest, should_return_false_when_get_value_by_key_given_bool_key)
{
    // given
    Fit::string key = "testBoolKey";
    int expectedValue = false;

    // when
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    // then
    EXPECT_EQ(config->Get(key).AsBool(true), expectedValue);
}

TEST_F(PluginConfigTest, should_return_false_when_get_value_by_key_given_bool_key_with_error_value)
{
    // given
    Fit::string key = "testBoolKey";
    int expectedValue = true;

    // when
    auto config = Fit::Plugin::CreatePluginConfig(TEST_FILE);

    // then
    EXPECT_NE(config->Get(key).AsBool(false), expectedValue);
}