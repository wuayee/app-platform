/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#ifndef FIT_TIMER_H
#define FIT_TIMER_H

#include <algorithm>
#include <atomic>
#include <chrono>
#include <functional>
#include <list>
#include <map>
#include <memory>
#include <utility>
#include <vector>
#include <fit/stl/condition_variable.hpp>
#include "fit_thread_pool.h"

namespace Fit {
class __attribute__ ((visibility ("default"))) timer {
public:
    using timer_handle_t = uint64_t;
    static const timer_handle_t INVALID_TASK_ID;
    static const size_t INCREMENT_STEP;

    static const size_t TIMER_TICK_INTERVAL;
    timer(const timer &) = delete;
    timer(timer &&) = delete;
    timer &operator=(const timer &) = delete;

    timer &operator=(timer &&) = delete;
    using time_point_t = std::chrono::time_point<std::chrono::steady_clock>;
    using time_interval_t = std::chrono::milliseconds;

    class timer_task {
    public:
        // interval ms
        explicit timer_task(std::function<void()> callback,
            time_point_t deadline,
            size_t interval,
            bool repeated,
            timer_handle_t id,
            Fit::Thread::thread_pool *thread_pool)
            : callback_(std::move(callback)), deadline_(deadline),
              interval_(interval), repeated_(repeated), id_(id), thread_pool_(thread_pool) {}

        ~timer_task() = default;

        bool operator<(const timer_task &rhs)
        {
            return deadline_ < rhs.deadline_;
        }

        void set_deadline(time_point_t deadline)
        {
            deadline_ = deadline;
        }

        const std::function<void()> &callback() const
        {
            return callback_;
        }

        time_point_t deadline() const
        {
            return deadline_;
        }

        size_t interval() const
        {
            return interval_;
        }

        timer_handle_t id() const
        {
            return id_;
        }

        bool is_repeated() const
        {
            return repeated_;
        }

        Fit::Thread::thread_pool *attached_thread_pool() const
        {
            return thread_pool_;
        }

    private:
        std::function<void()> callback_;
        time_point_t deadline_;

        // 单位毫秒(ms)
        size_t interval_;

        // true 表示周期性定时器
        bool repeated_;

        timer_handle_t id_;
        Fit::Thread::thread_pool *thread_pool_ {};
    };

    explicit timer(std::shared_ptr<Fit::Thread::thread_pool> pool);

    ~timer();

    template<typename F, typename... Args>
    timer_handle_t set_timeout(size_t milliseconds, F &&f, Args &&... args)
    {
        return add_task(milliseconds, false, internal_pool_.get(), std::forward<F>(f), std::forward<Args>(args)...);
    }

    template<typename F, typename... Args>
    timer_handle_t set_timeout(size_t milliseconds, Fit::Thread::thread_pool *thread_pool, F &&f, Args &&... args)
    {
        return add_task(milliseconds, false, thread_pool, std::forward<F>(f), std::forward<Args>(args)...);
    }

    template<typename F, typename... Args>
    timer_handle_t set_interval(size_t milliseconds, F &&f, Args &&... args)
    {
        return add_task(milliseconds, true, internal_pool_.get(), std::forward<F>(f), std::forward<Args>(args)...);
    }

    template<typename F, typename... Args>
    timer_handle_t set_interval(size_t milliseconds, Fit::Thread::thread_pool *thread_pool, F &&f, Args &&... args)
    {
        return add_task(milliseconds, true, thread_pool, std::forward<F>(f), std::forward<Args>(args)...);
    }

    /**
     * Deprecated, insert or update for timeout
     * @param id
     * @param milliseconds
     * @param f
     * @param args
     */
    template<typename F, typename... Args>
    void insert_or_update_timeout(timer_handle_t id, size_t milliseconds, F &&f, Args &&... args)
    {
        using RetType = typename std::result_of<F(Args...)>::type;
        auto task_func =
            std::make_shared<std::function<RetType()>>(std::bind(std::forward<F>(f), std::forward<Args>(args)...));
        auto executor = [task_func]() { (*task_func)(); };
        insert_or_update_timeout_internal(id, milliseconds, std::move(executor));
    }

    template<typename F, typename... Args>
    void modify(timer_handle_t id, size_t milliseconds, F &&f, Args &&... args)
    {
        using RetType = typename std::result_of<F(Args...)>::type;
        auto task_func =
            std::make_shared<std::function<RetType()>>(std::bind(std::forward<F>(f), std::forward<Args>(args)...));
        auto executor = [task_func]() { (*task_func)(); };
        modify_executor(id, milliseconds, std::move(executor));
    }

    /**
     * modify timer's interval
     * @param id
     * @param milliseconds
     */
    void modify(timer_handle_t id, size_t milliseconds);

    void remove(timer_handle_t id);

    void stop();

private:
    void tick();
    void modify_executor(timer_handle_t id, size_t milliseconds, std::function<void()> executor);
    void insert_or_update_timeout_internal(timer_handle_t id, size_t milliseconds, std::function<void()> executor);

    template<typename F, typename... Args>
    timer_handle_t add_task(size_t interval, bool repeated, Fit::Thread::thread_pool *thread_pool, F &&f,
        Args &&... args)
    {
        using RetType = typename std::result_of<F(Args...)>::type;

        auto id = count_.fetch_add(INCREMENT_STEP);
        auto task_func = std::bind(std::forward<F>(f), std::forward<Args>(args)...);
        auto executor = [task_func]() { task_func(); };

        std::lock_guard<std::mutex> lock(task_mutex_);
        insert(interval, repeated, thread_pool, id, executor);

        return id;
    }

    void insert(size_t interval, bool repeated, Thread::thread_pool *thread_pool, unsigned long id,
        std::function<void()> executor);

    std::shared_ptr<Fit::Thread::thread_pool> internal_pool_;
    std::list<timer_task> tasks_ {};
    std::mutex task_mutex_ {};
    std::atomic_bool clear {false};
    std::atomic<timer_handle_t> count_ {};
    std::mutex cv_mutex_ {};
    steady_condition_variable cv_ {};
};

std::shared_ptr<timer> timer_instance();
}  // namespace Fit
#endif