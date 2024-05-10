/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/2/18
 * Notes:       :
 */

#include <vector>
#include <functional>
#include <fit/value.hpp>

#include <gtest/gtest.h>
#include <gmock/gmock.h>

using namespace ::testing;
using namespace ::Fit;

class ObjectValueTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ObjectValueTest, should_move_value_when_assgin_given_rvalue)
{
    ObjectValue valueSrc {{"bool", true}};
    ObjectValue value = std::move(valueSrc);

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_do_nothing_when_assgin_given_self)
{
    ObjectValue value {{"bool", true}};
    value = value;

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_do_nothing_when_assgin_given_self_rvalue)
{
    ObjectValue valueSrc {{"bool", true}};
    ObjectValue value = std::move(valueSrc);

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_invalid_when_access_given_assgin_with_moved_lvalue)
{
    ObjectValue valueSrc {{"bool", true}};
    ObjectValue valueTemp(std::move(valueSrc));
    ObjectValue value {{"bool", true}};
    value = valueSrc;

    EXPECT_DEATH({
        value["bool"].AsBool();
    }, "");
}

TEST_F(ObjectValueTest, should_invalid_when_access_given_assgin_with_moved_rvalue)
{
    ObjectValue valueSrc {{"bool", true}};
    ObjectValue valueTemp(std::move(valueSrc));
    ObjectValue value {{"bool", true}};
    value = std::move(valueSrc);

    EXPECT_DEATH({
        value["bool"];
    }, "");
}

TEST_F(ObjectValueTest, should_do_nothing_when_assgin_given_self_lvalue)
{
    ObjectValue value {{"bool", true}};
    value = value;

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_do_nothing_when_assgin_given_self_moved_rvalue)
{
    ObjectValue value {{"bool", true}};
    value = std::move(value);

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_copy_value_when_copy_construct_given_rvalue)
{
    ObjectValue valueSrc {"bool", true};
    ObjectValue value(valueSrc);

    EXPECT_THAT(value["bool"].AsBool(), Eq(true));
}

TEST_F(ObjectValueTest, should_get_value_when_construct_given_initializer_list)
{
    ObjectValue value {{"1", 1}, {"2", 2}};

    ASSERT_THAT(value.Size(), Eq(2));
    EXPECT_THAT(value["1"].AsInt32(), Eq(1));
    EXPECT_THAT(value["2"].AsInt32(), Eq(2));
}

TEST_F(ObjectValueTest, should_iter_with_inc_when_for_each_given_const_value)
{
    const ObjectValue value = {{"1", 1}, {"2", 2}};

    auto iter = value.begin();
    ++iter;
    iter++;
    EXPECT_THAT(iter, Eq(value.end()));
}

TEST_F(ObjectValueTest, should_iter_with_dec_when_for_each_given_const_value)
{
    const ObjectValue value = {{"1", 1}, {"2", 2}};

    ConstMemberIterator iter = value.end();
    --iter;
    iter--;
    EXPECT_THAT(iter->Value().AsInt32(), Eq(1));
}

TEST_F(ObjectValueTest, should_iter_with_inc_when_for_each_given_value)
{
    ObjectValue value = {{"1", 1}, {"2", 2}};

    MemberIterator iter = value.begin();
    ++iter;
    iter++;
    EXPECT_THAT(iter, Eq(value.end()));
}

TEST_F(ObjectValueTest, should_iter_with_dec_when_for_each_given_value)
{
    ObjectValue value = {{"1", 1}, {"2", 2}};

    auto iter = value.end();
    --iter;
    iter--;
    EXPECT_THAT(iter->Value().AsInt32(), Eq(1));
}

TEST_F(ObjectValueTest, should_invalid_when_access_null_iter)
{
    ConstMemberIterator constIter;
    MemberIterator iter;

    EXPECT_DEATH(constIter->Value(), "");
    EXPECT_DEATH(iter->Value(), "");
}

TEST_F(ObjectValueTest, should_except_when_construct_given_initializer_list_not_pair)
{
    EXPECT_ANY_THROW((ObjectValue {{"1", 1}, {1, 2, 3}}));
}

TEST_F(ObjectValueTest, should_except_when_construct_given_initializer_list_key_not_string)
{
    EXPECT_ANY_THROW((ObjectValue {{"1", 1}, {2, "2"}}));
}

TEST_F(ObjectValueTest, should_except_when_construct_given_initializer_list_not_array)
{
    EXPECT_ANY_THROW((ObjectValue {{"1", 1}, 2}));
}