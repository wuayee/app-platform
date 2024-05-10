/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2022/1/12
 * Notes:       :
 */

#include <fit/internal/util/thread/fit_timer.h>

namespace Fit {
const timer::timer_handle_t timer::INVALID_TASK_ID = 0xffffffffffffffff;
const size_t timer::INCREMENT_STEP = 1;
const size_t timer::TIMER_TICK_INTERVAL = 1000000;

timer::timer(std::shared_ptr<Fit::Thread::thread_pool> pool) : internal_pool_ {std::move(pool)}
{
    internal_pool_->push(&timer::tick, this);
}

timer::~timer()
{
    stop();
    internal_pool_->stop();
}

void timer::stop()
{
    clear = true;
    cv_.notify_all();

    std::lock_guard<std::mutex> lock {task_mutex_};
    tasks_.clear();
}

void timer::remove(timer_handle_t id)
{
    std::lock_guard<std::mutex> lock(task_mutex_);

    auto pos = std::find_if(tasks_.begin(), tasks_.end(), [id](const timer_task &task) { return task.id() == id; });
    if (pos != tasks_.end()) {
        if (pos == tasks_.begin()) {
            cv_.notify_one();
        }
        tasks_.erase(pos);
    }
}

void timer::insert(size_t interval, bool repeated, Thread::thread_pool *thread_pool, timer_handle_t id,
    std::function<void()> executor)
{
    auto deadline_time = std::chrono::steady_clock::now() + std::chrono::milliseconds(interval);
    auto insert_pos = std::lower_bound(tasks_.begin(),
        tasks_.end(),
        deadline_time,
        [](const timer_task &task, time_point_t time) { return task.deadline() < time; });

    tasks_.emplace(
        insert_pos,
        std::move(executor),
        deadline_time,
        interval,
        repeated,
        id,
        thread_pool);

    if (tasks_.begin()->id() == id) {
        cv_.notify_one();
    }
}

void timer::tick()
{
    while (!clear) {
        auto wait_time = std::chrono::microseconds(TIMER_TICK_INTERVAL);
        {
            std::lock_guard<std::mutex> lock {task_mutex_};
            if (!tasks_.empty()) {
                wait_time = std::chrono::duration_cast<std::chrono::microseconds>(
                    tasks_.begin()->deadline() - std::chrono::steady_clock::now());
            }
        }
        std::unique_lock<std::mutex> cv_lock(cv_mutex_);
        cv_.wait_for(cv_lock, wait_time);

        std::lock_guard<std::mutex> lock {task_mutex_};
        auto time_now = std::chrono::steady_clock::now();

        for (auto it = tasks_.begin(); it != tasks_.end() && it->deadline() <= time_now;) {
            internal_pool_->push(it->callback());

            if (it->is_repeated()) {
                it->set_deadline(time_now + std::chrono::milliseconds(it->interval()));
                auto reinsert_pos = std::lower_bound(it,
                    tasks_.end(),
                    it->deadline(),
                    [](const timer_task &task, time_point_t deadline) { return task.deadline() < deadline; });
                tasks_.insert(reinsert_pos, std::move(*it));
            }
            it = tasks_.erase(it);
        }
    }
}

void timer::modify(timer_handle_t id, size_t milliseconds)
{
    modify_executor(id, milliseconds, nullptr);
}

void timer::modify_executor(timer_handle_t id, size_t milliseconds, std::function<void()> executor)
{
    std::lock_guard<std::mutex> lock(task_mutex_);
    auto pos = std::find_if(tasks_.begin(), tasks_.end(), [id](const timer_task &task) { return task.id() == id; });
    if (pos == tasks_.end()) {
        return;
    }
    auto old = (*pos);
    tasks_.erase(pos);
    if (!executor) {
        executor = old.callback();
    }

    insert(milliseconds, old.is_repeated(), old.attached_thread_pool(), id, std::move(executor));
}

void timer::insert_or_update_timeout_internal(timer::timer_handle_t id, size_t milliseconds,
    std::function<void()> executor)
{
    std::lock_guard<std::mutex> lock(task_mutex_);
    auto pos = std::find_if(tasks_.begin(), tasks_.end(), [id](const timer_task &task) { return task.id() == id; });
    if (pos == tasks_.end()) {
        insert(milliseconds, false, internal_pool_.get(), id, std::move(executor));
        return;
    }
    auto old = (*pos);
    tasks_.erase(pos);
    if (!executor) {
        executor = old.callback();
    }

    insert(milliseconds, false, old.attached_thread_pool(), id, std::move(executor));
}

std::shared_ptr<timer> __attribute__ ((visibility ("default"))) timer_instance()
{
    static auto t = std::make_shared<timer>(Thread::thread_pool_instance());
    return t;
}
}