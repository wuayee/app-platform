/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 * Description  : Provides template definitions for blocking queue.
 */

#ifndef DATABUS_BLOCKINGQUEUE_H
#define DATABUS_BLOCKINGQUEUE_H

#include <queue>
#include <memory>
#include <mutex>
#include <condition_variable>

namespace DataBus {
namespace Stl {

template <typename T>
class BlockingQueue {
public:
    explicit BlockingQueue(size_t capacity) : capacity_(capacity) {}

    template <typename U>
    void enqueue(U&& item)
    {
        std::unique_lock<std::mutex> lock(mutex_);
        condVar_.wait(lock, [this]() {
            return queue_.size() < capacity_;
        });
        queue_.push(std::forward<U>(item));
        condVar_.notify_one();
    }

    T dequeue()
    {
        std::unique_lock<std::mutex> lock(mutex_);
        condVar_.wait(lock, [this]() {
            return !queue_.empty();
        });
        T item = std::move(queue_.front());
        queue_.pop();
        condVar_.notify_one();
        return item;
    }

    bool empty()
    {
        std::lock_guard<std::mutex> lock(mutex_);
        return queue_.empty();
    }

    size_t size()
    {
        std::lock_guard<std::mutex> lock(mutex_);
        return queue_.size();
    }

    size_t capacity() const
    {
        return capacity_;
    }
private:
    std::queue<T> queue_;
    std::mutex mutex_;
    std::condition_variable condVar_;
    size_t capacity_;
};

} // Stl
} // DataBus

#endif // DATABUS_BLOCKINGQUEUE_H
