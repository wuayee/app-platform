/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 * Description  : test for writable value
 * Author       : songyongtan
 * Create       : 2023-08-18
 * Notes:       :
 */

#include "runtime/config/config_writable_value.hpp"

#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace testing;
using namespace Fit;
using namespace Fit::Config;

TEST(FitConfigWritableValueTest, should_return_bool_value_when_get_given_bool_value)
{
    auto value = WritableValue::New();
    value->SetValue(true);
    ASSERT_FALSE(value->IsNull());
    ASSERT_TRUE(value->IsBool());
    ASSERT_EQ(value->GetType(), VALUE_TYPE_BOOL);
    EXPECT_TRUE(value->AsBool());
    value->SetValue(false);
    EXPECT_FALSE(value->AsBool());
}

TEST(FitConfigWritableValueTest, should_return_int_value_when_get_given_int_value)
{
    auto value = WritableValue::New();
    int32_t expectValue = 2;
    value->SetValue(expectValue);
    ASSERT_FALSE(value->IsNull());
    ASSERT_EQ(value->GetType(), VALUE_TYPE_INT);
    ASSERT_TRUE(value->IsInt());
    EXPECT_EQ(value->AsInt(), expectValue);
    expectValue = 3;
    value->SetValue(expectValue);
    EXPECT_EQ(value->AsInt(), expectValue);
}

TEST(FitConfigWritableValueTest, should_return_double_value_when_get_given_double_value)
{
    auto value = WritableValue::New();
    double expectValue = 2.14;
    value->SetValue(expectValue);
    ASSERT_FALSE(value->IsNull());
    ASSERT_EQ(value->GetType(), VALUE_TYPE_DOUBLE);
    ASSERT_TRUE(value->IsDouble());
    EXPECT_EQ(value->AsDouble(), expectValue);
    expectValue = 3.14;
    value->SetValue(expectValue);
    EXPECT_EQ(value->AsDouble(), expectValue);
}

TEST(FitConfigWritableValueTest, should_return_string_value_when_get_given_string_value)
{
    auto value = WritableValue::New();
    string expectValue = "hello";
    value->SetValue(expectValue);
    ASSERT_FALSE(value->IsNull());
    ASSERT_EQ(value->GetType(), VALUE_TYPE_STRING);
    ASSERT_TRUE(value->IsString());
    EXPECT_EQ(value->AsString(), expectValue);
    EXPECT_EQ(value->AsString(""), expectValue);
    EXPECT_EQ(value->AsString(string("s")), expectValue);
    expectValue = "hello1";
    value->SetValue(expectValue);
    EXPECT_EQ(value->AsString(), expectValue);
}

TEST(FitConfigWritableValueTest, should_return_object_value_when_get_given_object_value)
{
    auto value = WritableValue::New();
    auto& configValue = *value;
    configValue.SetObject();
    vector<string> expectedKeys = {"i", "b_t", "b_f", "n", "d", "s", "object"};
    configValue.AddMember(expectedKeys[0].c_str()).SetValue(1234);
    configValue.AddMember(expectedKeys[1].c_str()).SetValue(true);
    configValue.AddMember(expectedKeys[2].c_str()).SetValue(false);
    configValue.AddMember(expectedKeys[3].c_str());
    configValue.AddMember(expectedKeys[4].c_str()).SetValue(22.33);
    configValue.AddMember(expectedKeys[5].c_str()).SetValue("string");
    configValue.AddMember(expectedKeys[6].c_str()).SetObject().AddMember("name").SetValue("nameValue");

    ASSERT_THAT(configValue.IsObject(), Eq(true));
    ASSERT_THAT(configValue.GetType(), Eq(VALUE_TYPE_OBJECT));
    EXPECT_THAT(configValue[expectedKeys[0].c_str()].AsInt(), Eq(1234));
    EXPECT_THAT(configValue[expectedKeys[1].c_str()].AsBool(), Eq(true));
    EXPECT_THAT(configValue[expectedKeys[2].c_str()].AsBool(), Eq(false));
    EXPECT_THAT(configValue[expectedKeys[3].c_str()].IsNull(), Eq(true));
    EXPECT_THAT(configValue[expectedKeys[4].c_str()].AsDouble(), Eq(22.33));
    EXPECT_THAT(configValue[expectedKeys[5].c_str()].AsString(), StrEq("string"));
    EXPECT_THAT(configValue[expectedKeys[6].c_str()]["name"].AsString(), StrEq("nameValue"));

    EXPECT_THAT(configValue.FindMember(expectedKeys[0].c_str()).AsInt(), Eq(configValue["i"].AsInt()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[1].c_str()).AsBool(), Eq(configValue["b_t"].AsBool()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[2].c_str()).AsBool(), Eq(configValue["b_f"].AsBool()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[3].c_str()).IsNull(), Eq(configValue["n"].IsNull()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[4].c_str()).AsDouble(), Eq(configValue["d"].AsDouble()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[5].c_str()).AsString(), StrEq(configValue["s"].AsString()));
    EXPECT_THAT(configValue.FindMember(expectedKeys[6].c_str()).FindMember("name").AsString(),
        StrEq(configValue["object"]["name"].AsString()));

    auto keys = configValue.GetKeys();
    std::sort(keys.begin(), keys.end());
    std::sort(expectedKeys.begin(), expectedKeys.end());
    EXPECT_THAT(keys, Eq(expectedKeys));
}

TEST(FitConfigWritableValueTest, should_return_array_value_when_get_given_array_value)
{
    auto value = WritableValue::New();
    auto& configValue = *value;
    constexpr uint32_t expectSize = 7;
    configValue.SetArray().Reserve(expectSize);
    configValue.PushBack().SetObject().AddMember("name").SetValue("nameValue");
    configValue.PushBack().SetValue(true);
    configValue.PushBack().SetValue(123);
    configValue.PushBack().SetValue(123.123);
    configValue.PushBack().SetValue("ss");
    configValue.PushBack().SetArray();
    configValue.PushBack();

    ASSERT_THAT(configValue.IsArray(), Eq(true));
    ASSERT_THAT(configValue.GetType(), Eq(VALUE_TYPE_ARRAY));
    ASSERT_THAT(configValue.Size(), Eq(expectSize));
    EXPECT_THAT(configValue[0]["name"].AsString(), StrEq("nameValue"));
    EXPECT_THAT(configValue[1].AsBool(), Eq(true));
    EXPECT_THAT(configValue[2].AsInt(), Eq(123));
    EXPECT_THAT(configValue[3].AsDouble(), Eq(123.123));
    EXPECT_THAT(configValue[3].AsDouble(1.0), Eq(123.123));
    EXPECT_THAT(configValue[4].AsString(), StrEq("ss"));
    EXPECT_THAT(configValue[5].IsArray(), Eq(true));
    EXPECT_THAT(configValue[6].IsNull(), Eq(true));
    EXPECT_THAT(configValue[7].IsNull(), Eq(true));
}

TEST(FitConfigWritableValueTest, should_return_default_value_when_as_value_given_not_exist_key)
{
    auto value = WritableValue::New();
    auto& configValue = *value;
    configValue.SetObject();
    configValue.AddMember("hello").SetValue("string");

    EXPECT_THAT(configValue["helloXXXX"].AsBool(true), Eq(true));
    EXPECT_THAT(configValue["helloXXXX"].AsInt(1), Eq(1));
    EXPECT_THAT(configValue["helloXXXX"].AsDouble(666.3), Eq(666.3));
    EXPECT_THAT(configValue["helloXXXX"].AsString("default"), StrEq("default"));
}

TEST(FitConfigWritableValueTest, should_return_null_value_when_get_given_non_null_value_set_null)
{
    auto value = WritableValue::New();
    value->SetValue(true).SetNull();
    EXPECT_TRUE(value->IsNull());
}

TEST(FitConfigWritableValueTest, should_throw_exception_when_access_given_attach_null_value)
{
    WritableValue value;

    EXPECT_THAT(value.GetType(), ::testing::Eq(VALUE_TYPE_NULL));
    EXPECT_THAT(value.SetNull().IsNull(), ::testing::Eq(true));
    EXPECT_THROW(value[0], std::runtime_error);
    EXPECT_THROW(value.Size(), std::runtime_error);
    EXPECT_THROW(value.AsBool(), std::runtime_error);
    EXPECT_THROW(value.AsInt(), std::runtime_error);
    EXPECT_THROW(value.AsDouble(), std::runtime_error);
    EXPECT_THROW(value.AsString(), std::runtime_error);
    EXPECT_THROW(value.AsString(), std::runtime_error);
    EXPECT_THROW(value["key"], std::runtime_error);
    EXPECT_THROW(value.AddMember("1"), std::runtime_error);
    EXPECT_THROW(value.FindMember("1"), std::runtime_error);
    EXPECT_THROW(value.SetValue(true), std::runtime_error);
    EXPECT_THROW(value.SetValue(123), std::runtime_error);
    EXPECT_THROW(value.SetValue(123.00), std::runtime_error);
    EXPECT_THROW(value.SetValue("value"), std::runtime_error);
    EXPECT_THROW(value.SetValue(string("str")), std::runtime_error);
    EXPECT_THROW(value.SetObject(), std::runtime_error);
    EXPECT_THROW(value.GetKeys(), std::runtime_error);
    EXPECT_THROW(value.SetArray(), std::runtime_error);
    EXPECT_THROW(value.Reserve(1), std::runtime_error);
    EXPECT_THROW(value.PushBack(), std::runtime_error);
}