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

class ArrayValueTest : public testing::Test {
public:
    void SetUp() override {}

    void TearDown() override {}
};

TEST_F(ArrayValueTest, should_no_exception_when_Reserve_given_new_size)
{
    Fit::ArrayValue value;

    EXPECT_NO_THROW(value.Reserve(2));
}

TEST_F(ArrayValueTest, should_move_value_when_assgin_given_rvalue)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue value = std::move(valueSrc);

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_do_nothing_when_assgin_given_self)
{
    Fit::ArrayValue value {true};
    value = value;

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_do_nothing_when_assgin_given_self_rvalue)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue value = std::move(valueSrc);

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_invalid_when_access_given_assgin_with_moved_lvalue)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue valueTemp(std::move(valueSrc));
    Fit::ArrayValue value {1};
    value = valueSrc;

    EXPECT_DEATH({
        value[0];
    }, "");
}

TEST_F(ArrayValueTest, should_invalid_when_access_given_assgin_with_moved_rvalue)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue valueTemp(std::move(valueSrc));
    Fit::ArrayValue value {1};
    value = std::move(valueSrc);

    EXPECT_DEATH({
        value[0];
    }, "");
}

TEST_F(ArrayValueTest, should_do_nothing_when_assgin_given_self_lvalue)
{
    Fit::ArrayValue value {true};
    value = value;

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_do_nothing_when_assgin_given_self_moved_rvalue)
{
    Fit::ArrayValue value {true};
    value = std::move(value);

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_copy_value_when_copy_construct_given_rvalue)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue value(valueSrc);

    EXPECT_THAT(value[0].AsBool(), Eq(true));
}

TEST_F(ArrayValueTest, should_get_value_when_construct_given_initializer_list)
{
    Fit::ArrayValue value {1, 2};

    ASSERT_THAT(value.Size(), Eq(2));
    EXPECT_THAT(value[0].AsInt32(), Eq(1));
    EXPECT_THAT(value[1].AsInt32(), Eq(2));
}

TEST_F(ArrayValueTest, should_get_invalid_value_when_copy_construct_given_moved_value)
{
    Fit::ArrayValue valueSrc;
    valueSrc.PushBack(true);
    Fit::ArrayValue valueTemp(std::move(valueSrc));

    EXPECT_NO_THROW(Fit::ArrayValue value(valueSrc));
}

TEST_F(ArrayValueTest, should_no_exception_when_PopBack_given_empty_values)
{
    Fit::ArrayValue valueSrc;

    EXPECT_NO_THROW(valueSrc.PopBack());
}

TEST_F(ArrayValueTest, should_throw_exception_when_get_given_outside_index)
{
    Fit::ArrayValue valueSrc;

    EXPECT_ANY_THROW(valueSrc[0]);
}

TEST_F(ArrayValueTest, should_iter_with_inc_when_for_each_given_const_value)
{
    const Fit::ArrayValue value = {1, 2};

    auto iter = value.begin();
    ++iter;
    iter++;
    EXPECT_THAT(iter, Eq(value.end()));
}

TEST_F(ArrayValueTest, should_iter_with_dec_when_for_each_given_const_value)
{
    const Fit::ArrayValue value = {1, 2};

    Fit::ConstValueIterator iter = value.end();
    --iter;
    iter--;
    EXPECT_THAT(iter->AsInt32(), Eq(1));
}

TEST_F(ArrayValueTest, should_iter_with_inc_when_for_each_given_value)
{
    Fit::ArrayValue value = {1, 2};

    Fit::ValueIterator iter = value.begin();
    ++iter;
    iter++;
    EXPECT_THAT(iter, Eq(value.end()));
}

TEST_F(ArrayValueTest, should_iter_with_dec_when_for_each_given_value)
{
    Fit::ArrayValue value = {1, 2};

    auto iter = value.end();
    --iter;
    iter--;
    EXPECT_THAT(iter->AsInt32(), Eq(1));
}

TEST_F(ArrayValueTest, should_invalid_when_access_null_iter)
{
    Fit::ConstValueIterator constIter;
    Fit::ValueIterator iter;

    EXPECT_DEATH(constIter->AsBool(), "");
    EXPECT_DEATH(iter->AsBool(), "");
}
