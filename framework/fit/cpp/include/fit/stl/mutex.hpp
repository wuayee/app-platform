/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 * Description  :
 * Author       : songyongtan
 * Date         : 2021/4/13
 * Notes:       :
 */

#ifndef FIT_MUTEX_HPP
#define FIT_MUTEX_HPP

#include <mutex>
#include <pthread.h>

namespace Fit {
using std::mutex;
using std::lock_guard;
using std::unique_lock;

void throw_bad_lock();
class bad_lock : public std::exception {
public:
    ~bad_lock() override = default;

    const char *what() const noexcept override
    {
        return "bad lock!";
    };
};

template<typename M>
class shared_lock {
public:
    using mutex_type = M;

    shared_lock() noexcept: pm_(nullptr), owns_(false) {}

    explicit shared_lock(mutex_type &m) : pm_(std::__addressof(m)), owns_(true)
    {
        m.lock_shared();
    }

    shared_lock(mutex_type &m, std::defer_lock_t) noexcept
        : pm_(std::__addressof(m)), owns_(false) {}

    shared_lock(mutex_type &m, std::try_to_lock_t)
        : pm_(std::__addressof(m)), owns_(m.try_lock_shared()) {}

    shared_lock(mutex_type &m, std::adopt_lock_t)
        : pm_(std::__addressof(m)), owns_(true) {}

    ~shared_lock()
    {
        if (owns_) {
            pm_->unlock_shared();
        }
    }

    shared_lock(shared_lock const &) = delete;
    shared_lock &operator=(shared_lock const &) = delete;

    shared_lock(shared_lock &&sl) noexcept: shared_lock()
    {
        swap(sl);
    }

    shared_lock &operator=(shared_lock &&sl) noexcept
    {
        shared_lock(std::move(sl)).swap(*this);
        return *this;
    }

    void lock()
    {
        lockable();
        pm_->lock_shared();
        owns_ = true;
    }

    bool try_lock()
    {
        lockable();
        return owns_ = pm_->try_lock_shared();
    }

    void unlock()
    {
        if (!owns_) {
            throw_bad_lock();
        }
        pm_->unlock_shared();
        owns_ = false;
    }

    void swap(shared_lock &u) noexcept
    {
        std::swap(pm_, u.pm_);
        std::swap(owns_, u.owns_);
    }

    mutex_type *release() noexcept
    {
        owns_ = false;
        auto old = pm_;
        pm_ = nullptr;
        return old;
    }

    bool owns_lock() const noexcept
    {
        return owns_;
    }

    explicit operator bool() const noexcept
    {
        return owns_;
    }

    mutex_type *mutex() const noexcept
    {
        return pm_;
    }

private:
    void lockable() const
    {
        if (pm_ == nullptr || !owns_) {
            throw_bad_lock();
        }
    }

    mutex_type *pm_;
    bool owns_;
};

class shared_mutex_pthread {
    pthread_rwlock_t rwlock_;
public:
    shared_mutex_pthread()
    {
        pthread_rwlock_init(&rwlock_, nullptr);
    }

    ~shared_mutex_pthread()
    {
        pthread_rwlock_destroy(&rwlock_);
    }

    shared_mutex_pthread(const shared_mutex_pthread &) = delete;
    shared_mutex_pthread &operator=(const shared_mutex_pthread &) = delete;

    void lock()
    {
        int ret = pthread_rwlock_wrlock(&rwlock_);
        if (ret != 0) {
            throw_bad_lock();
        }
    }

    bool try_lock()
    {
        int ret = pthread_rwlock_trywrlock(&rwlock_);
        if (ret == EBUSY) {
            return false;
        }
        return true;
    }

    void unlock()
    {
        int ret = pthread_rwlock_unlock(&rwlock_);
        if (ret != 0) {
            throw_bad_lock();
        }
    }

    void lock_shared()
    {
        int ret;
        do {
            ret = pthread_rwlock_rdlock(&rwlock_);
        } while (ret == EAGAIN);
        if (ret == EDEADLK) {
            throw_bad_lock();
        }
    }

    bool try_lock_shared()
    {
        int ret = pthread_rwlock_tryrdlock(&rwlock_);
        if (ret == EBUSY || ret == EAGAIN) {
            return false;
        }
        return true;
    }

    void unlock_shared()
    {
        unlock();
    }

    void *native_handle()
    {
        return &rwlock_;
    }
};

class shared_mutex {
public:
    shared_mutex() = default;
    ~shared_mutex() = default;

    shared_mutex(const shared_mutex &) = delete;
    shared_mutex &operator=(const shared_mutex &) = delete;

    void lock()
    {
        impl_.lock();
    }

    bool try_lock()
    {
        return impl_.try_lock();
    }

    void unlock()
    {
        impl_.unlock();
    }

    void lock_shared()
    {
        impl_.lock_shared();
    }

    bool try_lock_shared()
    {
        return impl_.try_lock_shared();
    }

    void unlock_shared()
    {
        impl_.unlock_shared();
    }

    using native_handle_type = void*;

    native_handle_type native_handle()
    {
        return impl_.native_handle();
    }

private:
    shared_mutex_pthread impl_;
};
}

#endif // FITMutex_HPP
