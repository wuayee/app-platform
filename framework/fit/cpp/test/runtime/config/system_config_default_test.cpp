/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#include <fit/internal/runtime/config/system_config_default.hpp>
#include <fit/internal/runtime/config/config_value_rapidjson.hpp>

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Config;

class SystemConfigDefaultTest : public testing::Test {
public:
    void SetUp() override
    {
        systemConfig_.LoadFromString("{}");
    }

    void TearDown() override {}

    SystemConfigDefault systemConfig_;
};

TEST_F(SystemConfigDefaultTest, should_return_correct_value_when_get_value_given_json_string)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true,
            "f": false,
            "n": null,
            "i": 123,
            "pi": 3.1416,
            "object": {"name" : "tom"},
            "a": [1, 2, 3, 4]
        }
        )";

    systemConfig_.LoadFromString(json);

    EXPECT_THAT(systemConfig_.GetValue("hello").AsString(), ::testing::StrEq("world"));
    EXPECT_THAT(systemConfig_.GetValue("t").AsBool(), ::testing::Eq(true)) << "true";
    EXPECT_THAT(systemConfig_.GetValue("t1").AsBool(true), ::testing::Eq(true)) << "true";
    EXPECT_THAT(systemConfig_.GetValue("f").AsBool(), ::testing::Eq(false));
    EXPECT_THAT(systemConfig_.GetValue("n").IsNull(), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("i").AsInt(), ::testing::Eq(123));
    EXPECT_THAT(systemConfig_.GetValue("pi").AsDouble(), ::testing::Eq(3.1416));
    EXPECT_THAT(systemConfig_.GetValue("a").IsArray(), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("a")[0].AsInt(), ::testing::Eq(1));
    EXPECT_THAT(systemConfig_.GetValue("a")[1].AsInt(), ::testing::Eq(2));
    EXPECT_THAT(systemConfig_.GetValue("object.name").AsString(), ::testing::StrEq("tom"));
}

TEST_F(SystemConfigDefaultTest, should_return_correct_value_when_get_value_given_json_string_and_put_new_items)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true,
            "f": false,
            "n": null,
            "i": 123,
            "pi": 3.1416,
            "object": {"name" : "tom"},
            "a": [1, 2, 3, 4]
        }
        )";

    systemConfig_.LoadFromString(json);
    systemConfig_.PutItems({
        {"hello",          R"("h")"},
        {"t",              "false"},
        {"f",              "true"},
        {"n",              "1"},
        {"i",              "456"},
        {"pi",             "666.3"},
        {"object.name",    R"("jake")"},
        {"object.age",     "12"},
        {"newStr",         R"("newStrValue")"},
        {"newObject.name", R"("newName")"}
    });

    EXPECT_THAT(systemConfig_.GetValue("hello").AsString(), ::testing::StrEq("h"));
    EXPECT_THAT(systemConfig_.GetValue("t").AsBool(), ::testing::Eq(false));
    EXPECT_THAT(systemConfig_.GetValue("f").AsBool(), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("n").AsInt(), ::testing::Eq(1));
    EXPECT_THAT(systemConfig_.GetValue("i").AsInt(), ::testing::Eq(456));
    EXPECT_THAT(systemConfig_.GetValue("pi").AsDouble(), ::testing::Eq(666.3));
    EXPECT_THAT(systemConfig_.GetValue("object.name").AsString(), ::testing::StrEq("jake"));
    EXPECT_THAT(systemConfig_.GetValue("object.age").AsInt(), ::testing::Eq(12));
    EXPECT_THAT(systemConfig_.GetValue("newStr").AsString(), ::testing::StrEq("newStrValue"));
    EXPECT_THAT(systemConfig_.GetValue("newObject.name").AsString(), ::testing::StrEq("newName"));
}

TEST_F(SystemConfigDefaultTest, should_return_null_value_when_get_value_given_nonexist_key)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true
        }
        )";

    systemConfig_.LoadFromString(json);

    EXPECT_THAT(systemConfig_.GetValue("hello1").IsNull(), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("hello").IsNull(), ::testing::Eq(false));
}

TEST_F(SystemConfigDefaultTest, should_return_default_value_when_as_value_given_wrong_type)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true
        }
        )";

    systemConfig_.LoadFromString(json);

    EXPECT_THAT(systemConfig_.GetValue("hello").AsBool(true), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("hello").AsInt(1), ::testing::Eq(1));
    EXPECT_THAT(systemConfig_.GetValue("hello").AsDouble(666.3), ::testing::Eq(666.3));
    EXPECT_THAT(systemConfig_.GetValue("t").AsString("default"), ::testing::StrEq("default"));
}

TEST_F(SystemConfigDefaultTest, should_return_default_value_when_as_value_given_not_exist_key)
{
    const char *json = "{\n"
                       "    \"hello\": \"world\"\n"
                       "}";

    systemConfig_.LoadFromString(json);

    EXPECT_THAT(systemConfig_.GetValue("helloXXXX").AsBool(true), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("helloXXXX").AsInt(1), ::testing::Eq(1));
    EXPECT_THAT(systemConfig_.GetValue("helloXXXX").AsDouble(666.3), ::testing::Eq(666.3));
    EXPECT_THAT(systemConfig_.GetValue("helloXXXX").AsString("default"), ::testing::StrEq("default"));
}

TEST_F(SystemConfigDefaultTest, should_throw_exception_when_as_value_given_wrong_type)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true
        }
        )";

    systemConfig_.LoadFromString(json);

    EXPECT_THROW(systemConfig_.GetValue("hello").AsBool(), std::runtime_error);
    EXPECT_THROW(systemConfig_.GetValue("hello").AsInt(), std::runtime_error);
    EXPECT_THROW(systemConfig_.GetValue("hello").AsDouble(), std::runtime_error);
    EXPECT_THROW(systemConfig_.GetValue("t").AsString(), std::runtime_error);
}

TEST_F(SystemConfigDefaultTest, should_return_bool_value_when_get_value_given_put_items_with_string_true_or_false)
{
    systemConfig_.PutItems({
        {"t",          "true"},
        {"f",          "false"}
    });

    EXPECT_THAT(systemConfig_.GetValue("t").AsBool(), ::testing::Eq(true));
    EXPECT_THAT(systemConfig_.GetValue("f").AsBool(), ::testing::Eq(false));
}

TEST_F(SystemConfigDefaultTest,
    should_return_int_value_when_get_value_given_put_items_with_string_only_has_number)
{
    systemConfig_.PutItems({
        {"i",          "456"},
        {"-i",          "-456"}
    });

    EXPECT_THAT(systemConfig_.GetValue("i").AsInt(), ::testing::Eq(456));
    EXPECT_THAT(systemConfig_.GetValue("-i").AsInt(), ::testing::Eq(-456));
}

TEST_F(SystemConfigDefaultTest,
    should_return_double_value_when_get_value_given_put_items_with_string_double_number)
{
    systemConfig_.PutItems({
        {"d",          "456.456"},
        {"-d",          "-456.456"}
    });

    EXPECT_THAT(systemConfig_.GetValue("d").AsDouble(), ::testing::Eq(456.456));
    EXPECT_THAT(systemConfig_.GetValue("-d").AsDouble(), ::testing::Eq(-456.456));
}

TEST_F(SystemConfigDefaultTest, should_return_string_value_when_get_value_given_put_items_with_string_no_quated)
{
    systemConfig_.PutItems({
        {"hello",          "h"}
    });

    EXPECT_THAT(systemConfig_.GetValue("hello").AsString(), ::testing::StrEq("h"));
}

TEST_F(SystemConfigDefaultTest, should_return_string_value_when_get_value_given_put_items_with_string_quated)
{
    systemConfig_.PutItems({
        {"hello",          "\"h\""}
    });

    EXPECT_THAT(systemConfig_.GetValue("hello").AsString(), ::testing::StrEq("h"));
}

TEST_F(SystemConfigDefaultTest, should_return_null_value_when_get_value_given_put_items_with_null_string)
{
    systemConfig_.PutItems({
        {"n",          "null"}
    });

    EXPECT_THAT(systemConfig_.GetValue("n").IsNull(), ::testing::Eq(true));
}