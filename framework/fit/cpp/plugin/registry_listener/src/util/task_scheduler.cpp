/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides implementation for task scheduler.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#include <util/task_scheduler.hpp>

#include <fit/stl/vector.hpp>
#include <fit/stl/mutex.hpp>
#include <condition_variable>
#include <thread>

using namespace Fit;
using namespace Fit::Registry::Listener;

namespace {
class FunctionalTask : public virtual Task {
public:
    explicit FunctionalTask(std::function<void()> body) : body_(std::move(body)) {}
    ~FunctionalTask() override = default;
    void Execute() override
    {
        body_();
    }
private:
    std::function<void()> body_;
};

class PeriodicTask {
public:
    explicit PeriodicTask(TaskPtr task, uint32_t interval)
        : task_(std::move(task)), interval_(interval)
    {
        seconds_ = 0;
    }
    ~PeriodicTask() = default;
    void Schedule()
    {
        if (seconds_ < 1) { // LCOV_EXCL_LINE
            task_->Execute();
            seconds_ = interval_;
        } else {
            seconds_--;
        }
    }
    bool Is(const TaskPtr& task) const
    {
        return task == task_;
    }
private:
    TaskPtr task_ {nullptr};
    uint32_t interval_;
    uint32_t seconds_ {};
};

class SingleThreadTaskScheduler : public TaskScheduler {
public:
    SingleThreadTaskScheduler()
    {
        thread_ = std::thread([&]() {
            while (!exit_) {
                ScheduleAll();
            }
        });
    }
    ~SingleThreadTaskScheduler() override
    {
        Shutdown();
    }
    void Schedule(TaskPtr task, uint32_t interval) override
    {
        lock_guard<mutex> guard {mutex_};
        tasks_.push_back(PeriodicTask(std::move(task), interval));
    }
    void Unschedule(const TaskPtr& task) override
    {
        lock_guard<mutex> guard {mutex_};
        for (auto iter = tasks_.begin(); iter != tasks_.end();) { // LCOV_EXCL_LINE
            if (iter->Is(task)) { // LCOV_EXCL_LINE
                tasks_.erase(iter);
                return;
            }
        }
    }
    void Shutdown() override
    {
        exit_ = true;
        conditionVariable_.notify_all();
        if (thread_.joinable()) {
            thread_.join();
        }
    }
private:
    void ScheduleAll()
    {
        unique_lock<mutex> lock {mutex_};
        for (auto& task : tasks_) { // LCOV_EXCL_LINE
            task.Schedule();
        }
        conditionVariable_.wait_for(lock, std::chrono::seconds(1));
    }
    vector<PeriodicTask> tasks_ {};
    std::thread thread_ {};
    Fit::mutex mutex_ {};
    std::condition_variable conditionVariable_ {};
    bool exit_ {false};
};
}

std::shared_ptr<TaskScheduler> TaskScheduler::WithSingleThread()
{
    return std::make_shared<SingleThreadTaskScheduler>();
}

TaskPtr Task::Create(std::function<void()> body)
{
    return std::make_shared<FunctionalTask>(std::move(body));
}
