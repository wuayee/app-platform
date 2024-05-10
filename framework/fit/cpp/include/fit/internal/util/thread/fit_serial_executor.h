/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       :
 * Date         :
 * Notes:       :
 */
#ifndef FIT_SERIAL_EXECUTOR_H
#define FIT_SERIAL_EXECUTOR_H

#include <memory>
#include <queue>
#include <utility>
#include <vector>
#include <fit/stl/mutex.hpp>
#include "fit_thread_pool.h"

namespace Fit {
namespace Thread {
class serial_executor {
public:
    using pool_t = thread_pool;

    explicit serial_executor(std::shared_ptr<pool_t> pool) : internal_pool_ {pool} {}
    ~serial_executor() {}
    template<typename F, typename... Args>
    auto execute(F&& f, Args&&... args) -> std::future<typename std::result_of<F(Args...)>::type>
    {
        using ret_t = typename std::result_of<F(Args...)>::type;

        auto task =
            std::make_shared<std::packaged_task<ret_t()>>(std::bind(std::forward<F>(f), std::forward<Args>(args)...));
        auto res = task->get_future();

        {
            std::lock_guard<mutex> lock {queue_mutex_};
            serial_tasks_.emplace([task, this] {
                (*task)();
                this->scheduleNext();
            });
        }

        if (!active) {
            scheduleNext();
        }
        return res;
    }

    void clear()
    {
        {
            std::lock_guard<mutex> lock {queue_mutex_};
            std::queue<task_func_t> emptyTasks;
            serial_tasks_.swap(emptyTasks);
        }
        internal_pool_->stop();
    }

    size_t task_num()
    {
        std::lock_guard<mutex> lock {queue_mutex_};
        return serial_tasks_.size();
    }
private:
    void scheduleNext()
    {
        task_func_t task {};

        std::unique_lock<mutex> lock {queue_mutex_};

        if (!serial_tasks_.empty()) {
            task = std::move(serial_tasks_.front());
            serial_tasks_.pop();
            active = true;
        } else {
            active = false;
        }

        lock.unlock();

        if (task) {
            internal_pool_->push(std::move(task));
        }
    }

    std::shared_ptr<pool_t> internal_pool_ {};
    std::queue<task_func_t> serial_tasks_ {};
    mutex queue_mutex_ {};
    std::atomic<bool> active {false};
};
}  // namespace thread
}  // namespace Fit

#endif