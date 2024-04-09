/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description : Provides definitions for databus task
 */

#ifndef DATABUS_TASK_H
#define DATABUS_TASK_H

#include <cstddef>

namespace DataBus {
namespace Task {

class Task {
public:
    // 在类内部定义TaskType枚举
    enum class TaskType {
        OPEN,
        READ,
        WRITE,
        CLOSE
    };

    explicit Task(TaskType type, int fd) : taskType_(type), clientFd_(fd), data_(nullptr), size_(0) {}
    explicit Task(TaskType type, int fd, const char* data, ssize_t size)
        : taskType_(type), clientFd_(fd), size_(size)
    {
        if (data != nullptr && size > 0) {
            data_ = std::make_unique<char[]>(size);
            std::copy(data, data + size, data_.get());
        }
    }
    // 禁止Task被复制
    Task(const Task&) = delete;
    Task& operator=(const Task&) = delete;

    TaskType Type() const
    {
        return taskType_;
    }

    int ClientFd() const
    {
        return clientFd_;
    }

    const char* DataRaw() const
    {
        return data_.get();
    }

    size_t Size() const
    {
        return size_;
    }
private:
    TaskType taskType_;
    int clientFd_;
    std::unique_ptr<char[]> data_;
    ssize_t size_;
};
}  // Task
}  // DataBus

#endif // DATABUS_TASK_H
