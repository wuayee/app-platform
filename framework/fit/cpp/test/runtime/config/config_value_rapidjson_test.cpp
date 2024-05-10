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

class ConfigValueRapidJsonTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ConfigValueRapidJsonTest, should_return_right_value_when_get_value_given_rapidjson_object)
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
    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    EXPECT_THAT(configValue.GetType(), ::testing::Eq(Fit::Config::VALUE_TYPE_OBJECT));
    EXPECT_THAT(configValue["hello"].AsString(), ::testing::StrEq("world"));
    EXPECT_THAT(configValue["hello_not_exist"].AsString("not_exist"), ::testing::StrEq("not_exist"));
    EXPECT_THAT(configValue["t"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(configValue["f"].AsBool(), ::testing::Eq(false));
    EXPECT_THAT(configValue["f_not_exist"].AsBool(false), ::testing::Eq(false));
    EXPECT_THAT(configValue["n"].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(configValue["i"].AsInt(), ::testing::Eq(123));
    EXPECT_THAT(configValue["i_not_exist"].AsInt(123444), ::testing::Eq(123444));
    EXPECT_THAT(configValue["pi"].AsDouble(), ::testing::Eq(3.1416));
    EXPECT_THAT(configValue["pi_not_exist"].AsDouble(2.13456), ::testing::Eq(2.13456));
    EXPECT_THAT(configValue["a"].IsArray(), ::testing::Eq(true));
    EXPECT_THAT(configValue["a"][0].AsInt(), ::testing::Eq(1));
    EXPECT_THAT(configValue["a"][1].AsInt(), ::testing::Eq(2));
    EXPECT_THAT(configValue["object"]["name"].AsString(), ::testing::StrEq("tom"));
}

TEST_F(ConfigValueRapidJsonTest, should_return_correct_value_when_get_value_given_json_string_and_update_value)
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
    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    configValue.FindMember("hello").SetValue("h");
    configValue.FindMember("t").SetValue(false);
    configValue.FindMember("f").SetValue(true);
    configValue.FindMember("n").SetValue(1);
    configValue.FindMember("i").SetValue(456);
    configValue.FindMember("pi").SetValue(666.3);
    configValue.FindMember("object").FindMember("name").SetValue("jake");

    EXPECT_THAT(configValue["hello"].AsString(), ::testing::StrEq("h"));
    EXPECT_THAT(configValue["t"].AsBool(), ::testing::Eq(false));
    EXPECT_THAT(configValue["f"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(configValue["n"].AsInt(), ::testing::Eq(1));
    EXPECT_THAT(configValue["i"].AsInt(), ::testing::Eq(456));
    EXPECT_THAT(configValue["pi"].AsDouble(), ::testing::Eq(666.3));
    EXPECT_THAT(configValue["object"]["name"].AsString(), ::testing::StrEq("jake"));
}

TEST_F(ConfigValueRapidJsonTest,
    should_return_correct_value_when_get_value_given_json_string_and_add_new_value_in_exist_object)
{
    const char *json = R"(
        {
            "object": {"name" : "tom"}
        }
        )";
    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    configValue.FindMember("object").AddMember("age").SetValue(12);

    EXPECT_THAT(configValue["object"]["name"].AsString(), ::testing::StrEq("tom"));
    EXPECT_THAT(configValue["object"]["age"].AsInt(), ::testing::Eq(12));
}

TEST_F(ConfigValueRapidJsonTest, should_return_correct_value_when_get_value_with_mutil_key_given_object)
{
    const char *json = R"(
        {
            "object": {"name" : "tom"}
        }
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson configValue(&document, &document);

    EXPECT_THAT(configValue["object.name"].AsString(), ::testing::StrEq("tom"));
}

TEST_F(ConfigValueRapidJsonTest, should_return_correct_value_when_get_value_given_json_string_and_add_new_value)
{
    const char *json = R"(
        {}
        )";
    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    configValue.AddMember("i").SetValue(1234);
    configValue.AddMember("b_t").SetValue(true);
    configValue.AddMember("b_false").SetValue(false);
    configValue.AddMember("n");
    configValue.AddMember("d").SetValue(22.33);
    configValue.AddMember("s").SetValue("string");
    configValue.AddMember("object").SetObject().AddMember("name").SetValue("nameValue");

    EXPECT_THAT(configValue["i"].AsInt(), ::testing::Eq(1234));
    EXPECT_THAT(configValue["b_t"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(configValue["b_false"].AsBool(), ::testing::Eq(false));
    EXPECT_THAT(configValue["n"].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(configValue["d"].AsDouble(), ::testing::Eq(22.33));
    EXPECT_THAT(configValue["s"].AsString(), ::testing::StrEq("string"));
    EXPECT_THAT(configValue["object"]["name"].AsString(), ::testing::StrEq("nameValue"));
}

TEST_F(ConfigValueRapidJsonTest, should_return_same_value_when_find_member_given_json_string_and_default_items)
{
    const char *json = R"(
        {
            "i":1,
            "b_t":true,
            "b_f":false,
            "n":null,
            "d":66.33,
            "s":"ssss",
            "object":{
                "name" : "nn"
            }
        }
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    EXPECT_THAT(configValue.FindMember("i").AsInt(), ::testing::Eq(configValue["i"].AsInt()));
    EXPECT_THAT(configValue.FindMember("b_t").AsBool(), ::testing::Eq(configValue["b_t"].AsBool()));
    EXPECT_THAT(configValue.FindMember("b_f").AsBool(), ::testing::Eq(configValue["b_f"].AsBool()));
    EXPECT_THAT(configValue.FindMember("n").IsNull(), ::testing::Eq(configValue["n"].IsNull()));
    EXPECT_THAT(configValue.FindMember("d").AsDouble(), ::testing::Eq(configValue["d"].AsDouble()));
    EXPECT_THAT(configValue.FindMember("s").AsString(), ::testing::StrEq(configValue["s"].AsString()));
    EXPECT_THAT(configValue.FindMember("object").FindMember("name").AsString(),
        ::testing::StrEq(configValue["object"]["name"].AsString()));
}

TEST_F(ConfigValueRapidJsonTest, should_is_null_value_when_set_null_given_valid_value)
{
    const char *json = R"(
        {
            "hello": "world"
        }
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson configValue(&document, &document);

    EXPECT_THAT(configValue.SetNull().IsNull(), ::testing::Eq(true));
}

TEST_F(ConfigValueRapidJsonTest, should_return_array_value_when_set_array_given_array_value_setted)
{
    const char *json = R"(
        {}
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson configValue(&document, &document);
    configValue.SetArray();
    configValue.PushBack().SetObject().AddMember("name").SetValue("nameValue");
    configValue.PushBack().SetValue(true);
    configValue.PushBack().SetValue(123);
    configValue.PushBack().SetValue(123.123);
    configValue.PushBack().SetValue("ss");
    configValue.PushBack().SetArray();
    configValue.PushBack();

    ASSERT_THAT(configValue.IsArray(), ::testing::Eq(true));
    EXPECT_THAT(configValue[0]["name"].AsString(), ::testing::StrEq("nameValue"));
    EXPECT_THAT(configValue[1].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(configValue[2].AsInt(), ::testing::Eq(123));
    EXPECT_THAT(configValue[3].AsDouble(), ::testing::Eq(123.123));
    EXPECT_THAT(configValue[3].AsDouble(1.0), ::testing::Eq(123.123));
    EXPECT_THAT(configValue[4].AsString(), ::testing::StrEq("ss"));
    EXPECT_THAT(configValue[5].IsArray(), ::testing::Eq(true));
    EXPECT_THAT(configValue[6].IsNull(), ::testing::Eq(true));
}

TEST_F(ConfigValueRapidJsonTest, should_return_default_value_when_as_value_given_wrong_type)
{
    const char *json = R"(
        {
            "hello": "world",
            "t": true
        }
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    EXPECT_THAT(configValue["hello"].AsBool(true), ::testing::Eq(true));
    EXPECT_THAT(configValue["hello"].AsInt(1), ::testing::Eq(1));
    EXPECT_THAT(configValue["hello"].AsDouble(666.3), ::testing::Eq(666.3));
    EXPECT_THAT(configValue["t"].AsString("default"), ::testing::StrEq("default"));
}

TEST_F(ConfigValueRapidJsonTest, should_return_default_value_when_as_value_given_not_exist_key)
{
    const char *json = R"(
        {
            "hello": "world"
        }
        )";

    rapidjson::Document document;
    document.Parse(json);
    ASSERT_THAT(document.HasParseError(), ::testing::Eq(false)) << document.GetParseError();

    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);
    auto &configValue = valueRapidJson;

    EXPECT_THAT(configValue["helloXXXX"].AsBool(true), ::testing::Eq(true));
    EXPECT_THAT(configValue["helloXXXX"].AsInt(1), ::testing::Eq(1));
    EXPECT_THAT(configValue["helloXXXX"].AsDouble(666.3), ::testing::Eq(666.3));
    EXPECT_THAT(configValue["helloXXXX"].AsString("default"), ::testing::StrEq("default"));
}

TEST_F(ConfigValueRapidJsonTest, should_throw_exception_when_access_given_attach_null_value)
{
    Fit::Config::ValueRapidJson valueRapidJson;

    EXPECT_THAT(valueRapidJson.GetType(), ::testing::Eq(VALUE_TYPE_NULL));
    EXPECT_THROW(valueRapidJson[0], std::runtime_error);
    EXPECT_THROW(valueRapidJson.Size(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsBool(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsInt(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsDouble(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsString(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsString(), std::runtime_error);
    EXPECT_THROW(valueRapidJson["key"], std::runtime_error);
    EXPECT_THROW(valueRapidJson.AddMember("1"), std::runtime_error);
    EXPECT_THROW(valueRapidJson.FindMember("1"), std::runtime_error);
    EXPECT_THROW(valueRapidJson.SetValue(true), std::runtime_error);
    EXPECT_THROW(valueRapidJson.SetValue(123), std::runtime_error);
    EXPECT_THROW(valueRapidJson.SetValue(123.00), std::runtime_error);
    EXPECT_THROW(valueRapidJson.SetValue("value"), std::runtime_error);
    EXPECT_THROW(valueRapidJson.SetObject(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.GetKeys(), std::runtime_error);
    EXPECT_THAT(valueRapidJson.SetNull().IsNull(), ::testing::Eq(true));
    EXPECT_THROW(valueRapidJson.SetArray(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.Reserve(1), std::runtime_error);
    EXPECT_THROW(valueRapidJson.PushBack(), std::runtime_error);
}

TEST_F(ConfigValueRapidJsonTest, should_throw_exception_when_access_given_not_matched_type)
{
    rapidjson::Document document;
    document.Null();
    Fit::Config::ValueRapidJson valueRapidJson(&document, &document);

    EXPECT_THAT(valueRapidJson.GetType(), ::testing::Eq(VALUE_TYPE_NULL));
    EXPECT_THROW(valueRapidJson[0], std::runtime_error);
    EXPECT_THROW(valueRapidJson.Size(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsBool(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsInt(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsDouble(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsString(), std::runtime_error);
    EXPECT_THROW(valueRapidJson.AsString(), std::runtime_error);
    EXPECT_THROW(valueRapidJson["key"], std::runtime_error);
    EXPECT_THROW(valueRapidJson.AddMember("1"), std::runtime_error);
    EXPECT_THROW(valueRapidJson.FindMember("1"), std::runtime_error);
    EXPECT_THAT(valueRapidJson.SetValue(true).IsBool(), ::testing::Eq(true));
    EXPECT_THAT(valueRapidJson.SetValue(123).IsInt(), ::testing::Eq(true));
    EXPECT_THAT(valueRapidJson.SetValue(123.00).IsDouble(), ::testing::Eq(true));
    EXPECT_THAT(valueRapidJson.SetValue("value").IsString(), ::testing::Eq(true));
    EXPECT_THAT(valueRapidJson.SetObject().IsObject(), ::testing::Eq(true));
    EXPECT_THAT(valueRapidJson.SetArray().IsArray(), ::testing::Eq(true));
    EXPECT_THROW(valueRapidJson.GetKeys(), std::runtime_error);
    EXPECT_THAT(valueRapidJson.SetNull().IsNull(), ::testing::Eq(true));
    EXPECT_THROW(valueRapidJson.Reserve(1), std::runtime_error);
    EXPECT_THROW(valueRapidJson.PushBack(), std::runtime_error);
}
