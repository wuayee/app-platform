/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/9/13
 * Notes:       :
 */

#include <vector>
#include <fit/external/framework/formatter/json_converter.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace Fit::Framework::Formatter::Json;
using namespace ::testing;

class JsonConverterTest : public testing::Test {
public:
    void SetUp() override
    {
        ctx = Fit::Context::NewContext();
    }

    void TearDown() override
    {
        ContextDestroy(ctx);
    }

protected:
    ContextObj ctx {};
};

TEST_F(JsonConverterTest, should_return_null_string_when_call_MessageToJson_given_null_value)
{
    Fit::Value value;

    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    auto ret = MessageToJson(nullptr, value, writer);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq("null"));
}

TEST_F(JsonConverterTest, should_return_bool_string_when_call_MessageToJson_given_bool_value)
{
    Fit::Value value;

    value.SetBool(true);

    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    auto ret = MessageToJson(nullptr, value, writer);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq("true"));
}

TEST_F(JsonConverterTest, should_return_number_string_when_call_MessageToJson_given_number_value)
{
    Fit::Value value;
    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);

    value.SetInt32(-123);
    auto ret = MessageToJson(nullptr, value, writer);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq("-123.0"));

    sb.Clear();
    writer.Reset(sb);
    value.SetInt32(123);
    ret = MessageToJson(nullptr, value, writer);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq("123.0"));

    sb.Clear();
    writer.Reset(sb);
    value.SetDouble(123.123);
    ret = MessageToJson(nullptr, value, writer);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq("123.123"));
}

TEST_F(JsonConverterTest, should_return_string_string_when_call_MessageToJson_given_string_value)
{
    Fit::Value value;

    value.SetString("hello world");

    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    auto ret = MessageToJson(nullptr, value, writer);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq(R"("hello world")"));
}

TEST_F(JsonConverterTest, should_return_object_string_when_call_MessageToJson_given_object_value)
{
    Fit::Value value;

    auto &object = value.SetObject();
    object.Add("n");
    object.Add("t", true)
        .Add("f", false)
        .Add("d", 123.33)
        .Add("s", "ss");
    object.Add("object").SetObject().Add("o1", "hello");
    object.Add("array").SetArray().PushBack("hello");

    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    auto ret = MessageToJson(nullptr, value, writer);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq(
        R"({"array":["hello"],"d":123.33,"f":false,"n":null,"object":{"o1":"hello"},"s":"ss","t":true})"));
}

TEST_F(JsonConverterTest, should_return_array_string_when_call_MessageToJson_given_array_value)
{
    Fit::Value value;

    auto &array = value.SetArray();
    array.PushBack();
    array.PushBack(true)
        .PushBack(false)
        .PushBack(123.33)
        .PushBack("ss");
    array.PushBack().SetObject().Add("o1", "hello");
    array.PushBack().SetArray().PushBack("hello");

    rapidjson::StringBuffer sb;
    rapidjson::Writer<rapidjson::StringBuffer> writer(sb);
    auto ret = MessageToJson(nullptr, value, writer);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(sb.GetString(), ::testing::StrEq(
        R"([null,true,false,123.33,"ss",{"o1":"hello"},["hello"]])"));
}

TEST_F(JsonConverterTest, should_return_null_value_when_call_JsonToMessage_given_null_string)
{
    Fit::Value value;

    Fit::string jsonString = "null";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));

    auto ret = JsonToMessage(nullptr, doc, value);

    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value.IsNull(), ::testing::Eq(true));
}

TEST_F(JsonConverterTest, should_return_bool_value_when_call_JsonToMessage_given_bool_string)
{
    Fit::Value value;

    Fit::string jsonString = "true";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(true));

    jsonString = "false";
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value.AsBool(), ::testing::Eq(false));
}

TEST_F(JsonConverterTest, should_return_number_value_when_call_JsonToMessage_given_number_string)
{
    Fit::Value value;

    Fit::string jsonString = "123.22";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value.AsDouble(), ::testing::Eq(123.22));
}

TEST_F(JsonConverterTest, should_return_string_value_when_call_JsonToMessage_given_string_string)
{
    Fit::Value value;

    Fit::string jsonString = "\"hello\"";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value.AsString(), ::testing::StrEq("hello"));
}

TEST_F(JsonConverterTest, should_return_object_value_when_call_JsonToMessage_given_object_string)
{
    Fit::Value value;

    Fit::string jsonString =
        R"({"array":["hello"],"d":123.33,"f":false,"n":null,"object":{"o1":"hello"},"s":"ss","t":true})";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_THAT(value.IsObject(), ::testing::Eq(true));

    auto &object = value.AsObject();
    EXPECT_THAT(object["n"].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(object["t"].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(object["d"].AsDouble(), ::testing::Eq(123.33));
    EXPECT_THAT(object["s"].AsString(), ::testing::StrEq("ss"));
    EXPECT_THAT(object["object"].AsObject()["o1"].AsString(), ::testing::StrEq("hello"));
    EXPECT_THAT(object["array"].AsArray()[0].AsString(), ::testing::StrEq("hello"));
}

TEST_F(JsonConverterTest, should_return_array_value_when_call_JsonToMessage_given_array_string)
{
    Fit::Value value;

    Fit::string jsonString = R"([null,true,false,123.33,"ss",{"o1":"hello"},["hello"]])";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    ASSERT_THAT(value.IsArray(), ::testing::Eq(true));

    auto &array = value.AsArray();
    EXPECT_THAT(array[0].IsNull(), ::testing::Eq(true));
    EXPECT_THAT(array[1].AsBool(), ::testing::Eq(true));
    EXPECT_THAT(array[2].AsBool(), ::testing::Eq(false));
    EXPECT_THAT(array[3].AsDouble(), ::testing::Eq(123.33));
    EXPECT_THAT(array[4].AsString(), ::testing::StrEq("ss"));
    EXPECT_THAT(array[5].AsObject()["o1"].AsString(), ::testing::StrEq("hello"));
    EXPECT_THAT(array[6].AsArray()[0].AsString(), ::testing::StrEq("hello"));
}

TEST_F(JsonConverterTest, should_return_bool_value_when_call_JsonToMessage_given_bool_string_as_string_type)
{
    bool value;

    Fit::string jsonString = R"("true")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value, ::testing::Eq(true));

    jsonString = R"("false")";
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value, ::testing::Eq(false));
}

TEST_F(JsonConverterTest, should_return_int_value_when_call_JsonToMessage_given_int_string_as_string_type)
{
    int32_t value;

    Fit::string jsonString = R"("123")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value, ::testing::Eq(123));
}

TEST_F(JsonConverterTest, should_return_double_value_when_call_JsonToMessage_given_double_string_as_string_type)
{
    double value;

    Fit::string jsonString = R"("123.33")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value, ::testing::Eq(123.33));
}

TEST_F(JsonConverterTest, should_return_float_value_when_call_JsonToMessage_given_float_string_as_string_type)
{
    float value;

    Fit::string jsonString = R"("123.33")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(value, ::testing::FloatEq(123.33));
}

TEST_F(JsonConverterTest, should_return_error_when_call_JsonToMessage_with_bool_given_number_as_string_type)
{
    bool value;

    Fit::string jsonString = R"("1")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_DESERIALIZE_JSON));
}

TEST_F(JsonConverterTest,
    should_return_error_when_call_JsonToMessage_with_int32_given_double_string_as_string_type)
{
    int32_t value;

    Fit::string jsonString = R"("123.23")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_DESERIALIZE_JSON));
}

TEST_F(JsonConverterTest, should_return_error_when_call_JsonToMessage_with_double_given_alpha_string)
{
    int32_t value;

    Fit::string jsonString = R"("true")";
    rapidjson::Document doc;
    doc.Parse(jsonString.c_str());
    ASSERT_THAT(doc.HasParseError(), ::testing::Eq(false));
    auto ret = JsonToMessage(nullptr, doc, value);
    ASSERT_THAT(ret, ::testing::Eq(FIT_ERR_DESERIALIZE_JSON));
}

TEST_F(JsonConverterTest, should_return_null_when_call_SerializeArgToString_with_level_one_null_arg)
{
    int32_t *arg {};
    Fit::any nullIntArg = arg;
    Fit::string result;
    Fit::string expectResult = "null";

    auto ret = SerializeArgToString<int32_t *, int32_t *>(nullptr, nullIntArg, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, Eq(expectResult));
}

TEST_F(JsonConverterTest,
    should_return_null_obj_when_call_DeserializeStringToArg_with_null_string_and_one_null_arg)
{
    Fit::any result;
    Fit::string nullString = "null";
    int32_t *expectResult = nullptr;

    auto ret = DeserializeStringToArg<int32_t *, int32_t>(nullptr, nullString, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(Fit::any_cast<int *>(result), Eq(expectResult));
}

TEST_F(JsonConverterTest, should_return_null_when_call_SerializeArgToString_with_level_two_null_arg)
{
    int32_t **arg {};
    Fit::any nullIntArg = arg;
    Fit::string result;
    Fit::string expectResult = "null";

    auto ret = SerializeArgToString<int32_t **, int32_t **>(nullptr, nullIntArg, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, Eq(expectResult));
}

TEST_F(JsonConverterTest,
    should_return_null_obj_when_call_DeserializeStringToArg_with_null_string_and_level_two_null_arg)
{
    Fit::any result;
    Fit::string nullString = "null";
    int32_t **expectResult = nullptr;

    auto ret = DeserializeStringToArg<int32_t **, int32_t *>(nullptr, nullString, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(Fit::any_cast<int **>(result), Eq(expectResult));
}

TEST_F(JsonConverterTest, should_return_null_when_call_SerializeRepeatedArgToString_with_null_arg)
{
    Fit::vector<int32_t> *arg {};
    Fit::any nullIntArg = arg;
    Fit::string result;
    Fit::string expectResult = "null";

    auto ret = SerializeRepeatedArgToString<Fit::vector<int32_t> *, Fit::vector<int32_t> *>(nullptr, nullIntArg,
        result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, Eq(expectResult));
}

TEST_F(JsonConverterTest, should_return_null_obj_when_call_DeserializeStringToRepeatedArg_with_null_string)
{
    Fit::any result;
    Fit::string nullString = "null";
    Fit::vector<int32_t> *expectResult = nullptr;

    auto ret = DeserializeStringToRepeatedArg<Fit::vector<int32_t> *, Fit::vector<int32_t>>(nullptr, nullString,
        result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(Fit::any_cast<Fit::vector<int32_t> *>(result), Eq(expectResult));
}

TEST_F(JsonConverterTest, should_return_null_when_call_SerializeMapArgToString_with_null_arg)
{
    using ArgType = Fit::map<int32_t, int32_t>;
    ArgType *arg {};
    Fit::any nullIntArg = arg;
    Fit::string result;
    Fit::string expectResult = "null";

    auto ret = SerializeMapArgToString<ArgType *, ArgType *>(nullptr, nullIntArg, result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(result, Eq(expectResult));
}

TEST_F(JsonConverterTest, should_return_null_obj_when_call_DeserializeStringToMapArg_with_null_string)
{
    Fit::any result;
    Fit::string nullString = "null";
    using ArgType = Fit::map<int32_t, int32_t>;
    ArgType *expectResult = nullptr;

    auto ret = DeserializeStringToMapArg<ArgType *, ArgType>(nullptr, nullString,
        result);
    ASSERT_THAT(ret, ::testing::Eq(FIT_OK));
    EXPECT_THAT(Fit::any_cast<ArgType *>(result), Eq(expectResult));
}

struct TestStruct {
    bool b;
    int32_t i32;
    uint32_t ui32;
    int64_t i64;
    uint64_t ui64;
    float f;
    double d;
    Fit::string str;
    Fit::bytes bytes;
    Fit::vector<int32_t> vi;
    Fit::vector<int32_t> emptyVi;
    Fit::map<Fit::string, Fit::string> dict;
    Fit::map<Fit::string, Fit::string> emptyDict;
};

namespace Fit {
namespace Framework {
namespace Formatter {
namespace Json {
template<>
FitCode MessageToJson(ContextObj ctx, const TestStruct &value, rapidjson::Writer<rapidjson::StringBuffer> &writer)
{
    writer.StartObject();

    writer.String("b");
    MessageToJson(ctx, ExtractArgToRef(value.b), writer);
    writer.String("i32");
    MessageToJson(ctx, ExtractArgToRef(value.i32), writer);
    writer.String("ui32");
    MessageToJson(ctx, ExtractArgToRef(value.ui32), writer);
    writer.String("i64");
    MessageToJson(ctx, ExtractArgToRef(value.i64), writer);
    writer.String("ui64");
    MessageToJson(ctx, ExtractArgToRef(value.ui64), writer);
    writer.String("f");
    MessageToJson(ctx, ExtractArgToRef(value.f), writer);
    writer.String("d");
    MessageToJson(ctx, ExtractArgToRef(value.d), writer);
    writer.String("str");
    MessageToJson(ctx, ExtractArgToRef(value.str), writer);
    writer.String("bytes");
    MessageToJson(ctx, ExtractArgToRef(value.bytes), writer);
    writer.String("vi");
    MessageToJson(ctx, ExtractArgToRef(value.vi), writer);
    writer.String("emptyVi");
    MessageToJson(ctx, ExtractArgToRef(value.emptyVi), writer);
    writer.String("dict");
    MessageToJson(ctx, ExtractArgToRef(value.dict), writer);
    writer.String("emptyDict");
    MessageToJson(ctx, ExtractArgToRef(value.emptyDict), writer);

    writer.EndObject();
    return FIT_OK;
}

template<>
FitCode JsonToMessage(ContextObj ctx, const rapidjson::Value &jsonValue, TestStruct &value)
{
    if (!jsonValue.HasMember("b")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["b"], value.b);

    if (!jsonValue.HasMember("i32")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["i32"], value.i32);

    if (!jsonValue.HasMember("ui32")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["ui32"], value.ui32);

    if (!jsonValue.HasMember("i64")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["i64"], value.i64);

    if (!jsonValue.HasMember("ui64")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["ui64"], value.ui64);

    if (!jsonValue.HasMember("f")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["f"], value.f);

    if (!jsonValue.HasMember("d")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["d"], value.d);

    if (!jsonValue.HasMember("str")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["str"], value.str);

    if (!jsonValue.HasMember("bytes")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["bytes"], value.bytes);

    if (!jsonValue.HasMember("vi")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["vi"], value.vi);

    if (!jsonValue.HasMember("emptyVi")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["emptyVi"], value.emptyVi);

    if (!jsonValue.HasMember("dict")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["dict"], value.dict);

    if (!jsonValue.HasMember("emptyDict")) {
        return FIT_ERR_DESERIALIZE_JSON;
    }
    JsonToMessage(ctx, jsonValue["emptyDict"], value.emptyDict);

    return FIT_OK;
}
}
}
}
}

TEST_F(JsonConverterTest, serialize_bool_type)
{
    // given
    const bool in = true;
    Fit::string expect = "true";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const bool *, bool>(ctx, Fit::any(&in), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_bool_type)
{
    // given
    Fit::string jsonValue = "true";
    bool expected = true;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<bool **, bool *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        bool **res = Fit::any_cast<bool **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_int32_type)
{
    // given
    const int32_t i = 1;
    Fit::string expect = "1";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const int32_t *, int32_t>(ctx, Fit::any(&i), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_int32_type)
{
    // given
    Fit::string jsonValue = "100";
    int32_t expected = 100;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<int32_t **, int32_t *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        int32_t **res = Fit::any_cast<int32_t **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_uint32_type)
{
    // given
    const uint32_t i = 1;
    Fit::string expect = "1";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const uint32_t *, uint32_t>(ctx, Fit::any(&i),
        out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_uint32_type)
{
    // given
    Fit::string jsonValue = "100";
    uint32_t expected = 100;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<uint32_t **, uint32_t *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        uint32_t **res = Fit::any_cast<uint32_t **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_int64_type)
{
    // given
    const int64_t i = 1;
    Fit::string expect = "1";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const int64_t *, int64_t>(ctx, Fit::any(&i), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_int64_type)
{
    // given
    Fit::string jsonValue = "100";
    int64_t expected = 100;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<int64_t **, int64_t *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        int64_t **res = Fit::any_cast<int64_t **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_uint64_type)
{
    // given
    const uint64_t i = 1;
    Fit::string expect = "1";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const uint64_t *, uint64_t>(ctx, Fit::any(&i),
        out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_uint64_type)
{
    // given
    Fit::string jsonValue = "100";
    uint64_t expected = 100;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<uint64_t **, uint64_t *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        uint64_t **res = Fit::any_cast<uint64_t **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_float_type)
{
    // given
    const float i = 1;
    Fit::string expect = "1.0";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const float *, float>(ctx, Fit::any(&i), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_float_type)
{
    // given
    Fit::string jsonValue = "37.5";
    float expected = 37.5;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<float **, float *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        float **res = Fit::any_cast<float **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_double_type)
{
    // given
    const double i = 38.88;
    Fit::string expect = "38.88";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const double *, double>(ctx, Fit::any(&i), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_double_type)
{
    // given
    Fit::string jsonValue = "52.3";
    double expected = 52.3;

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<double **, double *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        double **res = Fit::any_cast<double **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_string_type)
{
    // given
    const Fit::string str = "hello world";
    Fit::string expect = "\"hello world\"";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const Fit::string *, Fit::string>(ctx,
        Fit::any(&str), out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_string_type)
{
    // given
    Fit::string jsonValue = "\"hello world\"";
    Fit::string expected = "hello world";

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<Fit::string **, Fit::string *>(ctx, jsonValue,
        out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::string **res = Fit::any_cast<Fit::string **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_bytes_type)
{
    // given
    const Fit::bytes in = Fit::string("\x31\x32\x20");
    Fit::string expect = "\"MTIg\"";

    // when
    Fit::string out;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<const Fit::bytes *, Fit::bytes>(ctx, Fit::any(&in),
        out);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(out, expect);
}

TEST_F(JsonConverterTest, deserialize_bytes_type)
{
    // given
    Fit::string jsonValue = "\"MTIg\"";
    Fit::bytes expected = Fit::string("\x31\x32\x20");

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<Fit::bytes **, Fit::bytes *>(ctx, jsonValue,
        out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::bytes **res = Fit::any_cast<Fit::bytes **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_repeated_string_type)
{
    // given
    Fit::vector<Fit::string> in = {"hello", "world"};
    Fit::string expect = "[\"hello\",\"world\"]";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeRepeatedArgToString<Fit::vector<Fit::string>,
        Fit::vector<Fit::string>>(ctx, in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_repeated_string_type)
{
    // given
    Fit::string jsonValue = "[\"hello\", \"world\"]";
    Fit::vector<Fit::string> expected = {"hello", "world"};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToRepeatedArg<Fit::vector<Fit::string> **,
        Fit::vector<Fit::string> *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::vector<Fit::string> **res = Fit::any_cast<Fit::vector<Fit::string> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_repeated_bool_type)
{
    // given
    Fit::vector<bool> in = {true, true};
    Fit::string expect = "[true,true]";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeRepeatedArgToString<Fit::vector<bool>,
        Fit::vector<bool>>(ctx, in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_repeated_bool_type)
{
    // given
    Fit::string jsonValue = "[true,false]";
    Fit::vector<bool> expected = {true, false};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToRepeatedArg<Fit::vector<bool> **,
        Fit::vector<bool> *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::vector<bool> **res = Fit::any_cast<Fit::vector<bool> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_empty_repeated_string_type)
{
    // given
    Fit::vector<Fit::string> in = {};
    Fit::string expect = "[]";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeRepeatedArgToString<Fit::vector<Fit::string>,
        Fit::vector<Fit::string>>(ctx, in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_empty_repeated_string_type)
{
    // given
    Fit::string jsonValue = "[]";
    Fit::vector<Fit::string> expected = {};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToRepeatedArg<Fit::vector<Fit::string> **,
        Fit::vector<Fit::string> *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::vector<Fit::string> **res = Fit::any_cast<Fit::vector<Fit::string> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_map_string_string_type)
{
    // given
    Fit::map<Fit::string, Fit::string> in = {{"hello", "world"},
                                             {"ping",  "pong"}};
    Fit::string expect = "{\"hello\":\"world\",\"ping\":\"pong\"}";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeMapArgToString<Fit::map<Fit::string, Fit::string>,
        Fit::map<Fit::string, Fit::string>>(ctx, in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_map_string_string_type)
{
    // given
    Fit::string jsonValue = "{\"hello\":\"world\",\"ping\":\"pong\"}";
    Fit::map<Fit::string, Fit::string> expected = {{"hello", "world"},
                                                   {"ping",  "pong"}};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToMapArg<Fit::map<Fit::string, Fit::string> **,
        Fit::map<Fit::string, Fit::string> *>(ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::map<Fit::string, Fit::string> **res = Fit::any_cast<Fit::map<Fit::string, Fit::string> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_empty_map_string_string_type)
{
    // given
    Fit::map<Fit::string, Fit::string> in = {};
    Fit::string expect = "{}";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeMapArgToString<Fit::map<Fit::string, Fit::string>,
        Fit::map<Fit::string, Fit::string>>(ctx, in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_empty_map_string_string_type)
{
    // given
    Fit::string jsonValue = "{}";
    Fit::map<Fit::string, Fit::string> expected = {};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToMapArg<Fit::map<Fit::string, Fit::string> **,
        Fit::map<Fit::string, Fit::string> *>(
        ctx, jsonValue, out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        Fit::map<Fit::string, Fit::string> **res = Fit::any_cast<Fit::map<Fit::string, Fit::string> **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ(**res, expected);
    }
}

TEST_F(JsonConverterTest, serialize_common_struct_type)
{
    // given
    TestStruct in = {true, -100, 100, -111111111111111111, 1111111111111111111L, 4.0, -8.4, "haha",
        Fit::string("\x20\x31\x48"), {1, 2}, {},
        {{"hello", "world"}, {"ping", "pong"}}};
    Fit::string expect =
        "{\"b\":true,\"i32\":-100,\"ui32\":100,\"i64\":-111111111111111111,\"ui64\":1111111111111111111,"
        "\"f\":4.0,\"d\":-8.4,\"str\":\"haha\",\"bytes\":\"IDFI\",\"vi\":[1,2],\"emptyVi\":[],"
        "\"dict\":{\"hello\":\"world\",\"ping\":\"pong\"},\"emptyDict\":{}}";

    // when
    Fit::string result;
    auto ret = Fit::Framework::Formatter::Json::SerializeArgToString<TestStruct, TestStruct>(ctx,
        in, result);

    // then
    EXPECT_EQ(ret, FIT_OK);
    EXPECT_EQ(result, expect);
}

TEST_F(JsonConverterTest, deserialize_common_struct_type)
{
    // given
    Fit::string jsonValue =
        "{\"b\":true,\"i32\":-100,\"ui32\":100,\"i64\":-111111111111111111,\"ui64\":1111111111111111111,"
        "\"f\":4.0,\"d\":-8.4,\"str\":\"haha\",\"bytes\":\"IDFI\",\"vi\":[1,2],\"emptyVi\":[],"
        "\"dict\":{\"hello\":\"world\",\"ping\":\"pong\"},\"emptyDict\":{}}";
    TestStruct expected = {true, -100, 100, -111111111111111111, 1111111111111111111L, 4.0, -8.4, "haha",
        Fit::string("\x20\x31\x48"), {1, 2}, {},
        {{"hello", "world"}, {"ping", "pong"}}};

    // when
    Fit::any out;
    auto ret = Fit::Framework::Formatter::Json::DeserializeStringToArg<TestStruct **, TestStruct *>(ctx, jsonValue,
        out);
    EXPECT_EQ(ret, FIT_OK);

    if (ret == FIT_OK) {
        TestStruct **res = Fit::any_cast<TestStruct **>(out);
        EXPECT_NE(*res, nullptr);
        EXPECT_EQ((*res)->b, expected.b);
        EXPECT_EQ((*res)->i32, expected.i32);
        EXPECT_EQ((*res)->ui32, expected.ui32);
        EXPECT_EQ((*res)->i64, expected.i64);
        EXPECT_EQ((*res)->ui64, expected.ui64);
        EXPECT_EQ((*res)->str, expected.str);
        EXPECT_EQ((*res)->bytes, expected.bytes);
        EXPECT_EQ((*res)->f, expected.f);
        EXPECT_EQ((*res)->d, expected.d);
        EXPECT_EQ((*res)->vi, expected.vi);
        EXPECT_EQ((*res)->emptyVi, expected.emptyVi);
        EXPECT_EQ((*res)->dict, expected.dict);
        EXPECT_EQ((*res)->emptyDict, expected.emptyDict);
    }
}
