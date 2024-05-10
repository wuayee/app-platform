/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/23
 * Notes:       :
 */

#include <fit/internal/util/thread/fit_thread_pool.h>
#include "gtest/gtest.h"

using namespace ::Fit;
using namespace ::Fit::Thread;

class FitThreadPoolTest : public ::testing::Test {
public:
    void SetUp() override {}
    void TearDown() override {}
    thread_pool pool{2};
};

TEST_F(FitThreadPoolTest, should_return_thread_return_value_given_lambda_and_capture_and_params_to_pool)
{
    auto param = 1;
    auto param2 = 2;
    auto result = pool.push([param](int t) { return t + param; }, param2);
    EXPECT_EQ(result.get(), 3);
}

int task_function(int a)
{
    return a + 1;
}

TEST_F(FitThreadPoolTest, should_return_thread_return_value_given_raw_function_pointer)
{
    auto result = pool.push(task_function, 1);
    EXPECT_EQ(result.get(), 2);
}

TEST_F(FitThreadPoolTest, should_return_thread_return_value_given_member_function)
{
    struct foo {
        int member_func(int a)
        {
            return a + b;
        }

        int b = 1;
    };
    foo f;

    auto result = pool.push(&foo::member_func, &f, 1);
    EXPECT_EQ(result.get(), 2);
}

TEST_F(FitThreadPoolTest, should_return_thread_return_value_given_lambda_without_params)
{
    auto result = pool.push([]() { return 1; });
    EXPECT_EQ(result.get(), 1);
}

TEST_F(FitThreadPoolTest, should_return_thread_return_value_given_lambda_with_reference_params)
{
    auto data = 1;
    auto result = pool.push([](int& data) { return ++data; }, std::ref(data));
    EXPECT_EQ(result.get(), 2);
    EXPECT_EQ(data, 2);
}

int global = 1;
void void_func()
{
    global++;
}

TEST_F(FitThreadPoolTest, should_modify_global_var_given_function_without_return_value_or_params)
{
    auto result = pool.push(void_func);
    result.get();
    EXPECT_EQ(global, 2);
}

TEST_F(FitThreadPoolTest, should_return_true_when_resize_given_large_num)
{
    auto result = pool.resize(3);
    EXPECT_EQ(result, true);
}

TEST_F(FitThreadPoolTest, should_return_false_when_resize_given_less_num)
{
    auto result = pool.resize(1);
    EXPECT_EQ(result, false);
}