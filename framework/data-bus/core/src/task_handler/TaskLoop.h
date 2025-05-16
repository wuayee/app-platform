/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: Provides definitions for databus task loop
 */

#ifndef DATABUS_TASKLOOP_H
#define DATABUS_TASKLOOP_H

#include "stl/BlockingQueue.h"
#include "Task.h"

namespace DataBus {
namespace Task {

constexpr int TASK_BACKLOG_MAX = 100000;
// DataBus内部向TaskLoop投递消息时使用的套接字文件描述符和请求序列号。
constexpr int32_t DATABUS_SOCKET_FD = 0;
constexpr uint32_t DATABUS_INTERNAL_SEQ = 1;

class TaskLoop {
public:
    explicit TaskLoop(): taskQueue_(TASK_BACKLOG_MAX) {}
    virtual ~TaskLoop() = default;

    virtual void AddOpenTask(int socketFd);
    virtual void AddCloseTask(int socketFd);
    virtual void AddReadTask(int socketFd, const char* buffer, size_t len);
    virtual void AddWriteTask(int socketFd, const char* buffer, size_t len);
    virtual std::unique_ptr<Task> GetNextTask();
private:
    DataBus::Stl::BlockingQueue<std::unique_ptr<Task>> taskQueue_;
};
}  // Task
}  // DataBus
#endif // DATABUS_TASKLOOP_H
