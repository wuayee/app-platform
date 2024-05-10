/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/22
 * Notes:       :
 */

#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include <fit/internal/util/context/map_context.hpp>

using namespace Fit::Context;
using namespace ::testing;

class MapContextTest : public ::testing::Test {
public:
    void SetUp() override
    {
        ctx_.Put(existKey_, existValue_);
        ctx_.Put("key1", "value1");
        existItemCount_ = 2;
    }

    void TearDown() override {}

    MapContext ctx_;
    Fit::string existKey_ = "key";
    Fit::string existValue_ = "value";
    Fit::string existSerializeString_ = R"({"key":"value","key1":"value1"})";
    uint32_t existItemCount_;
};

TEST_F(MapContextTest, should_return_value_when_Get_given_existed_key)
{
    auto result = ctx_.Get(existKey_);
    EXPECT_THAT(result, Eq(existValue_));
}

TEST_F(MapContextTest, should_return_empty_when_Get_given_nonexisted_key)
{
    auto result = ctx_.Get("XXX");
    EXPECT_THAT(result, Eq(""));
}

TEST_F(MapContextTest, should_return_empty_when_Get_given_removed_key)
{
    ctx_.Remove(existKey_);
    auto result = ctx_.Get(existKey_);
    EXPECT_THAT(result, Eq(""));
}

TEST_F(MapContextTest, should_return_all_values_when_GetAll)
{
    auto all = ctx_.GetAll();
    ASSERT_THAT(all.find(existKey_) != all.end(), Eq(true));
    EXPECT_THAT(all[existKey_], Eq(existValue_));
    EXPECT_THAT(all.size(), Eq(existItemCount_));
}

TEST_F(MapContextTest, should_return_empty_when_IsEmpty_given_empty_item)
{
    ctx_.Reset({});

    EXPECT_THAT(ctx_.IsEmpty(), Eq(true));
}

TEST_F(MapContextTest, should_return_json_string_when_Serialize)
{
    Fit::string result;
    auto ret = ctx_.Serialize(result);

    EXPECT_THAT(ret, Eq(FIT_OK));
    EXPECT_THAT(result, Eq(existSerializeString_));
}

TEST_F(MapContextTest, should_cover_old_items_when_GetAll_after_Deserialize)
{
    Fit::string newItemString = R"({"1":"1"})";
    auto ret = ctx_.Deserialize(newItemString);
    auto all = ctx_.GetAll();

    EXPECT_THAT(ret, Eq(FIT_OK));
    EXPECT_THAT(all.size(), Eq(1));
    EXPECT_EQ(all["1"] == Fit::string("1"), true);
}

TEST_F(MapContextTest, should_return_fail_when_Deserialize_given_wrong_json_string)
{
    Fit::string newItemString = R"(})";
    auto ret = ctx_.Deserialize(newItemString);
    auto all = ctx_.GetAll();

    EXPECT_THAT(ret, Eq(FIT_ERR_DESERIALIZE_JSON));
    EXPECT_THAT(all.size(), Eq(existItemCount_));
}

TEST_F(MapContextTest, should_return_fail_when_Deserialize_given_json_string_value_is_not_string)
{
    Fit::string newItemString = R"({"1":1})";
    auto ret = ctx_.Deserialize(newItemString);
    auto all = ctx_.GetAll();

    EXPECT_THAT(ret, Eq(FIT_ERR_DESERIALIZE_JSON));
    EXPECT_THAT(all.size(), Eq(existItemCount_));
}

TEST_F(MapContextTest, should_return_fail_when_Deserialize_given_json_string_is_not_object)
{
    Fit::string newItemString = R"(["1"])";
    auto ret = ctx_.Deserialize(newItemString);
    auto all = ctx_.GetAll();

    EXPECT_THAT(ret, Eq(FIT_ERR_DESERIALIZE_JSON));
    EXPECT_THAT(all.size(), Eq(existItemCount_));
}
