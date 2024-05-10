/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/02/23
 * Notes:       :
 */

#include <fit/internal/util/thread/fit_timer.h>
#include "gtest/gtest.h"
#include "gmock/gmock.h"

using namespace ::Fit;
using namespace ::Fit::Thread;
using namespace ::testing;

TEST(FitTimerTest, should_execute_chronologically_when_push_different_timeout_task)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    timer.set_timeout(100, [&data]() { data++; });
    timer.set_timeout(200, [&data]() { data++; });

    EXPECT_EQ(data, 1);

    std::this_thread::sleep_for(std::chrono::milliseconds(150));
    EXPECT_EQ(data, 2);

    std::this_thread::sleep_for(std::chrono::milliseconds(200));
    EXPECT_EQ(data, 3);
}

TEST(FitTimerTest, should_execute_intervally_when_push_interval_task)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    timer.set_interval(100, [&data]() { data++; });

    std::this_thread::sleep_for(std::chrono::milliseconds(150));
    EXPECT_EQ(data, 2);

    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    EXPECT_EQ(data, 3);
}

TEST(FitTimerTest, should_not_execute_when_remove_task_before_invoking_it)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    auto id = timer.set_interval(100, [&data]() { data++; });

    std::this_thread::sleep_for(std::chrono::milliseconds(50));
    timer.remove(id);

    std::this_thread::sleep_for(std::chrono::milliseconds(100));

    EXPECT_EQ(data, 1);
}

TEST(FitTimerTest, should_execute_after_new_time_when_modify_before_invoking_it)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    auto id = timer.set_interval(1000, [&data]() { data++; });
    timer.modify(id, 100);

    std::this_thread::sleep_for(std::chrono::milliseconds(150));

    EXPECT_EQ(data, 2);
}

TEST(FitTimerTest, should_execute_after_new_time_when_modify_before_invoking_it_given_time_and_func)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    auto id = timer.set_interval(1000, [&data]() { data++; });
    timer.modify(id, 100, [&data]() { data += 100; });

    std::this_thread::sleep_for(std::chrono::milliseconds(150));

    EXPECT_EQ(data, 101);
}

TEST(FitTimerTest, should_execute_new_func_after_new_time_when_insert_or_update_timeout_before_invoking_it)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    auto id = timer.set_timeout(1000, [&data]() { data++; });
    timer.insert_or_update_timeout(id, 100, [&data]() { data += 100; });

    std::this_thread::sleep_for(std::chrono::milliseconds(150));

    EXPECT_THAT(data, Eq(101));
}

TEST(FitTimerTest, should_add_task_when_insert_or_update_timeout_given_new_id)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    timer.insert_or_update_timeout(2, 100, [&data]() { data += 100; });

    std::this_thread::sleep_for(std::chrono::milliseconds(150));

    EXPECT_THAT(data, Eq(101));
}

TEST(FitTimerTest, should_delay_when_alive_task_given_task_is_doing)
{
    timer timer {std::make_shared<thread_pool>(2)};

    auto data = 1;
    timer.set_interval(10, [&data]() {
        data++;
        std::this_thread::sleep_for(std::chrono::milliseconds(50));
    });

    std::this_thread::sleep_for(std::chrono::milliseconds(30));

    EXPECT_EQ(data, 2);
}