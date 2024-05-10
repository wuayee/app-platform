/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2021. All rights reserved.
 *
 * Description  : Provides scheduler for tasks.
 * Author       : liangjishi 00298979
 * Date         : 2021/12/21
 */

#ifndef FIT_REGISTRY_LISTENER_TASK_SCHEDULER_HPP
#define FIT_REGISTRY_LISTENER_TASK_SCHEDULER_HPP

#include <functional>
#include <memory>

namespace Fit {
namespace Registry {
namespace Listener {
class Task;
using TaskPtr = std::shared_ptr<Task>;
class TaskScheduler;
using TaskSchedulerPtr = std::shared_ptr<TaskScheduler>;

class Task {
public:
    Task() = default;
    virtual ~Task() = default;

    Task(const Task&) = delete;
    Task(Task&&) = delete;
    Task& operator=(const Task&) = delete;
    Task& operator=(Task&&) = delete;

    virtual void Execute() = 0;

    static TaskPtr Create(std::function<void()> body);
};

/**
 * 为任务提供调度程序。
 */
class TaskScheduler {
public:
    TaskScheduler() = default;
    virtual ~TaskScheduler() = default;

    TaskScheduler(const TaskScheduler&) = delete;
    TaskScheduler(TaskScheduler&&) = delete;
    TaskScheduler& operator=(const TaskScheduler&) = delete;
    TaskScheduler& operator=(TaskScheduler&&) = delete;

    /**
     * 调度指定任务。
     *
     * @param task 表示待调度的任务的执行方法。
     * @param interval 表示调度的间隔的秒数的32位无符号整数。
     */
    virtual void Schedule(TaskPtr task, uint32_t interval) = 0;

    /**
     * 取消任务调度。
     *
     * @param task 表示待取消调度的任务的指针。
     */
    virtual void Unschedule(const TaskPtr& task) = 0;

    /**
     * 关闭调度程序。
     */
    virtual void Shutdown() = 0;

    /**
     * 创建一个任务调度程序的实例，用以在单个线程中调度所有任务。
     *
     * @return 表示指向新创建的任务调度程序新实例的指针。
     */
    static TaskSchedulerPtr WithSingleThread();
};
}
}
}

#endif // FIT_REGISTRY_LISTENER_TASK_SCHEDULER_HPP
