/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#ifndef FIT_THREAD_POOL_H
#define FIT_THREAD_POOL_H

#include <fit/stl/mutex.hpp>
#include <fit/stl/string.hpp>

#include <algorithm>
#include <condition_variable>
#include <functional>
#include <future>
#include <memory>
#include <queue>
#include <stdexcept>
#include <thread>
#include <utility>
#include <vector>
#include "fit_thread.h"

namespace Fit {
namespace Thread {
using task_func_t = std::function<void()>;
constexpr size_t DEFAULT_THREAD_NUM = 10;

class thread_pool {
public:
    thread_pool(thread_pool&&) = delete;
    thread_pool(const thread_pool&) = delete;
    thread_pool& operator=(thread_pool&&) = delete;
    thread_pool& operator=(const thread_pool&) = delete;

    using pool_type = thread_pool;

    explicit thread_pool(size_t thread_nums = DEFAULT_THREAD_NUM);

    explicit thread_pool(size_t thread_nums, const char *name);
    ~thread_pool();

    template<typename Func, typename... Args>
    auto push(Func&& func, Args&&... args) -> std::future<typename std::result_of<Func(Args...)>::type>
    {
        using RetType = typename std::result_of<Func(Args...)>::type;

        using TaskPromise = std::packaged_task<RetType()>;
        auto task = std::make_shared<TaskPromise>(
            std::bind(std::forward<Func>(func), std::forward<Args>(args)...));
        auto res_future = task->get_future();
        push_internal([task] { (*task)(); });

        return res_future;
    }

    bool resize(size_t new_num);

    void stop();

    void set_name(const char* name);

protected:
    void worker_routine();
    void push_internal(task_func_t task);

private:
    std::vector<fit_thread> workers_;
    std::queue<task_func_t> tasks_;

    mutex task_mutex_;
    mutex threads_mutex_;
    std::condition_variable_any task_condition_;
    bool stop_flag_ {false};
    bool stopped_ {false};
    size_t worker_num_ {};
    Fit::string name_;
};

std::shared_ptr<thread_pool> thread_pool_instance();
}  // namespace Thread
}  // namespace Fit

#endif