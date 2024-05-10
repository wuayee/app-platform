/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#ifndef FIT_CONDITION_VARIABLE_HPP
#define FIT_CONDITION_VARIABLE_HPP

#include <chrono>
#include <condition_variable>

namespace Fit {
class __attribute__ ((visibility ("default"))) steady_condition_variable {
public:
    steady_condition_variable();
    ~steady_condition_variable();
    void notify_one() noexcept;

    void notify_all() noexcept;

    void wait(std::unique_lock<std::mutex>& lock);

    template<typename Rep, typename Period, typename Predicate>
    bool wait_for(std::unique_lock<std::mutex>& lock, const std::chrono::duration<Rep, Period>& rtime,
        Predicate p)
    {
        while (!p()) {
            if (wait_for_internal(lock, rtime) == std::cv_status::timeout) {
                return p();
            }
        }
        return true;
    }
    template<typename Rep, typename Period>
    std::cv_status wait_for(std::unique_lock<std::mutex>& lock, const std::chrono::duration<Rep, Period>& rtime)
    {
        return wait_for_internal(lock, rtime);
    }

    template<typename Duration, typename Predicate>
    bool wait_until(std::unique_lock<std::mutex>& lock,
        const std::chrono::time_point<std::chrono::steady_clock, Duration>& rtime, Predicate p)
    {
        while (!p()) {
            if (wait_for_internal(lock, rtime - std::chrono::steady_clock::now()) == std::cv_status::timeout) {
                return p();
            }
        }
        return true;
    }
    template<typename Duration>
    bool wait_until(std::unique_lock<std::mutex>& lock,
        const std::chrono::time_point<std::chrono::steady_clock, Duration>& rtime)
    {
        return wait_for_internal(lock, rtime - std::chrono::steady_clock::now());
    }

    std::cv_status wait_for_internal(const std::unique_lock<std::mutex> &lock, const std::chrono::nanoseconds& rtime);

    pthread_condattr_t attr_ {};
    pthread_cond_t condition_ {};
};
}

#endif // FIT_CONDITION_VARIABLE_HPP
