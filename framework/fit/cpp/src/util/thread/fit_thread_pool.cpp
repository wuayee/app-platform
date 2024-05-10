/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/13
 * Notes:       :
 */

#include <fit/internal/util/thread/fit_thread_pool.h>

namespace Fit {
namespace Thread {
thread_pool::thread_pool(size_t thread_nums)
    : thread_pool(thread_nums, "fit.thr.pool") {}

thread_pool::thread_pool(size_t thread_nums, const char *name)
    : worker_num_ {thread_nums}, name_(name)
{
    for (auto count = 0U; count < thread_nums; ++count) {
        workers_.emplace_back(&thread_pool::worker_routine, this);
        workers_.back().set_name((name_ + Fit::to_string(count)).c_str());
    }
}

thread_pool::~thread_pool()
{
    if (!stopped_) {
        stop();
    }
}

void thread_pool::worker_routine()
{
    while (true) {
        std::unique_lock<mutex> lock(task_mutex_);
        task_condition_.wait(lock,
            [this] { return stop_flag_ || !tasks_.empty(); });

        if (stop_flag_ && tasks_.empty()) {
            return;
        }

        task_func_t task {std::move(tasks_.front())};
        tasks_.pop();
        lock.unlock();

        task();
    }
}

bool thread_pool::resize(size_t new_num)
{
    std::lock_guard<mutex> lock {threads_mutex_};
    if (new_num > worker_num_) {
        for (auto i = worker_num_; i < new_num; ++i) {
            workers_.emplace_back(&thread_pool::worker_routine, this);
            workers_.back().set_name((name_ + Fit::to_string(i)).c_str());
        }
    } else if (new_num < worker_num_) {
        return false;
    }

    worker_num_ = new_num;
    return true;
}

void thread_pool::stop()
{
    {
        std::lock_guard<mutex> lock {task_mutex_};
        stop_flag_ = true;
    }
    task_condition_.notify_all();

    for (auto &worker : workers_) {
        worker.join();
    }

    stopped_ = true;
}

void thread_pool::push_internal(task_func_t task)
{
    {
        std::lock_guard<mutex> lock(task_mutex_);
        tasks_.emplace(std::move(task));
    }
    task_condition_.notify_one();
}

void thread_pool::set_name(const char *name)
{
    std::lock_guard<mutex> lock {threads_mutex_};
    int32_t count = 0;
    for (auto &worker : workers_) {
        Fit::string header = name;
        worker.set_name((header + Fit::to_string(count++)).c_str());
    }
}

std::shared_ptr<thread_pool> thread_pool_instance()
{
    static auto pool = std::make_shared<thread_pool>(DEFAULT_THREAD_NUM);
    return pool;
}
}
}
