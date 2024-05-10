/*
* Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
* Description:
* Author: w00561424
* Date:2020/09/09
*/

#include <chrono>
#include <fit/stl/vector.hpp>
#include <fit/stl/string.hpp>
#include <fit/stl/map.hpp>
#include <fit/internal/util/json_converter_util.hpp>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::testing;
using std::make_shared;

class JsonConverterUtilTest : public ::testing::Test {
public:
    void SetUp() override
    {
        aliases_ = {"alias1", "alias2"};
        kvs_ = {{"key1", "value1"}, {"key2", "value2"}};
    }

    void TearDown() override
    {
    }
public:
    Fit::vector<Fit::string> aliases_;
    Fit::map<Fit::string, Fit::string> kvs_;
};

TEST_F(JsonConverterUtilTest, should_return_expect_json_format_when_call_string_set_to_json_given_string_set)
{
    // given
    Fit::string expectedAliasesStr = "[\"alias1\",\"alias2\"]";
    // when
    Fit::string aliasesStr {};
    Fit::JsonConverterUtil::MessageToJson(aliases_, aliasesStr);

    // then
    EXPECT_EQ(aliasesStr, expectedAliasesStr);
}

TEST_F(JsonConverterUtilTest, should_return_expect_string_set_format_when_call_json_to_string_set_given_string)
{
    // given
    Fit::string aliasesStr = "[\"alias1\",\"alias2\"]";
    // when
    Fit::vector<Fit::string> actualAliases;
    Fit::JsonConverterUtil::JsonToMessage(aliasesStr, actualAliases);
    // then
    ASSERT_EQ(actualAliases.size(), aliases_.size());
    EXPECT_EQ(actualAliases[0], aliases_[0]);
    EXPECT_EQ(actualAliases[1], aliases_[1]);
}

TEST_F(JsonConverterUtilTest, should_return_expect_json_format_when_call_kvs_to_json_given_kvs)
{
    // given
    Fit::string expectedKvsStr = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
    // when
    Fit::string kvsStr;
    Fit::JsonConverterUtil::MessageToJson(kvs_, kvsStr);

    // then
    EXPECT_EQ(kvsStr, expectedKvsStr);
}

TEST_F(JsonConverterUtilTest, should_return_expect_kvs_when_call_json_to_kvs_given_json_string)
{
    // given
    Fit::string kvsStr = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
    // when
    Fit::map<Fit::string, Fit::string> actualKvs;
    Fit::JsonConverterUtil::JsonToMessage(kvsStr, actualKvs);

    // then
    EXPECT_EQ(actualKvs["key1"], kvs_["key1"]);
    EXPECT_EQ(actualKvs["key2"], kvs_["key2"]);
}