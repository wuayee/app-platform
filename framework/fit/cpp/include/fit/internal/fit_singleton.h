/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 * Description: base singleton
 * Author: w00561424
 * Date: 2020-07-15
 */
#ifndef FIT_SINGLETON_H
#define FIT_SINGLETON_H
#include <mutex>
#include <memory>
#include <atomic>
namespace Fit {
template<typename T>
class fit_singleton {
public:
    fit_singleton(const fit_singleton&) = delete;
    fit_singleton& operator=(const fit_singleton&) = delete;
    static T* get_instance()
    {
        T* tmp = instance_.load(std::memory_order_acquire);
        if (!tmp) {
            std::lock_guard<std::mutex> lock(mutex_);
            tmp = instance_.load(std::memory_order_relaxed);
            if (!tmp) {
                tmp = new T();
                instance_.store(tmp, std::memory_order_release);
            }
        }
        return tmp;
    };
protected:
    fit_singleton() = default;
private:
    static std::mutex mutex_;
    static std::atomic<T*> instance_;
};

template<class T>
std::atomic<T*> fit_singleton<T>::instance_ { nullptr };

template<class T>
std::mutex fit_singleton<T>::mutex_;
}
#endif