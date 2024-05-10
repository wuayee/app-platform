/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#include <fit/stl/condition_variable.hpp>

namespace Fit {
steady_condition_variable::steady_condition_variable()
{
    pthread_condattr_init(&attr_);
    pthread_condattr_setclock(&attr_, CLOCK_MONOTONIC);
    pthread_cond_init(&condition_, &attr_);
}

steady_condition_variable::~steady_condition_variable()
{
    pthread_cond_destroy(&condition_);
}

void steady_condition_variable::notify_one() noexcept
{
    pthread_cond_signal(&condition_);
}

void steady_condition_variable::notify_all() noexcept
{
    pthread_cond_broadcast(&condition_);
}

std::cv_status steady_condition_variable::wait_for_internal(const std::unique_lock<std::mutex> &lock,
    const std::chrono::nanoseconds &rtime)
{
    auto ns = rtime.count();
    struct timespec ts {};
    clock_gettime(CLOCK_MONOTONIC, &ts);

    const auto ns_per_second = std::chrono::nanoseconds(std::chrono::seconds(1)).count();

    ts.tv_sec += ns / ns_per_second;
    ts.tv_nsec += ns % ns_per_second;
    if (ts.tv_nsec >= ns_per_second) {
        ts.tv_sec += 1;
        ts.tv_nsec -= ns_per_second;
    }
    int rc = pthread_cond_timedwait(&condition_, lock.mutex()->native_handle(), &ts);
    if (rc != ETIMEDOUT) {
        return std::cv_status::no_timeout;
    }
    return std::cv_status::timeout;
}

void steady_condition_variable::wait(std::unique_lock<std::mutex> &lock)
{
    pthread_cond_wait(&condition_, lock.mutex()->native_handle());
}
}