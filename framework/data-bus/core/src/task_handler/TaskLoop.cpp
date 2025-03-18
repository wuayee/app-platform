/**
 * Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 * This file is a part of the ModelEngine Project.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 */

/**
 * Description: Provides implementation for databus task loop
 */

#include "TaskLoop.h"

using namespace std;
using namespace DataBus::Task;

namespace DataBus {
namespace Task {

void TaskLoop::AddOpenTask(int socketFd)
{
    taskQueue_.enqueue(make_unique<Task>(Task::TaskType::OPEN, socketFd));
}

void TaskLoop::AddCloseTask(int socketFd)
{
    taskQueue_.enqueue(make_unique<Task>(Task::TaskType::CLOSE, socketFd));
}

void TaskLoop::AddReadTask(int socketFd, const char* buffer, size_t len)
{
    taskQueue_.enqueue(make_unique<Task>(Task::TaskType::READ, socketFd, buffer, len));
}

void TaskLoop::AddWriteTask(int socketFd, const char* buffer, size_t len)
{
    taskQueue_.enqueue(make_unique<Task>(Task::TaskType::WRITE, socketFd, buffer, len));
}

unique_ptr<Task> TaskLoop::GetNextTask()
{
    return taskQueue_.dequeue();
}
}  // Task
}  // DataBus

