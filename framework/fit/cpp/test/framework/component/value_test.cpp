/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/6/10
 * Notes:       :
 */

#include <vector>
#include <functional>
#include <fit/value.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

class ValueTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ValueTest, should_return_true_when_call_is_null_given_created_value)
{
    Fit::Value value;

    EXPECT_THAT(value.IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_create_null_value_when_construct_given_moved_value)
{
    Fit::Value movedValue {true};
    Fit::Value tmp = std::move(movedValue);
    Fit::Value value(movedValue);

    EXPECT_THAT(value.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.IsBool(), ::testing::Eq(false));
    EXPECT_THAT(value.IsNumber(), ::testing::Eq(false));
    EXPECT_THAT(value.IsString(), ::testing::Eq(false));
    EXPECT_THAT(value.IsObject(), ::testing::Eq(false));
    EXPECT_THAT(value.IsArray(), ::testing::Eq(false));
    EXPECT_THAT(value.IsArray(), ::testing::Eq(false));
}

TEST_F(ValueTest, should_return_correct_value_when_as_bool_given_set_bool_value)
{
    Fit::Value value;
    value.SetBool(true);

    ASSERT_THAT(value.IsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsBool(false), ::testing::Eq(true));
}

TEST_F(ValueTest, should_throw_exception_value_when_as_bool_given_not_bool_value)
{
    Fit::Value value;

    EXPECT_THROW(value.AsBool(), std::logic_error);
    EXPECT_THAT(value.AsBool(true), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_as_int32_given_set_int32_value)
{
    Fit::Value value;
    int32_t expectValue = 123;
    value.SetInt32(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_as_int32_given_set_negative_int32_value)
{
    Fit::Value value;
    int32_t expectValue = -123;
    value.SetInt32(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsInt32(0), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_throw_exception_when_as_int32_given_value_over_less_INT_MIN)
{
    Fit::Value value;
    double expectValue = INT_MIN;
    expectValue -= 1;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THROW(value.AsInt32(), std::logic_error);
    EXPECT_THAT(value.AsInt32(999), ::testing::Eq(999));
}

TEST_F(ValueTest, should_throw_exception_when_as_int32_given_value_great_INT_MAX)
{
    Fit::Value value;
    double expectValue = INT32_MAX;
    expectValue += 1;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THROW(value.AsInt32(), std::logic_error);
    EXPECT_THAT(value.AsInt32(999), ::testing::Eq(999));
}

TEST_F(ValueTest, should_return_correct_value_when_as_uint32_given_set_uint32_value)
{
    Fit::Value value;
    uint32_t expectValue = 123;
    value.SetUInt32(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsUInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_throw_exception_when_as_uint32_given_value_less_0)
{
    Fit::Value value;
    double expectValue = -1;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THROW(value.AsUInt32(), std::logic_error);
    EXPECT_THAT(value.AsUInt32(999), ::testing::Eq(999));
}

TEST_F(ValueTest, should_throw_exception_when_as_uint32_given_value_great_UINT_MAX)
{
    Fit::Value value;
    double expectValue = UINT32_MAX;
    expectValue += 1;
    value.SetDouble(expectValue);

    EXPECT_THROW(value.AsUInt32(), std::logic_error);
}

TEST_F(ValueTest, should_return_correct_value_when_as_double_given_set_double_value)
{
    Fit::Value value;
    double expectValue = 123.33;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsDouble(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsDouble(1.0), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_as_int_given_set_double_value_in_valid_range)
{
    Fit::Value value;
    double expectValue = -123;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsInt32(1), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_as_uint_given_set_double_value_in_valid_range)
{
    Fit::Value value;
    double expectValue = 456;
    value.SetDouble(expectValue);

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsUInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsUInt32(1), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_as_string_given_set_string_value)
{
    Fit::Value value;
    const char *expectValue = "this is a test";
    value.SetString("1");
    value.SetString(expectValue);

    ASSERT_THAT(value.IsString(), ::testing::Eq(true));
    EXPECT_THAT(value.AsString(), ::testing::StrEq(expectValue));
    EXPECT_THAT(value.AsString(""), ::testing::StrEq(expectValue));
}

TEST_F(ValueTest, should_throw_exception_when_as_xxx_given_null_value)
{
    Fit::Value value;
    const Fit::Value constValue;

    EXPECT_THROW(value.AsBool(), std::logic_error);
    EXPECT_THROW(value.AsInt32(), std::logic_error);
    EXPECT_THROW(value.AsUInt32(), std::logic_error);
    EXPECT_THROW(value.AsDouble(), std::logic_error);
    EXPECT_THROW(value.AsString(), std::logic_error);
    EXPECT_THROW(value.AsArray(), std::logic_error);
    EXPECT_THROW(constValue.AsArray(), std::logic_error);
    EXPECT_THROW(value.AsObject(), std::logic_error);
    EXPECT_THROW(constValue.AsObject(), std::logic_error);
}

TEST_F(ValueTest, should_get_default_value_when_as_xxx_given_null_value_and_default_value)
{
    Fit::Value value;
    Fit::ArrayValue arrayValue{true};
    Fit::ObjectValue objectValue{"bool", true};

    EXPECT_THAT(value.AsBool(true), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(-1), ::testing::Eq(-1));
    EXPECT_THAT(value.AsUInt32(1), ::testing::Eq(1));
    EXPECT_THAT(value.AsDouble(1.0), ::testing::Eq(1.0));
    EXPECT_THAT(value.AsString("1"), ::testing::StrEq("1"));
    EXPECT_THAT(value.AsArray(arrayValue)[0].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject(objectValue)["bool"].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_as_object_given_object_value)
{
    Fit::Value value;
    value.SetObject();
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();

    EXPECT_THAT(object.Empty(), ::testing::Eq(true));
    EXPECT_THAT(object.Size(), ::testing::Eq(0));
    EXPECT_THAT(constObject.Empty(), ::testing::Eq(true));
    EXPECT_THAT(constObject.Size(), ::testing::Eq(0));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_bool_value)
{
    Fit::Value value;
    Fit::ObjectValue defaultValue;
    const char *name = "b";
    bool expectValue = true;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name, expectValue);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].AsBool(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsObject(defaultValue)[name].AsBool(), ::testing::Eq(expectValue));
    EXPECT_THAT(constObject[name].AsBool(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_null_value)
{
    Fit::Value value;
    const char *name = "b";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(constObject[name].IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_int32_value)
{
    Fit::Value value;
    const char *name = "i";
    int32_t expectValue = -456;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name, expectValue);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].AsInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(constObject[name].AsInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_uint32_value)
{
    Fit::Value value;
    const char *name = "ui";
    uint32_t expectValue = 456;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name, expectValue);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].AsInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(constObject[name].AsInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_double_value)
{
    Fit::Value value;
    const char *name = "d";
    double expectValue = 456.999;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name, expectValue);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].AsDouble(), ::testing::Eq(expectValue));
    EXPECT_THAT(constObject[name].AsDouble(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_string_value)
{
    Fit::Value value;
    const char *name = "s";
    const char *expectValue = "this is a test";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name, expectValue);

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].AsString(), ::testing::StrEq(expectValue));
    EXPECT_THAT(constObject[name].AsString(), ::testing::StrEq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_object_value)
{
    Fit::Value value;
    const char *name = "ui";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name).SetObject();

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].IsObject(), ::testing::Eq(true));
    EXPECT_THAT(constObject[name].IsObject(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_object_value_and_add_array_value)
{
    Fit::Value value;
    const char *name = "ui";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name).SetArray();

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(object[name].IsArray(), ::testing::Eq(true));
    EXPECT_THAT(constObject[name].IsArray(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_remove_a_exist_member_given_object_value_and_add_a_value)
{
    Fit::Value value;
    const char *name = "ui";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name).SetArray();
    object.Remove(name);

    EXPECT_THAT(object[name].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(constObject[name].IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_clear_given_object_value_and_add_some_values)
{
    Fit::Value value;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add("1").SetBool(true);
    object.Add("2").SetUInt32(123);

    object.Clear();

    EXPECT_THAT(object.Empty(), ::testing::Eq(true));
    EXPECT_THAT(constObject.Empty(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_remove_with_iter_given_object_value_and_add_a_value)
{
    Fit::Value value;
    const char *name = "ui";
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();
    object.Add(name).SetArray();
    object.Remove(object.Find("ui"));

    EXPECT_THAT(object[name].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(constObject[name].IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_true_when_call_exist_given_object_value_and_exist_name)
{
    Fit::Value value;
    auto &object = value.SetObject();
    object.Add("1").SetBool(true);
    object.Add("2").SetUInt32(123);

    EXPECT_THAT(object.Exist("1"), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_default_value_when_AsObject_given_non_object_type)
{
    Fit::Value value;
    Fit::ObjectValue defaultValue {"bool", true};

    EXPECT_THAT(value.AsObject(defaultValue)["bool"].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_iter_when_call_find_given_object_value_and_exist_name)
{
    Fit::Value value;
    auto &object = value.SetObject();
    auto &constObject = ((const Fit::Value &)value).AsObject();

    object.Add("1").SetBool(true);
    object.Add("2").SetUInt32(123);

    auto iter = object.Find("1");
    auto constIter = constObject.Find("2");

    ASSERT_THAT(iter, ::testing::Not(object.End()));
    EXPECT_THAT(iter->Name(), ::testing::StrEq("1"));
    EXPECT_THAT(iter->Value().AsBool(), ::testing::Eq(true));

    ASSERT_THAT(constIter, ::testing::Not(constObject.End()));
    EXPECT_THAT(constIter->Name(), ::testing::StrEq("2"));
    EXPECT_THAT(constIter->Value().AsInt32(), ::testing::Eq(123));
}

TEST_F(ValueTest, should_get_all_member_names_when_get_names_given_object_value_and_add_some_values)
{
    Fit::Value value;
    auto &object = value.SetObject();
    object.Add("1").SetBool(true);
    object.Add("2").SetUInt32(123);
    object.Add("3").SetString("123");

    auto names = object.GetNames();

    ASSERT_THAT(names.size(), ::testing::Eq(3));
    EXPECT_THAT(std::find_if(names.begin(), names.end(), [](const Fit::string &item) { return item == "1"; }),
        ::testing::Not(names.end()));
    EXPECT_THAT(std::find_if(names.begin(), names.end(), [](const Fit::string &item) { return item == "2"; }),
        ::testing::Not(names.end()));
    EXPECT_THAT(std::find_if(names.begin(), names.end(), [](const Fit::string &item) { return item == "3"; }),
        ::testing::Not(names.end()));
}

TEST_F(ValueTest, should_return_correct_value_when_as_array_given_array_value)
{
    Fit::Value value;
    value.SetArray();
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();

    EXPECT_THAT(array.Empty(), ::testing::Eq(true));
    EXPECT_THAT(array.Size(), ::testing::Eq(0));
    EXPECT_THAT(constArray.Size(), ::testing::Eq(0));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_bool_value)
{
    Fit::Value value;
    Fit::ArrayValue defaultValue;
    bool expectValue = true;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack(expectValue);

    EXPECT_THAT(array[0].AsBool(), ::testing::Eq(expectValue));
    EXPECT_THAT(value.AsArray(defaultValue)[0].AsBool(), ::testing::Eq(expectValue));
    EXPECT_THAT(constArray[0].AsBool(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_null_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack();

    EXPECT_THAT(array[0].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(constArray[0].IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_int32_value)
{
    Fit::Value value;
    int32_t expectValue = -456;
    auto &array = value.SetArray();
    array.PushBack(expectValue);

    EXPECT_THAT(array[0].AsInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_uint32_value)
{
    Fit::Value value;
    uint32_t expectValue = 456;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack(expectValue);

    EXPECT_THAT(array[0].AsInt32(), ::testing::Eq(expectValue));
    EXPECT_THAT(constArray[0].AsInt32(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_double_value)
{
    Fit::Value value;
    double expectValue = 456.999;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack(expectValue);

    EXPECT_THAT(array[0].AsDouble(), ::testing::Eq(expectValue));
    EXPECT_THAT(constArray[0].AsDouble(), ::testing::Eq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_string_value)
{
    Fit::Value value;
    const char *expectValue = "this is a test";
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack(expectValue);

    EXPECT_THAT(array[0].AsString(), ::testing::StrEq(expectValue));
    EXPECT_THAT(constArray[0].AsString(), ::testing::StrEq(expectValue));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_object_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack().SetObject();

    EXPECT_THAT(array[0].IsObject(), ::testing::Eq(true));
    EXPECT_THAT(constArray[0].IsObject(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_correct_value_when_get_member_given_array_value_and_add_array_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack().SetArray();

    EXPECT_THAT(array[0].IsArray(), ::testing::Eq(true));
    EXPECT_THAT(constArray[0].IsArray(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_remove_a_exist_member_given_array_value_and_add_a_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack();
    array.Remove(0);

    EXPECT_THAT(array.Empty(), ::testing::Eq(true));
    EXPECT_THAT(constArray.Empty(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_remove_with_iter_given_array_value_and_add_a_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    auto &constArray = ((const Fit::Value &)value).AsArray();
    array.PushBack();
    array.Remove(array.Begin());

    EXPECT_THAT(array.Empty(), ::testing::Eq(true));
    EXPECT_THAT(constArray.Empty(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_return_default_value_when_AsArray_given_non_array_type)
{
    Fit::Value value;
    Fit::ArrayValue defaultValue {true};

    EXPECT_THAT(value.AsArray(defaultValue)[0].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_pop_back_given_array_value_and_add_a_value)
{
    Fit::Value value;
    auto &array = value.SetArray();
    array.PushBack();
    array.PopBack();

    EXPECT_THAT(array.Empty(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_removed_when_clear_given_array_value_and_add_some_values)
{
    Fit::Value value;
    auto &array = value.SetArray();
    array.PushBack().SetBool(true);
    array.PushBack().SetInt32(-123);

    array.Clear();

    EXPECT_THAT(array.Empty(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_range_when_for_range_given_array_value_and_add_some_values)
{
    Fit::Value value;
    auto &array = value.SetArray();
    array.PushBack().SetBool(true);
    array.PushBack().SetInt32(-123);

    std::vector<std::function<void(const Fit::Value &node)>> checkList = {
        [](const Fit::Value &node) { EXPECT_THAT(node.AsBool(), ::testing::Eq(true)); },
        [](const Fit::Value &node) { EXPECT_THAT(node.AsInt32(), ::testing::Eq(-123)); }
    };
    auto checkStarter = std::begin(checkList);
    for (const auto &node : array) {
        (*(checkStarter++))(node);
    }
}

TEST_F(ValueTest, should_range_when_for_range_with_modify_given_array_value_and_add_some_values)
{
    Fit::Value value;
    auto &array = value.SetArray();
    array.PushBack().SetString("1239");
    array.PushBack().SetDouble(12);

    std::vector<std::function<void(Fit::Value &node)>> changeList = {
        [](Fit::Value &node) { node.SetBool(true); },
        [](Fit::Value &node) { node.SetInt32(-123); }
    };

    auto changeStarter = std::begin(changeList);
    for (auto &node : array) {
        (*(changeStarter++))(node);
    }

    EXPECT_THAT(array[0].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(array[1].AsInt32(), ::testing::Eq(-123));
}

TEST_F(ValueTest, should_range_when_for_range_given_object_value_and_add_some_values)
{
    Fit::Value value;
    auto &object = value.SetObject();
    object["b"].SetBool(true);
    object["i"].SetInt32(-123);

    std::vector<std::function<void(const char *name, const Fit::Value &node)>> checkList = {
        [](const char *name, const Fit::Value &node) {
            EXPECT_THAT(name, ::testing::StrEq("b"));
            EXPECT_THAT(node.AsBool(), ::testing::Eq(true));
        },
        [](const char *name, const Fit::Value &node) {
            EXPECT_THAT(name, ::testing::StrEq("i"));
            EXPECT_THAT(node.AsInt32(), ::testing::Eq(-123));
        }
    };
    auto checkStarter = std::begin(checkList);
    for (const auto &node : object) {
        (*(checkStarter++))(node.Name(), node.Value());
    }
}

TEST_F(ValueTest, should_range_when_for_range_with_modify_given_object_value_and_add_some_values)
{
    Fit::Value value;
    auto &object = value.SetObject();
    object["b"].SetInt32(-111);
    object["i"].SetBool(false);

    std::vector<std::function<void(const char *name, Fit::Value &node)>> changeList = {
        [](const char *name, Fit::Value &node) {
            node.SetBool(true);
        },
        [](const char *name, Fit::Value &node) {
            node.SetInt32(-123);
        }
    };
    auto starter = std::begin(changeList);
    for (auto &node : object) {
        (*(starter++))(node.Name(), node.Value());
    }

    EXPECT_THAT(object["b"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(object["i"].AsInt32(), ::testing::Eq(-123));
}

TEST_F(ValueTest, should_get_moved_value_when_assign_value_given_rvalue)
{
    Fit::Value boolValue;
    boolValue.SetBool(true);

    Fit::Value value = std::move(boolValue);

    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_do_nothing_when_assign_value_given_rvalue_self)
{
    Fit::Value value{true};
    value = std::move(value);

    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_do_nothing_when_assign_value_given_lvalue_self)
{
    Fit::Value value{true};
    value = value;

    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_copyed_value_when_copy_value_given_bool_value)
{
    Fit::Value boolValue;
    boolValue.SetBool(true);

    Fit::Value value(boolValue);

    EXPECT_THAT(boolValue.AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_copyed_value_when_copy_value_given_int32_value)
{
    Fit::Value intValue;
    intValue.SetInt32(-123);

    Fit::Value value(intValue);

    EXPECT_THAT(intValue.AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(-123));
}

TEST_F(ValueTest, should_get_copyed_value_when_copy_value_given_string_value)
{
    Fit::Value stringValue;
    stringValue.SetString("string");

    Fit::Value value(stringValue);

    EXPECT_THAT(stringValue.AsString(), ::testing::StrEq("string"));
    EXPECT_THAT(value.AsString(), ::testing::StrEq("string"));
}

TEST_F(ValueTest, should_get_copyed_value_when_copy_value_given_value_with_object_type)
{
    Fit::Value objectValue;
    objectValue.SetObject().Add("b", true);

    Fit::Value value(objectValue);

    EXPECT_THAT(objectValue.AsObject()["b"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject()["b"].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_copyed_value_when_copy_value_given_value_with_array_type)
{
    Fit::Value arrayValue;
    arrayValue.SetArray().PushBack(true);

    Fit::Value value(arrayValue);

    EXPECT_THAT(arrayValue.AsArray()[0].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[0].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_moved_value_when_copy_value_given_bool_value)
{
    Fit::Value boolValue;
    boolValue.SetBool(true);

    Fit::Value value(std::move(boolValue));

    EXPECT_THAT(boolValue.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_moved_value_when_copy_value_given_int32_value)
{
    Fit::Value intValue;
    intValue.SetInt32(-123);

    Fit::Value value(std::move(intValue));

    EXPECT_THAT(intValue.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(-123));
}

TEST_F(ValueTest, should_get_moved_value_when_copy_value_given_string_value)
{
    Fit::Value stringValue;
    stringValue.SetString("string");

    Fit::Value value(std::move(stringValue));

    EXPECT_THAT(stringValue.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsString(), ::testing::StrEq("string"));
}

TEST_F(ValueTest, should_get_moved_value_when_copy_value_given_value_with_object_type)
{
    Fit::Value objectValue;
    objectValue.SetObject().Add("b", true);

    Fit::Value value(std::move(objectValue));

    EXPECT_THAT(objectValue.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject()["b"].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_moved_value_when_copy_value_given_value_with_array_type)
{
    Fit::Value arrayValue;
    arrayValue.SetArray().PushBack(true);

    Fit::Value value(std::move(arrayValue));

    EXPECT_THAT(arrayValue.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[0].AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_a_null_value_when_explicit_create_given_nullptr)
{
    Fit::Value value = nullptr;

    ASSERT_THAT(value.IsNull(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_a_bool_value_when_explicit_create_given_bool_value)
{
    Fit::Value value = true;

    ASSERT_THAT(value.IsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_a_number_value_when_explicit_create_given_int_value)
{
    Fit::Value value = -123;

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsInt32(), ::testing::Eq(-123));
}

TEST_F(ValueTest, should_get_a_number_value_when_explicit_create_given_double_value)
{
    Fit::Value value = -123.123;

    ASSERT_THAT(value.IsNumber(), ::testing::Eq(true));
    EXPECT_THAT(value.AsDouble(), ::testing::Eq(-123.123));
}

TEST_F(ValueTest, should_get_a_string_value_when_explicit_create_given_string_value)
{
    Fit::Value value = "hello world";

    ASSERT_THAT(value.IsString(), ::testing::Eq(true));
    EXPECT_THAT(value.AsString(), ::testing::StrEq("hello world"));
}

TEST_F(ValueTest, should_get_a_object_value_when_explicit_create_given_init_values_like_object)
{
    Fit::Value value = {
        {"null", nullptr},
        {"b", true},
        {"i", -123},
        {"d", -123.123},
        {"s", "hello"},
    };

    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject()["null"].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject()["b"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsObject()["i"].AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(value.AsObject()["d"].AsDouble(), ::testing::Eq(-123.123));
    EXPECT_THAT(value.AsObject()["s"].AsString(), ::testing::StrEq("hello"));
}

TEST_F(ValueTest, should_get_a_array_value_when_explicit_create_given_init_values_like_array)
{
    Fit::Value value = {
        nullptr,
        true,
        -123,
        -123.123,
        "hello",
    };

    ASSERT_THAT(value.IsArray(), ::testing::Eq(true));
    int32_t i = 0;
    EXPECT_THAT(value.AsArray()[i++].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[i++].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[i++].AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(value.AsArray()[i++].AsDouble(), ::testing::Eq(-123.123));
    EXPECT_THAT(value.AsArray()[i++].AsString(), ::testing::StrEq("hello"));
}

TEST_F(ValueTest, should_get_a_nested_array_value_when_explicit_create_given_nested_init_values_like_array)
{
    Fit::Value value = {
        true,
        -123,
        -123.123,
        "hello",
        {
            {"b", true},
            {"i", -123},
            {"d", -123.123},
            {"s", "hello"},
            {"array", {
                true,
                -123,
                -123.123,
                "hello",
            }},
        }
    };

    ASSERT_THAT(value.IsArray(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[0].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[1].AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(value.AsArray()[2].AsDouble(), ::testing::Eq(-123.123));
    EXPECT_THAT(value.AsArray()[3].AsString(), ::testing::StrEq("hello"));
    EXPECT_THAT(value.AsArray()[4].IsObject(), ::testing::Eq(true));
    EXPECT_THAT(value.AsArray()[4].AsObject()["array"].IsArray(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_get_object_value_when_explicit_create_given_init_values_object)
{
    Fit::ObjectValue object = {
        {"b", true},
        {"i", -123},
        {"d", -123.123},
        {"s", "hello"},
        {"array",
            {
                true,
                -123,
                -123.123,
                "hello",
            }
        },
    };

    EXPECT_THAT(object["b"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(object["i"].AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(object["d"].AsDouble(), ::testing::Eq(-123.123));
    EXPECT_THAT(object["s"].AsString(), ::testing::StrEq("hello"));
    EXPECT_THAT(object["array"].IsArray(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_throw_exception_when_explicit_create_given_init_values_cannot_as_object)
{
    auto createObjectValue = []() {
        Fit::ObjectValue {
            1,
            {"b", true}
        };
    };
    EXPECT_THROW(createObjectValue(), std::logic_error);
}

TEST_F(ValueTest, should_get_array_value_when_explicit_create_ArrayValue_given_init_values_to_array_value)
{
    Fit::ArrayValue array = {
        true,
        -123,
        -123.123,
        "hello",
        {
            {"b", true},
            {"array", {
                true,
                -123,
                -123.123,
                "hello",
            }},
        }
    };

    EXPECT_THAT(array[0].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(array[1].AsInt32(), ::testing::Eq(-123));
    EXPECT_THAT(array[2].AsDouble(), ::testing::Eq(-123.123));
    EXPECT_THAT(array[3].AsString(), ::testing::StrEq("hello"));
    EXPECT_THAT(array[4].IsObject(), ::testing::Eq(true));
    EXPECT_THAT(array[4].AsObject()["array"].IsArray(), ::testing::Eq(true));
}

TEST_F(ValueTest, should_is_null_when_IsNull_given_moved_value)
{
    Fit::Value value(true);
    Fit::Value temp(std::move(value));

    EXPECT_THAT(value.IsNull(), ::testing::Eq(true));
    EXPECT_THAT(value.Type(), ::testing::Eq(Fit::ValueType::NULL_));
}