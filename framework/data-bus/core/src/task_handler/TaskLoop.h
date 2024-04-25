/*
* Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
* Description  : Provides definitions for databus task loop
*/

#ifndef DATABUS_TASKLOOP_H
#define DATABUS_TASKLOOP_H

#include "stl/BlockingQueue.h"
#include "Task.h"

namespace DataBus {
namespace Task {

constexpr int TASK_BACKLOG_MAX = 100000;

class TaskLoop {
public:
    explicit TaskLoop(): taskQueue_(TASK_BACKLOG_MAX) {}

    void AddOpenTask(int socketFd);
    void AddCloseTask(int socketFd);
    void AddReadTask(int socketFd, const char*, size_t len);
    void AddWriteTask(int socketFd, const char*, size_t len);
    std::unique_ptr<Task> GetNextTask();
private:
    DataBus::Stl::BlockingQueue<std::unique_ptr<Task>> taskQueue_;
};
}  // Task
}  // DataBus
#endif // DATABUS_TASKLOOP_H
